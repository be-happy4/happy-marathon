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
 * 通过 changzheng API 网关的公开端点 /homePage/official/recentMatch
 * 获取近期国内马拉松赛事列表。
 */
@Component
@Slf4j
public class RunchinaCrawler implements Crawler {

    private static final String SOURCE_KEY = "runchina";
    private static final String API_URL = "https://api-changzheng.chinaath.com/changzheng-content-center-api/api/homePage/official/recentMatch";
    private static final String SEARCH_LIST_URL = "https://api-changzheng.chinaath.com/changzheng-content-center-api/api/homePage/official/searchList";

    private static final Pattern CITY_PATTERN = Pattern.compile(
            "\\d{4}\\s*([\\u4e00-\\u9fff]+?)\\s*(?:马拉松|半程|10公里|越野|超级马拉松|接力|精英赛|挑战赛|竞走)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

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
            TrustManager[] trustAll = new TrustManager[] {
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
        log.info("[Runchina] 开始爬取 recentMatch (每次返回最近20条, 通过多次爬取+去重累积)");

        // recentMatch 固定返回最近20条, 不支持分页参数。
        // 通过定期爬取 + GameMergeStrategy 去重 (name+game_date) 实现数据累积。
        String body = objectMapper.writeValueAsString(Map.of());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());

        if (!root.path("success").asBoolean(false)) {
            String msg = root.path("msg").asText("unknown error");
            log.warn("[Runchina] API 返回失败: {}", msg);
            return List.of();
        }

        Set<Long> seenIds = new HashSet<>();
        List<CrawlResult> results = new ArrayList<>();
        for (JsonNode item : root.path("data")) {
            long id = item.path("id").asLong();
            if (!seenIds.add(id)) continue;

            CrawlResult result = mapToResult(item);
            if (result != null) {
                results.add(result);
            }
        }

        // 同时爬取归档赛事 (searchList pageTitle=5,pageType=1, 约4条历史赛事)
        try {
            List<CrawlResult> archived = fetchArchivedRaces();
            for (CrawlResult r : archived) {
                long id = Long.parseLong(r.getSourceGameId());
                if (seenIds.add(1000000 + id)) { // offset to avoid collision
                    results.add(r);
                }
            }
            log.info("[Runchina] 归档赛事 {} 条", archived.size());
        } catch (Exception e) {
            log.warn("[Runchina] 归档赛事抓取失败: {}", e.getMessage());
        }

        log.info("[Runchina] 本次爬取 {} 条 (数据库累积通过定期调度 + merge去重实现)", results.size());
        return results;
    }

    private List<CrawlResult> fetchArchivedRaces() throws Exception {
        String reqBody = objectMapper.writeValueAsString(
                Map.of("pageTitle", 5, "pageType", 1, "pageNo", 1, "pageSize", 20));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SEARCH_LIST_URL))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());

        if (!root.path("success").asBoolean(false)) return List.of();

        List<CrawlResult> results = new ArrayList<>();
        for (JsonNode item : root.path("data")) {
            String title = item.path("title").asText();
            if (title.isBlank()) continue;

            // Extract year from title
            int year = 2021; // default
            Matcher ym = YEAR_PATTERN.matcher(title);
            if (ym.find()) year = Integer.parseInt(ym.group(1));

            String jumpUrl = item.path("jumpUrl").asText();

            Map<String, Object> fields = new HashMap<>();
            fields.put("name", title);
            fields.put("game_date", year > 0 ? LocalDate.of(year, 1, 1) : null);
            fields.put("region_name", extractCity(title));
            fields.put("status", "finished");
            // infer categories from name
            String nameLower = title.toLowerCase();
            if (nameLower.contains("半程")) {
                fields.put("categories", List.of(Map.of("game_type", "half_marathon", "distance_km", new BigDecimal("21.0975"))));
            } else if (nameLower.contains("10公里") || nameLower.contains("10km")) {
                fields.put("categories", List.of(Map.of("game_type", "road_run", "distance_km", new BigDecimal("10.000"))));
            } else {
                fields.put("categories", List.of(Map.of("game_type", "marathon", "distance_km", new BigDecimal("42.195"))));
            }

            results.add(CrawlResult.builder()
                    .sourceKey(SOURCE_KEY)
                    .sourceGameId(jumpUrl)
                    .fields(fields)
                    .rawData(item.toString())
                    .build());
        }
        return results;
    }

    private CrawlResult mapToResult(JsonNode item) {
        try {
            String name = item.path("name").asText();
            if (name.isBlank()) return null;

            Map<String, Object> fields = new HashMap<>();
            fields.put("name", name);
            fields.put("game_date", parseDate(item.path("date").asText()));
            fields.put("region_name", extractCity(name));
            fields.put("china_road_run_level", mapRaceGrade(item.path("raceGrade").asText()));
            fields.put("tags", mapTags(item.path("raceGrade").asText()));
            fields.put("status", "announced");
            fields.put("categories", parseRaceItems(item.path("raceItem").asText()));

            return CrawlResult.builder()
                    .sourceKey(SOURCE_KEY)
                    .sourceGameId(String.valueOf(item.path("id").asLong()))
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

    static String extractCity(String name) {
        if (name == null) return null;
        Matcher m = CITY_PATTERN.matcher(name);
        if (m.find()) {
            String city = m.group(1);
            // clean up special chars
            city = city.replaceAll("[·\\-\\s、]", "");
            if (city.length() <= 6) return city;
        }
        return null;
    }

    static String mapRaceGrade(String grade) {
        if (grade == null) return null;
        return switch (grade.trim().toUpperCase()) {
            case "A" -> "a";
            case "B" -> "b";
            case "C" -> "c";
            default -> null;
        };
    }

    static String mapTags(String grade) {
        if ("A".equals(grade)) return "china_road_run_a";
        if ("B".equals(grade)) return "china_road_run_b";
        return null;
    }

    static List<Map<String, Object>> parseRaceItems(String raceItemJson) {
        List<Map<String, Object>> cats = new ArrayList<>();
        if (raceItemJson == null || raceItemJson.isBlank()) return cats;

        try {
            // raceItem is a JSON array like ["全程","半程"]
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
}
