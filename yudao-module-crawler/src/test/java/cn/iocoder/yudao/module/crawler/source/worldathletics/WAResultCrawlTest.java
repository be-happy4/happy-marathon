package cn.iocoder.yudao.module.crawler.source.worldathletics;

import cn.iocoder.yudao.module.crawler.CrawlerTestApplication;
import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import cn.iocoder.yudao.module.crawler.pipeline.PipelineStats;
import cn.iocoder.yudao.module.crawler.pipeline.ResultPipeline;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * 批量爬取 World Athletics 中国赛事成绩。
 * 需要代理: http.proxyHost=127.0.0.1 http.proxyPort=7897
 */
@SpringBootTest(classes = CrawlerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WAResultCrawlTest {

    @Resource
    private WorldAthleticsGraphQLClient client;

    @Resource
    private CrawlerGameMapper crawlerGameMapper;

    @Resource
    private ResultPipeline resultPipeline;

    @Test
    void crawlChineseGameResults() throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");

        // Find all Chinese WA games with source_game_id
        List<CrawlerGameDO> allGames = crawlerGameMapper.selectList();
        List<CrawlerGameDO> chnGames = allGames.stream()
                .filter(g -> g.getSourceId() != null && g.getSourceId() == 1L)
                .filter(g -> g.getRegionName() != null && g.getRegionName().contains("CHN"))
                .filter(g -> g.getSourceGameId() != null && !g.getSourceGameId().isBlank())
                .toList();

        System.out.println("=== 找到 " + chnGames.size() + " 个中国 WA 赛事 ===");

        int successCount = 0;
        int totalResults = 0;

        for (int i = 0; i < chnGames.size(); i++) {
            CrawlerGameDO game = chnGames.get(i);
            Long compId = Long.parseLong(game.getSourceGameId());
            try {
                WorldAthleticsGraphQLClient.NimarionResultsResponse resp =
                        client.getCompetitionResults(compId);
                if (resp == null || resp.getEvents() == null || resp.getEvents().isEmpty()) {
                    continue;
                }

                List<CrawlResult> results = new ArrayList<>();
                for (var event : resp.getEvents()) {
                    if (event.getRaces() == null) continue;
                    for (var race : event.getRaces()) {
                        if (race.getResults() == null) continue;
                        for (var result : race.getResults()) {
                            results.add(mapResult(compId, event, race, result));
                        }
                    }
                }

                // set crawler_game_id on each result for proper dedup
                for (var r : results) {
                    r.getFields().put("crawler_game_id", game.getId());
                    r.getFields().put("game_name", game.getName());
                    r.getFields().put("game_date", game.getGameDate() != null ? game.getGameDate().toString() : null);
                }

                if (!results.isEmpty()) {
                    PipelineStats stats = resultPipeline.process(1L, "world_athletics", results);
                    successCount++;
                    totalResults += results.size();
                    System.out.printf("  [%d/%d] %s → %d条 (新增%d 更新%d 跳过%d)%n",
                            i + 1, chnGames.size(), game.getName(),
                            results.size(), stats.getNewCount(), stats.getUpdatedCount(), stats.getIgnoredCount());
                }

                Thread.sleep(300); // rate limit
            } catch (Exception e) {
                System.out.printf("  [%d/%d] %s → 失败: %s%n",
                        i + 1, chnGames.size(), game.getName(), e.getMessage());
            }
        }

        System.out.printf("%n=== 完成: %d/%d 赛事有成绩, 共 %d 条 ===%n",
                successCount, chnGames.size(), totalResults);
    }

    private CrawlResult mapResult(Long compId,
                                   WorldAthleticsGraphQLClient.NimarionEvent event,
                                   WorldAthleticsGraphQLClient.NimarionRace race,
                                   WorldAthleticsGraphQLClient.NimarionResult result) {
        var athlete = (result.getAthletes() != null && !result.getAthletes().isEmpty())
                ? result.getAthletes().get(0) : null;

        Map<String, Object> fields = new HashMap<>();
        fields.put("name", athlete != null ? athlete.getFirstname() + " " + athlete.getLastname() : null);
        fields.put("name_en", fields.get("name"));
        fields.put("nationality", result.getCountry());
        fields.put("gender", event.getSex());
        fields.put("age_group", event.getCategory());
        fields.put("gun_time_ms", result.getPerformanceValue());
        fields.put("net_time_ms", result.getPerformanceValue());
        fields.put("rank", result.getPlace());
        fields.put("game_type", event.getDiscipline());

        return CrawlResult.builder()
                .sourceKey("world_athletics")
                .sourceGameId(String.valueOf(compId))
                .fields(fields)
                .rawData("{\"place\":" + result.getPlace() + ",\"mark\":\"" + esc(result.getMark()) + "\"}")
                .build();
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
