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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国马拉松官网个人成绩查询（Playwright 浏览器自动化）。
 *
 * 支持单次查询和批量查询（同一 Session 共用验证码）。
 */
@Component
@Slf4j
public class RunchinaScoreQuerier {

    private static final String HOMEPAGE = "https://www.runchina.org.cn/";
    private static final String SCORE_PAGE = "https://www.runchina.org.cn/#/data-score/public-score/list";
    private static final String API_BASE = "api-changzheng.chinaath.com";
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+):(\\d{2}):(\\d{2})");

    /** 批量模式下等待用户手动完成验证码的超时（秒） */
    private static final int CAPTCHA_WAIT_SECONDS = 120;

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

    // ============================================================
    // Batch mode — 复用同一 Session，手动过一次验证码后批量查询
    // ============================================================

    /**
     * 批量查询 session 上下文。
     * 调用方先 startBatchSession()，然后多次 queryInSession()，最后 closeBatchSession()。
     */
    public static class BatchSession implements AutoCloseable {
        private final BrowserContext context;
        private final Page page;
        private final List<JsonNode> capturedApis;
        private boolean captchaResolved;
        private final RunchinaScoreQuerier querier;

        BatchSession(BrowserContext context, Page page, RunchinaScoreQuerier querier) {
            this.context = context;
            this.page = page;
            this.capturedApis = new CopyOnWriteArrayList<>();
            this.querier = querier;
        }

        public boolean isCaptchaResolved() { return captchaResolved; }
        public void setCaptchaResolved(boolean v) { this.captchaResolved = v; }
        public Page getPage() { return page; }
        public List<JsonNode> getCapturedApis() { return capturedApis; }
        public RunchinaScoreQuerier getQuerier() { return querier; }

        @Override
        public void close() {
            context.close();
            log.info("[RunchinaScoreQuerier] BatchSession closed");
        }
    }

    /**
     * 开始批量查询 session。
     *
     * 打开浏览器，导航到成绩查询页，等待用户完成滑块验证码。
     * 如果在 headless 模式下遇到验证码则直接抛异常。
     *
     * @return BatchSession，用完需 close()
     * @throws CaptchaRequiredException 如果 headless 模式且遇到验证码，或等待超时
     */
    public BatchSession startBatchSession() {
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

        BatchSession session = new BatchSession(context, page, this);

        // 拦截 API 响应
        page.onResponse(response -> {
            String ct = response.headers().get("content-type");
            String url = response.url();
            if (ct != null && ct.contains("json") && url.contains(API_BASE)) {
                try {
                    String body = response.text();
                    JsonNode node = objectMapper.readTree(body);
                    session.getCapturedApis().add(node);
                    log.debug("[Runchina] API {} {}: {} chars", response.status(),
                            url.contains("/api/") ? url.substring(url.indexOf("/api/")) : url, body.length());
                } catch (Exception ignored) {
                }
            }
        });

        try {
            // Step 1: 加载首页
            log.info("[Runchina] Batch: loading homepage...");
            try {
                page.navigate(HOMEPAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Homepage load timeout: {}", e.getMessage());
            }
            page.waitForTimeout(5000);

            // Step 2: 导航到成绩查询页
            log.info("[Runchina] Batch: navigating to score query page...");
            try {
                page.navigate(SCORE_PAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Score page load timeout: {}", e.getMessage());
            }
            page.waitForTimeout(8000);

            // Step 3: 等待用户手动完成滑块验证码
            if (hasCaptcha(page)) {
                boolean headless = properties.getPlaywright().isHeadless();
                if (headless) {
                    throw new CaptchaRequiredException("Headless 模式遇到滑块验证码，无法自动处理。请设置 headless=false 后手动完成。");
                }

                log.info("[Runchina] Batch: 检测到验证码，等待用户手动完成（最多 {} 秒）...", CAPTCHA_WAIT_SECONDS);
                boolean resolved = waitForCaptchaResolved(page, CAPTCHA_WAIT_SECONDS);
                if (!resolved) {
                    throw new CaptchaRequiredException("等待验证码完成超时（" + CAPTCHA_WAIT_SECONDS + " 秒），请重试");
                }
                log.info("[Runchina] Batch: 验证码已通过 ✅");
            } else {
                log.info("[Runchina] Batch: 未检测到验证码，可直接查询");
            }
            session.setCaptchaResolved(true);

        } catch (CaptchaRequiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Runchina] Batch: session setup failed", e);
            session.close();
            throw new RuntimeException("批量查询初始化失败: " + e.getMessage(), e);
        }

        return session;
    }

    /**
     * 在已有 session 中查询一个人的成绩。
     *
     * @param session 已创建的批量 session
     * @param name    姓名
     * @param idCard  身份证号（可选）
     * @return 爬取结果列表
     */
    public List<CrawlResult> queryInSession(BatchSession session, String name, String idCard) {
        Page page = session.getPage();
        List<CrawlResult> results = new ArrayList<>();

        // 清空上一个查询的 API 缓存
        session.getCapturedApis().clear();

        log.info("[Runchina] Batch query: name={}, idCard={}***",
                name, idCard != null ? mask(idCard) : "null");

        try {
            // 填写表单
            fillInputByPlaceholder(page, "姓名", name);
            if (idCard != null && !idCard.isBlank()) {
                fillInputByPlaceholder(page, "证件号码", idCard);
                fillInputByPlaceholder(page, "身份证", idCard);
            }
            page.waitForTimeout(500);

            // 再检查一次验证码（可能已过期）
            if (hasCaptcha(page) && !session.isCaptchaResolved()) {
                boolean headless = properties.getPlaywright().isHeadless();
                if (headless) {
                    throw new CaptchaRequiredException("Headless 模式遇到滑块验证码，无法自动处理");
                }
                log.info("[Runchina] 验证码重新出现，等待手动完成...");
                boolean resolved = waitForCaptchaResolved(page, 60);
                if (!resolved) {
                    throw new CaptchaRequiredException("等待验证码超时");
                }
                session.setCaptchaResolved(true);
            }

            // 提交查询
            log.info("[Runchina] Submitting query for {}", name);
            clickButton(page, "查询");
            page.waitForTimeout(8000);

            // 等待 API 响应
            page.waitForTimeout(3000);

        } catch (CaptchaRequiredException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Runchina] Query failed for {}", name, e);
        }

        // 解析捕获的 API 响应
        for (JsonNode apiResp : session.getCapturedApis()) {
            List<CrawlResult> parsed = parseApiResponse(apiResp, name);
            results.addAll(parsed);
        }

        log.info("[Runchina] Batch query {}: {} results from {} API responses",
                name, results.size(), session.getCapturedApis().size());
        return results;
    }

    /**
     * 批量查询多人（便利方法：打开 session → 逐人查询 → 关闭 session）。
     *
     * @param requests 查询请求列表 [{name, idCard}, ...]
     * @return 所有人结果汇总
     */
    public List<CrawlResult> batchQuery(List<String[]> requests) {
        List<CrawlResult> allResults = new ArrayList<>();
        try (BatchSession session = startBatchSession()) {
            for (String[] req : requests) {
                String name = req[0];
                String idCard = req.length > 1 ? req[1] : null;
                try {
                    List<CrawlResult> personResults = queryInSession(session, name, idCard);
                    allResults.addAll(personResults);
                } catch (CaptchaRequiredException e) {
                    log.warn("[Runchina] 验证码中断，已查询 {} 人，剩余跳过", allResults.size());
                    break;
                }
                // 每次查询后稍等
                pageWait(session.getPage(), 2000);
            }
        } catch (Exception e) {
            log.error("[Runchina] Batch query failed", e);
        }
        return allResults;
    }

    // ============================================================
    // Single mode — 每次查询都独立打开浏览器
    // ============================================================

    /**
     * 单次查询（为保持兼容性保留，新调用推荐用 batchQuery）。
     */
    public List<CrawlResult> query(String name, String idCard) {
        List<CrawlResult> results = new ArrayList<>();
        List<JsonNode> capturedApis = new CopyOnWriteArrayList<>();

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .setViewportSize(1440, 900)
                .setLocale("zh-CN"));

        Page page = context.newPage();

        page.addInitScript("""
                delete Object.getPrototypeOf(navigator).webdriver;
                Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });
                window.chrome = { runtime: {}, app: {} };
                """);

        page.onResponse(response -> {
            String ct = response.headers().get("content-type");
            String url = response.url();
            if (ct != null && ct.contains("json") && url.contains(API_BASE)) {
                try {
                    String body = response.text();
                    JsonNode node = objectMapper.readTree(body);
                    capturedApis.add(node);
                    log.debug("[Runchina] API {}: {} chars", response.status(), body.length());
                } catch (Exception ignored) {
                }
            }
        });

        try {
            try {
                page.navigate(HOMEPAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Homepage load timeout: {}", e.getMessage());
            }
            page.waitForTimeout(5000);

            try {
                page.navigate(SCORE_PAGE, new Page.NavigateOptions().setTimeout(30000));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            } catch (Exception e) {
                log.warn("[Runchina] Score page load timeout: {}", e.getMessage());
            }
            page.waitForTimeout(8000);

            fillInputByPlaceholder(page, "姓名", name);
            if (idCard != null && !idCard.isBlank()) {
                fillInputByPlaceholder(page, "证件号码", idCard);
                fillInputByPlaceholder(page, "身份证", idCard);
            }
            page.waitForTimeout(1000);

            if (hasCaptcha(page)) {
                log.warn("[Runchina] Captcha detected, cannot proceed automatically");
                throw new CaptchaRequiredException("遇到滑块验证码，请手动在浏览器中完成查询");
            }

            clickButton(page, "查询");
            page.waitForTimeout(8000);
            page.waitForTimeout(3000);

        } finally {
            context.close();
        }

        for (JsonNode apiResp : capturedApis) {
            List<CrawlResult> parsed = parseApiResponse(apiResp, name);
            results.addAll(parsed);
        }

        log.info("[Runchina] Query complete: {} results from {} API responses", results.size(), capturedApis.size());
        return results;
    }

    // ============================================================
    // Helper methods
    // ============================================================

    /**
     * 轮询等待用户手动完成滑块验证码。
     *
     * @return true = 验证码已消失，false = 超时
     */
    private boolean waitForCaptchaResolved(Page page, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (!hasCaptcha(page)) {
                return true;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return !hasCaptcha(page); // 最后一次检查
    }

    private void pageWait(Page page, long ms) {
        try {
            page.waitForTimeout(ms);
        } catch (Exception ignored) {
        }
    }

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
        page.keyboard().press("Enter");
    }

    private List<CrawlResult> parseApiResponse(JsonNode root, String queryName) {
        List<CrawlResult> results = new ArrayList<>();
        JsonNode code = root.get("code");
        if (code == null) return results;

        int codeVal = code.asInt(-1);
        if (codeVal != 0 && codeVal != 200) return results;

        List<JsonNode> rows = new ArrayList<>();
        JsonNode data = root.get("data");
        if (data != null) {
            if (data.isArray()) {
                rows.addAll(safeArray(data));
            } else {
                for (String key : List.of("results", "rows", "records", "list", "data")) {
                    JsonNode arr = data.get(key);
                    if (arr != null && arr.isArray()) {
                        rows.addAll(safeArray(arr));
                    }
                }
                if (rows.isEmpty() && (data.has("name") || data.has("raceName"))) {
                    rows.add(data);
                }
            }
        }

        for (JsonNode row : rows) {
            try {
                CrawlResult result = mapToCrawlResult(row);
                if (result != null) results.add(result);
            } catch (Exception e) {
                log.debug("[Runchina] Failed to map row: {}", e.getMessage());
            }
        }

        if (results.isEmpty() && data != null && data.size() > 0) {
            CrawlResult fallback = mapToCrawlResult(data);
            if (fallback != null) results.add(fallback);
        }

        return results;
    }

    private List<JsonNode> safeArray(JsonNode arr) {
        List<JsonNode> list = new ArrayList<>();
        if (arr != null && arr.isArray()) arr.forEach(list::add);
        return list;
    }

    private CrawlResult mapToCrawlResult(JsonNode row) {
        Map<String, Object> fields = new HashMap<>();
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

        String gameName = textOrNull(row, "raceName", "competitionName", "matchName", "eventName", "gameName");
        if (gameName != null) fields.put("game_name", gameName);

        String gameDate = textOrNull(row, "raceTime", "raceDate", "competitionDate", "matchDate", "gameDate");
        if (gameDate != null) fields.put("game_date", gameDate);

        fields.put("gun_time_ms", parseTimeMs(textOrNull(row, "gunTime", "gunScore", "grossTime", "totalTime")));
        fields.put("net_time_ms", parseTimeMs(textOrNull(row, "netTime", "netScore", "chipTime", "cleanTime")));

        Integer rank = intOrNull(row, "rank", "overallRank", "totalRank", "overallPlace");
        if (rank != null) fields.put("rank", rank);

        Integer genderRank = intOrNull(row, "genderRank", "sexRank", "genderPlace");
        if (genderRank != null) fields.put("gender_rank", genderRank);

        Integer categoryRank = intOrNull(row, "categoryRank", "groupRank", "categoryPlace");
        if (categoryRank != null) fields.put("category_rank", categoryRank);

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

    private Long parseTimeMs(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return null;
        Matcher m = TIME_PATTERN.matcher(timeStr.trim());
        if (m.find()) {
            long h = Long.parseLong(m.group(1));
            long min = Long.parseLong(m.group(2));
            long sec = Long.parseLong(m.group(3));
            return (h * 3600 + min * 60 + sec) * 1000;
        }
        try {
            double seconds = Double.parseDouble(timeStr.trim());
            return (long) (seconds * 1000);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String mask(String s) {
        if (s == null || s.length() <= 4) return "***";
        return s.substring(0, 4) + "***";
    }
}
