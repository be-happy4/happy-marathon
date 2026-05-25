package cn.iocoder.yudao.module.crawler.source.worldathletics;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WorldAthleticsCrawler 集成测试。
 * 通过代理访问 nimarion API，验证真实数据流。
 *
 * 运行方式：
 *   mvn test -pl yudao-module-crawler -Dtest=WorldAthleticsCrawlerIT -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=7897 -Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=7897
 */
class WorldAthleticsCrawlerIT {

    private static WorldAthleticsGraphQLClient client;
    private static WorldAthleticsCrawler crawler;

    @BeforeAll
    static void setUp() throws Exception {
        // 配置代理
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");

        client = new WorldAthleticsGraphQLClient();
        // 手动注入 apiBaseUrl（测试环境无 Spring 上下文）
        var field = WorldAthleticsGraphQLClient.class.getDeclaredField("apiBaseUrl");
        field.setAccessible(true);
        field.set(client, "https://worldathletics.nimarion.de");

        crawler = new WorldAthleticsCrawler(client);
    }

    @Test
    void shouldCallApiDirectly() {
        List<WorldAthleticsGraphQLClient.NimarionCompetition> comps =
                client.searchCompetitions("Marathon");
        System.out.println("API 直接调用结果: " + comps.size() + " 条");
        comps.stream().limit(3).forEach(c ->
                System.out.printf("  [%d] %s | %s | %s%n",
                        c.getId(), c.getName(),
                        c.getLocation() != null ? c.getLocation().getCountry() : "?",
                        c.getDisciplines()));
        assertFalse(comps.isEmpty(), "API 应该返回马拉松赛事");
    }

    @Test
    void shouldFetchMarathonCompetitions() throws Exception {
        List<CrawlResult> results = crawler.crawl(CrawlerContext.incremental(null));

        assertNotNull(results);
        assertFalse(results.isEmpty(), "应该爬取到赛事数据");

        // 验证数据质量
        for (CrawlResult r : results) {
            assertNotNull(r.getString("name"), "赛事名称不能为空: " + r.getSourceGameId());
            assertNotNull(r.getSourceGameId(), "sourceGameId 不能为空");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cats = (List<Map<String, Object>>) r.getFields().get("categories");
            assertNotNull(cats, "组别不能为空: " + r.getString("name"));
            assertFalse(cats.isEmpty(), "至少有一个组别: " + r.getString("name"));

            for (Map<String, Object> cat : cats) {
                assertNotNull(cat.get("game_type"), "game_type 不能为空");
            }
        }

        // 打印前5条供人工验证
        System.out.println("=== 爬取结果 (前5条) ===");
        results.stream().limit(5).forEach(r -> {
            System.out.printf("  [%s] %s | %s | %s | labels=%s%n",
                    r.getSourceGameId(),
                    r.getString("name"),
                    r.getString("game_date"),
                    r.getString("region_name"),
                    r.getString("world_athletics_level"));
        });
        System.out.println("=== 共 " + results.size() + " 条 ===");
    }

    @Test
    void shouldFetchAndFilterByGameType() throws Exception {
        List<CrawlResult> results = crawler.crawl(CrawlerContext.incremental(null));

        @SuppressWarnings("unchecked")
        long marathonCount = results.stream()
                .filter(r -> {
                    List<Map<String, Object>> cats = (List<Map<String, Object>>) r.getFields().get("categories");
                    return cats != null && cats.stream().anyMatch(c -> "marathon".equals(c.get("game_type")));
                })
                .count();

        System.out.println("马拉松赛事数: " + marathonCount + " / " + results.size());
        assertTrue(marathonCount > 0, "应该有马拉松赛事");
    }
}
