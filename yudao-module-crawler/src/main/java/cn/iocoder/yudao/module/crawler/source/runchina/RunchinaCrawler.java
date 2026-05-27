package cn.iocoder.yudao.module.crawler.source.runchina;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlType;
import cn.iocoder.yudao.module.crawler.core.Crawler;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国马拉松官网 (runchina.org.cn) 数据源爬虫。
 *
 * 通过 changzheng API 网关的 searchCompetitionMls 分页接口爬取全量赛事。
 * recentMatch 仅返回最近20条，searchCompetitionMls 支持全量分页 (当前 ~2800条)。
 */
@Component
@Slf4j
public class RunchinaCrawler implements Crawler {

    private static final String SOURCE_KEY = "runchina";
    private static final String SEARCH_URL = "https://api-changzheng.chinaath.com/changzheng-content-center-api/api/homePage/official/searchCompetitionMls";
    private static final int PAGE_SIZE = 50;

    private static final Pattern CITY_PATTERN = Pattern.compile(
            "\\d{4}\\s*([\\u4e00-\\u9fff]+?)\\s*(?:马拉松|半程|10公里|越野|超级马拉松|接力|精英赛|挑战赛|竞走|路跑)",
            Pattern.CASE_INSENSITIVE);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RunchinaCrawler() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .sslContext(createPermissiveSslContext())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private static SSLContext createPermissiveSslContext() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }
            };
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new SecureRandom());
            return ctx;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSourceKey() {
        return SOURCE_KEY;
    }

    @Override
    public CrawlType getType() {
        return CrawlType.GAME;
    }

    @Override
    public List<CrawlResult> crawl(CrawlerContext ctx) throws Exception {
        // 先拿到第一页获取 totalCount
        Map<String, Object> params = new HashMap<>();
        params.put("provinceId", "");
        params.put("cityId", "");
        params.put("districtId", "");
        params.put("raceName", "");
        params.put("raceGrade", "");
        params.put("raceStartTime", "");
        params.put("pageNo", 1);
        params.put("pageSize", PAGE_SIZE);

        JsonNode firstPage = postJson(SEARCH_URL, params);
        JsonNode data = firstPage.path("data");
        int totalCount = data.path("totalCount").asInt(0);
        int pageCount = data.path("pageCount").asInt(1);
        log.info("[Runchina] totalCount={}, pageCount={}, 开始分页爬取", totalCount, pageCount);

        Set<Long> seenIds = new HashSet<>();
        List<CrawlResult> allResults = new ArrayList<>();

        for (int page = 1; page <= pageCount; page++) {
            if (page > 1) {
                params.put("pageNo", page);
                data = postJson(SEARCH_URL, params).path("data");
            }

            JsonNode results = data.path("results");
            for (JsonNode item : results) {
                long raceId = item.path("raceId").asLong();
                if (!seenIds.add(raceId)) continue;

                CrawlResult result = mapToResult(item);
                if (result != null) {
                    allResults.add(result);
                }
            }

            if (page % 10 == 0 || page == pageCount) {
                log.info("[Runchina] 进度: {}/{} 页, 已收集 {} 条", page, pageCount, allResults.size());
            }
        }

        log.info("[Runchina] 爬取完成: {} 条赛事", allResults.size());
        return allResults;
    }

    private CrawlResult mapToResult(JsonNode item) {
        try {
            String name = item.path("raceName").asText();
            if (name.isBlank()) return null;

            Map<String, Object> fields = new HashMap<>();
            fields.put("name", name);
            fields.put("game_date", parseDate(item.path("raceTime").asText()));
            fields.put("region_name", extractRegionName(item.path("raceAddress").asText()));
            fields.put("china_road_run_level", mapRaceGrade(item.path("raceGrade").asText()));
            fields.put("tags", mapTags(item.path("raceGrade").asText()));
            fields.put("status", "announced");
            fields.put("categories", parseRaceItems(item.path("raceItem").asText()));

            return CrawlResult.builder()
                    .sourceKey(SOURCE_KEY)
                    .sourceGameId(String.valueOf(item.path("raceId").asLong()))
                    .fields(fields)
                    .rawData(item.toString())
                    .build();
        } catch (Exception e) {
            log.warn("[Runchina] 解析失败: {}", item, e);
            return null;
        }
    }

    static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 "黑龙江省/黑河市/嫩江市" 格式地址中提取城市名。
     */
    static String extractRegionName(String address) {
        if (address == null || address.isBlank()) return null;
        String[] parts = address.split("/");
        if (parts.length >= 2) {
            // 取倒数第二部分作为城市/地区名
            return parts[parts.length - 2].trim();
        }
        return address.trim();
    }

    static String mapRaceGrade(String grade) {
        if (grade == null) return null;
        String g = grade.trim();
        if (g.startsWith("A")) return "a";
        if (g.startsWith("B")) return "b";
        if (g.startsWith("C")) return "c";
        return null;
    }

    static String mapTags(String grade) {
        if (grade == null) return null;
        String g = grade.trim();
        if (g.startsWith("A")) return "china_road_run_a";
        if (g.startsWith("B")) return "china_road_run_b";
        return null;
    }

    static List<Map<String, Object>> parseRaceItems(String raceItemJson) {
        List<Map<String, Object>> cats = new ArrayList<>();
        if (raceItemJson == null || raceItemJson.isBlank()) return cats;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(raceItemJson);
            for (JsonNode item : arr) {
                String type = item.asText();
                String gameType = mapRaceType(type);
                if (gameType != null) {
                    Map<String, Object> cat = new HashMap<>();
                    cat.put("game_type", gameType);
                    cat.put("distance_km", getDistanceForType(gameType));
                    cats.add(cat);
                }
            }
        } catch (Exception e) {
            log.debug("[Runchina] 解析 raceItem 失败: {}", raceItemJson);
        }
        return cats;
    }

    static String mapRaceType(String chinese) {
        if (chinese == null) return null;
        return switch (chinese.trim()) {
            case "全程", "全程马拉松" -> "marathon";
            case "半程", "半程马拉松" -> "half_marathon";
            case "10公里", "十公里" -> "road_run";
            case "5公里" -> "road_run";
            case "越野" -> "trail_run";
            default -> null;
        };
    }

    static BigDecimal getDistanceForType(String gameType) {
        return switch (gameType) {
            case "marathon" -> new BigDecimal("42.195");
            case "half_marathon" -> new BigDecimal("21.0975");
            case "road_run" -> new BigDecimal("10.000");
            default -> null;
        };
    }

    private JsonNode postJson(String url, Object body) throws Exception {
        String bodyStr = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .header("Origin", "https://www.runchina.org.cn")
                .header("Referer", "https://www.runchina.org.cn/")
                .POST(HttpRequest.BodyPublishers.ofString(bodyStr))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }
}
