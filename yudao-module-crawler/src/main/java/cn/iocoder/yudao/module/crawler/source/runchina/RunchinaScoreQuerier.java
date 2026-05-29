package cn.iocoder.yudao.module.crawler.source.runchina;

import cn.iocoder.yudao.module.crawler.config.CrawlerProperties;
import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国马拉松官网个人成绩查询（Playwright 浏览器自动化）。
 *
 * 通过 runchina.org.cn 公开成绩查询页获取个人完赛记录。
 * 仅供按需查询，不做批量爬取。
 */
@Component
@Slf4j
public class RunchinaScoreQuerier {

    private static final String HOMEPAGE = "https://www.runchina.org.cn/";
    private static final String SCORE_PAGE = "https://www.runchina.org.cn/#/data-score/public-score/list";
    private static final String API_BASE = "api-changzheng.chinaath.com";
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+):(\\d{2}):(\\d{2})");

    private final CrawlerProperties properties;
    private final ObjectMapper objectMapper;
    private Playwright playwright;
    private Browser browser;

    public RunchinaScoreQuerier(CrawlerProperties properties) {
        this.properties = properties;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    void init() {
        playwright = Playwright.create();
        var headless = properties.getPlaywright().isHeadless();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setArgs(List.of(
                        "--no-sandbox",
                        "--disable-blink-features=AutomationControlled",
                        "--disable-features=IsolateOrigins,site-per-process")));
        log.info("[RunchinaScoreQuerier] Playwright initialized (headless={})", headless);
    }

    @PreDestroy
    void destroy() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        log.info("[RunchinaScoreQuerier] Playwright closed");
    }

    /**
     * 根据姓名 + 证件号查询个人成绩。
     *
     * @return 爬取结果列表（字段 key 与 ResultPipeline.buildResultDO 保持一致）
     * @throws CaptchaRequiredException 如果遇到滑块验证码
     */
    public List<CrawlResult> query(String name, String idCard) {
        List<CrawlResult> results = new ArrayList<>();
        List<JsonNode> capturedApis = new CopyOnWriteArrayList<>();

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .setViewportSize(1440, 900)
                .setLocale("zh-CN"));

        Page page = context.newPage();

        // 反检测脚本
        page.addInitScript("""
                delete Object.getPrototypeOf(navigator).webdriver;
                Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });
                window.chrome = { runtime: {}, app: {} };
                """);

        // 拦截 API 响应
        page.onResponse(response -> {
            String ct = response.headers().get("content-type");
            String url = response.url();
            if (ct != null && ct.contains("json") && url.contains(API_BASE)) {
                try {
                    String body = response.text();
                    JsonNode node = objectMapper.readTree(body);
                    capturedApis.add(node);
                    String path = url.contains("/api/") ? url.substring(url.indexOf("/api/")) : url;
                    log.debug("[Runchina] API {} {}: {}", response.status(), path,
                            body.length() > 300 ? body.substring(0, 300) : body);
                } catch (Exception ignored) {
                }
            }
        });

        try {
            // Step 1: 加载首页（过 CDN 检查）
            log.info("[Runchina] Loading homepage...");
            try {
                page.navigate(HOMEPAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Homepage load timeout (may be OK): {}", e.getMessage());
            }
            page.waitForTimeout(5000);

            // Step 2: 导航到成绩查询页
            log.info("[Runchina] Navigating to score query page...");
            try {
                page.navigate(SCORE_PAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Score page load timeout: {}", e.getMessage());
            }
            page.waitForTimeout(8000);

            // Step 3: 填写表单
            log.info("[Runchina] Filling form: name={}, idCard={}***", name, idCard != null ? idCard.substring(0, Math.min(4, idCard.length())) : "null");
            fillInputByPlaceholder(page, "姓名", name);
            if (idCard != null && !idCard.isBlank()) {
                fillInputByPlaceholder(page, "证件号码", idCard);
                // 也尝试"身份证"placeholder
                fillInputByPlaceholder(page, "身份证", idCard);
            }
            page.waitForTimeout(1000);

            // Step 4: 检测验证码
            if (hasCaptcha(page)) {
                log.warn("[Runchina] Captcha detected, cannot proceed automatically");
                throw new CaptchaRequiredException("遇到滑块验证码，请手动在浏览器中完成查询");
            }

            // Step 5: 提交查询
            log.info("[Runchina] Submitting query...");
            clickButton(page, "查询");
            page.waitForTimeout(8000);

            // 再等一会，让 API 响应全部到达
            page.waitForTimeout(3000);

        } finally {
            context.close();
        }

        // Step 6: 解析捕获的 API 响应
        for (JsonNode apiResp : capturedApis) {
            List<CrawlResult> parsed = parseApiResponse(apiResp, name);
            results.addAll(parsed);
        }

        log.info("[Runchina] Query complete: {} results from {} API responses", results.size(), capturedApis.size());
        return results;
    }

    // ===== Helper methods =====

    private void fillInputByPlaceholder(Page page, String placeholder, String value) {
        try {
            Locator input = page.locator("input[placeholder*='" + placeholder + "']").first();
            if (input.count() > 0) {
                input.fill(value);
                log.debug("[Runchina] Filled '{}' with value", placeholder);
            }
        } catch (Exception e) {
            log.debug("[Runchina] Could not fill '{}': {}", placeholder, e.getMessage());
        }
    }

    private boolean hasCaptcha(Page page) {
        String[] selectors = {
                "iframe[src*=captcha]", "iframe[src*=ncaptcha]", "iframe[src*=aliyun]",
                ".nc_wrapper", ".nc-container", "#nc_1_n1z",
                "[class*=captcha]", "[id*=captcha]", "#tcaptcha_iframe",
        };
        for (String sel : selectors) {
            try {
                if (page.locator(sel).count() > 0) {
                    log.info("[Runchina] Captcha element found: {}", sel);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private void clickButton(Page page, String text) {
        try {
            Locator btn = page.locator("button:has-text('" + text + "')").first();
            if (btn.count() > 0) {
                btn.click();
                return;
            }
        } catch (Exception ignored) {
        }
        // Fallback: span/div with text
        for (String sel : List.of("span:has-text('" + text + "')", "div:has-text('" + text + "')")) {
            try {
                Locator el = page.locator(sel).first();
                if (el.count() > 0) {
                    el.click();
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        // Last resort: Enter key
        page.keyboard().press("Enter");
    }

    /**
     * 解析 API 响应中的成绩数据。
     * 尝试多种可能的 JSON 结构。
     */
    private List<CrawlResult> parseApiResponse(JsonNode root, String queryName) {
        List<CrawlResult> results = new ArrayList<>();
        JsonNode code = root.get("code");
        if (code == null) return results;

        // 检查是否成功响应
        int codeVal = code.asInt(-1);
        if (codeVal != 0 && codeVal != 200) return results;

        // 尝试多种 data 路径
        List<JsonNode> rows = new ArrayList<>();
        JsonNode data = root.get("data");
        if (data != null) {
            if (data.isArray()) {
                rows.addAll(safeArray(data));
            } else {
                // data.results / data.rows / data.records / data.list
                for (String key : List.of("results", "rows", "records", "list", "data")) {
                    JsonNode arr = data.get(key);
                    if (arr != null && arr.isArray()) {
                        rows.addAll(safeArray(arr));
                    }
                }
                // 也可能 data 本身就是单条记录
                if (rows.isEmpty() && data.has("name") || data.has("raceName")) {
                    rows.add(data);
                }
            }
        }

        for (JsonNode row : rows) {
            try {
                CrawlResult result = mapToCrawlResult(row);
                if (result != null) {
                    results.add(result);
                }
            } catch (Exception e) {
                log.debug("[Runchina] Failed to map row: {}", e.getMessage());
            }
        }

        // 如果 API 没有返回结构化数据，尝试从 raw 响应中提取
        if (results.isEmpty() && data != null && data.size() > 0) {
            CrawlResult fallback = mapToCrawlResult(data);
            if (fallback != null) results.add(fallback);
        }

        return results;
    }

    private List<JsonNode> safeArray(JsonNode arr) {
        List<JsonNode> list = new ArrayList<>();
        if (arr != null && arr.isArray()) {
            arr.forEach(list::add);
        }
        return list;
    }

    private CrawlResult mapToCrawlResult(JsonNode row) {
        Map<String, Object> fields = new HashMap<>();

        // 选手信息
        String name = textOrNull(row, "name", "realName", "athleteName", "playerName");
        if (name == null || name.isBlank()) return null;
        fields.put("name", name);

        String idCard = textOrNull(row, "idCard", "idNumber", "certNo", "cardNo", "certificateNo");
        if (idCard != null) fields.put("id_card", idCard);

        String bibNumber = textOrNull(row, "bibNumber", "bibNo", "raceNumber", "competitionNumber");
        if (bibNumber != null) fields.put("bib_number", bibNumber);

        String gender = textOrNull(row, "gender", "sex", "genderName");
        if (gender != null) fields.put("gender", gender);

        String nationality = textOrNull(row, "nationality", "nation", "country");
        if (nationality != null) fields.put("nationality", nationality);

        // 赛事信息
        String gameName = textOrNull(row, "raceName", "competitionName", "matchName", "eventName", "gameName");
        if (gameName != null) fields.put("game_name", gameName);

        String gameDate = textOrNull(row, "raceTime", "raceDate", "competitionDate", "matchDate", "gameDate");
        if (gameDate != null) fields.put("game_date", gameDate);

        // 成绩信息
        fields.put("gun_time_ms", parseTimeMs(textOrNull(row, "gunTime", "gunScore", "grossTime", "totalTime")));
        fields.put("net_time_ms", parseTimeMs(textOrNull(row, "netTime", "netScore", "chipTime", "cleanTime")));

        Integer rank = intOrNull(row, "rank", "overallRank", "totalRank", "overallPlace");
        if (rank != null) fields.put("rank", rank);

        Integer genderRank = intOrNull(row, "genderRank", "sexRank", "genderPlace");
        if (genderRank != null) fields.put("gender_rank", genderRank);

        Integer categoryRank = intOrNull(row, "categoryRank", "groupRank", "categoryPlace");
        if (categoryRank != null) fields.put("category_rank", categoryRank);

        // 组别
        String ageGroup = textOrNull(row, "ageGroup", "groupName", "categoryName");
        if (ageGroup != null) fields.put("age_group", ageGroup);

        return CrawlResult.builder()
                .sourceKey("runchina_personal")
                .sourceGameId(textOrNull(row, "raceId", "matchId", "competitionId", "eventId"))
                .fields(fields)
                .rawData(row.toString())
                .build();
    }

    private String textOrNull(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode val = node.get(key);
            if (val != null && !val.isNull()) {
                String text = val.asText();
                if (!text.isBlank()) return text;
            }
        }
        return null;
    }

    private Integer intOrNull(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode val = node.get(key);
            if (val != null && !val.isNull()) {
                int v = val.asInt(-1);
                if (v >= 0) return v;
            }
        }
        return null;
    }

    /**
     * 解析 "H:MM:SS" 或 "MM:SS" 格式时间为毫秒。
     */
    private Long parseTimeMs(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        Matcher m = TIME_PATTERN.matcher(timeStr.trim());
        if (m.find()) {
            long h = Long.parseLong(m.group(1));
            long min = Long.parseLong(m.group(2));
            long sec = Long.parseLong(m.group(3));
            return (h * 3600 + min * 60 + sec) * 1000;
        }
        // 也可能直接是秒数
        try {
            double seconds = Double.parseDouble(timeStr.trim());
            return (long) (seconds * 1000);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
