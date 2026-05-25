package cn.iocoder.yudao.module.crawler.source.worldathletics;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlType;
import cn.iocoder.yudao.module.crawler.core.Crawler;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * World Athletics 数据源爬虫。
 *
 * 通过 nimarion REST API (https://worldathletics.nimarion.de) 获取全球赛事数据。
 * 搜索含 "Marathon"、"Half Marathon" 等关键词的赛事。
 */
@Component
@Slf4j
public class WorldAthleticsCrawler implements Crawler {

    private static final String SOURCE_KEY = "world_athletics";
    private static final String[] SEARCH_TERMS = {
            "Marathon", "Half Marathon", "10km", "10K", "Trail",
            "China", "Chinese", "Beijing", "Shanghai", "Xiamen", "Wuxi", "Yangzhou",
            "Guangzhou", "Shenzhen", "Chengdu", "Hangzhou", "Wuhan", "Nanjing"
    };

    private final WorldAthleticsGraphQLClient client;

    public WorldAthleticsCrawler(WorldAthleticsGraphQLClient client) {
        this.client = client;
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
        log.info("[WA Crawler] 开始爬取, 搜索词: {}", (Object) SEARCH_TERMS);

        Set<Long> seenIds = new HashSet<>();
        List<CrawlResult> allResults = new ArrayList<>();

        for (String term : SEARCH_TERMS) {
            List<WorldAthleticsGraphQLClient.NimarionCompetition> competitions =
                    client.searchCompetitions(term);
            log.info("[WA Crawler] 搜索 '{}' → {} 条结果", term, competitions.size());

            for (WorldAthleticsGraphQLClient.NimarionCompetition comp : competitions) {
                if (!seenIds.add(comp.getId())) continue;

                // 过滤非路跑项目
                if (!isRoadRunning(comp.getDisciplines())) continue;

                CrawlResult result = mapToResult(comp);
                allResults.add(result);
            }
        }

        log.info("[WA Crawler] 爬取完成, 去重后 {} 条赛事", allResults.size());
        return allResults;
    }

    CrawlResult mapToResult(WorldAthleticsGraphQLClient.NimarionCompetition comp) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", comp.getName());
        fields.put("name_en", comp.getName());
        fields.put("game_date", parseDate(comp.getStart()));
        fields.put("region_name", buildRegion(comp.getLocation()));
        fields.put("world_athletics_level", deriveLabel(comp.getCompetitionGroup()));
        fields.put("status", deriveStatus(comp.getStart(), comp.getEnd()));
        fields.put("tags", deriveTags(comp.getCompetitionGroup()));
        fields.put("categories", deriveCategories(comp.getDisciplines(), comp.getName()));

        return CrawlResult.builder()
                .sourceKey(SOURCE_KEY)
                .sourceGameId(String.valueOf(comp.getId()))
                .fields(fields)
                .rawData(toRawJson(comp))
                .build();
    }

    // ============ 映射方法 ============

    static String buildRegion(WorldAthleticsGraphQLClient.NimarionLocation loc) {
        if (loc == null) return null;
        StringBuilder sb = new StringBuilder();
        if (loc.getCity() != null) sb.append(loc.getCity());
        if (loc.getCountry() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(loc.getCountry());
        }
        return !sb.isEmpty() ? sb.toString() : null;
    }

    static String deriveLabel(String competitionGroup) {
        if (competitionGroup == null) return null;
        String g = competitionGroup.toLowerCase();
        if (g.contains("platinum")) return "platinum";
        if (g.contains("gold")) return "gold";
        if (g.contains("elite")) return "elite";
        if (g.contains("label")) return "label";
        return null;
    }

    static String deriveTags(String competitionGroup) {
        if (competitionGroup == null) return null;
        List<String> tags = new ArrayList<>();
        if (competitionGroup.toLowerCase().contains("world marathon majors")) {
            tags.add("wmm");
        }
        return tags.isEmpty() ? null : String.join(",", tags);
    }

    static String deriveStatus(String start, String end) {
        if (start == null) return "uncertain";
        try {
            LocalDate startDate = parseDate(start);
            if (startDate == null) return "uncertain";
            LocalDate today = LocalDate.now();
            if (startDate.isAfter(today)) return "announced";
            if (end != null) {
                LocalDate endDate = parseDate(end);
                if (endDate != null && endDate.isBefore(today)) return "finished";
            }
            if (startDate.equals(today)) return "prepared";
            if (startDate.isBefore(today)) return "finished";
            return "announced";
        } catch (Exception e) {
            return "uncertain";
        }
    }

    static List<Map<String, Object>> deriveCategories(List<String> disciplines, String name) {
        List<Map<String, Object>> cats = new ArrayList<>();
        if (disciplines == null || disciplines.isEmpty()) {
            // 根据名称推断
            Map<String, Object> inferred = inferCategory(name);
            if (inferred != null) cats.add(inferred);
            return cats;
        }

        String nameLower = name != null ? name.toLowerCase() : "";
        for (String disc : disciplines) {
            if (disc == null) continue;
            if (!isRoadRunningDiscipline(disc)) continue;
            // Road Running 统一类别，根据名称区分具体组别
            String gameType = inferGameType(nameLower, disc);
            if (gameType != null) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("game_type", gameType);
                cat.put("distance_km", getDistanceForType(gameType));
                cats.add(cat);
            }
        }
        return cats;
    }

    static Map<String, Object> inferCategory(String name) {
        if (name == null) return null;
        String n = name.toLowerCase();
        String gameType = inferGameType(n, "Road Running");
        if (gameType == null) return null;
        Map<String, Object> cat = new HashMap<>();
        cat.put("game_type", gameType);
        cat.put("distance_km", getDistanceForType(gameType));
        return cat;
    }

    static String inferGameType(String nameLower, String discipline) {
        String disc = discipline != null ? discipline.toLowerCase() : "";
        if (nameLower.contains("marathon") && !nameLower.contains("half")) return "marathon";
        if (nameLower.contains("half marathon")) return "half_marathon";
        if (nameLower.contains("10k") || nameLower.contains("10km")) return "road_run";
        if (nameLower.contains("trail")) return "trail_run";
        // fallback to discipline
        if (disc.contains("marathon") && !disc.contains("half")) return "marathon";
        if (disc.contains("half marathon")) return "half_marathon";
        return "road_run";
    }

    static boolean isRoadRunning(List<String> disciplines) {
        if (disciplines == null) return false;
        return disciplines.stream().anyMatch(WorldAthleticsCrawler::isRoadRunningDiscipline);
    }

    static boolean isRoadRunningDiscipline(String d) {
        if (d == null) return false;
        String lower = d.toLowerCase();
        return lower.contains("road running") || lower.contains("marathon")
                || lower.contains("race walking");
    }

    static BigDecimal getDistanceForType(String gameType) {
        return switch (gameType) {
            case "marathon" -> new BigDecimal("42.195");
            case "half_marathon" -> new BigDecimal("21.0975");
            case "road_run" -> new BigDecimal("10.000");
            case "trail_run" -> null;
            default -> null;
        };
    }

    static LocalDate parseDate(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            // "2026-05-16T00:00:00.000Z" → 取前10位
            String datePart = iso.length() >= 10 ? iso.substring(0, 10) : iso;
            return LocalDate.parse(datePart);
        } catch (Exception e) {
            return null;
        }
    }

    // ============ 简单 JSON 序列化 ============

    private String toRawJson(WorldAthleticsGraphQLClient.NimarionCompetition comp) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":").append(comp.getId()).append(",");
        sb.append("\"name\":\"").append(esc(comp.getName())).append("\",");
        if (comp.getLocation() != null) {
            sb.append("\"location\":{\"city\":\"").append(esc(comp.getLocation().getCity()))
              .append("\",\"country\":\"").append(esc(comp.getLocation().getCountry())).append("\"},");
        }
        sb.append("\"start\":\"").append(comp.getStart()).append("\",");
        sb.append("\"end\":\"").append(comp.getEnd()).append("\",");
        sb.append("\"rankingCategory\":\"").append(esc(comp.getRankingCategory())).append("\",");
        sb.append("\"competitionGroup\":\"").append(esc(comp.getCompetitionGroup())).append("\",");
        sb.append("\"hasResults\":").append(comp.getHasResults());
        sb.append("}");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
