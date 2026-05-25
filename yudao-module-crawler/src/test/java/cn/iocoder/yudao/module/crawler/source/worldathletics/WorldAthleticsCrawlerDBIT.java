package cn.iocoder.yudao.module.crawler.source.worldathletics;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameCategoryDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameCategoryMapper;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import cn.iocoder.yudao.module.crawler.merge.GameMergeStrategy;
import cn.iocoder.yudao.module.crawler.merge.MergeAction;
import cn.iocoder.yudao.module.crawler.pipeline.GamePipeline;
import cn.iocoder.yudao.module.crawler.pipeline.PipelineStats;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 全链路集成测试：API → Crawler → Pipeline → DB。
 *
 * 运行：需要 Spring Boot 上下文 + 代理配置
 */
@SpringBootTest(classes = {cn.iocoder.yudao.server.YudaoServerApplication.class})
class WorldAthleticsCrawlerDBIT {

    @Resource
    private CrawlerGameMapper crawlerGameMapper;

    @Resource
    private CrawlerGameCategoryMapper crawlerGameCategoryMapper;

    @Resource
    private GameMergeStrategy gameMergeStrategy;

    private static WorldAthleticsGraphQLClient client;
    private static WorldAthleticsCrawler crawler;

    @BeforeAll
    static void setUp() throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");

        client = new WorldAthleticsGraphQLClient();
        var field = WorldAthleticsGraphQLClient.class.getDeclaredField("apiBaseUrl");
        field.setAccessible(true);
        field.set(client, "https://worldathletics.nimarion.de");

        crawler = new WorldAthleticsCrawler(client);
    }

    @Test
    void shouldCrawlAndWriteToDB() throws Exception {
        // 1. 爬取数据
        List<CrawlResult> results = crawler.crawl(CrawlerContext.incremental(null));
        System.out.println("爬取结果: " + results.size() + " 条");

        // 2. 通过 Pipeline 写入 DB
        GamePipeline pipeline = new GamePipeline();
        // 注入依赖
        var mapperField = GamePipeline.class.getDeclaredField("crawlerGameMapper");
        mapperField.setAccessible(true);
        mapperField.set(pipeline, crawlerGameMapper);
        var catMapperField = GamePipeline.class.getDeclaredField("crawlerGameCategoryMapper");
        catMapperField.setAccessible(true);
        catMapperField.set(pipeline, crawlerGameCategoryMapper);
        var mergeField = GamePipeline.class.getDeclaredField("gameMergeStrategy");
        mergeField.setAccessible(true);
        mergeField.set(pipeline, gameMergeStrategy);

        PipelineStats stats = pipeline.process(1L, "world_athletics", results);
        System.out.printf("DB写入结果: 新增 %d, 更新 %d, 跳过 %d%n",
                stats.getNewCount(), stats.getUpdatedCount(), stats.getIgnoredCount());

        assertTrue(stats.getNewCount() > 0, "应该有新增的赛事");

        // 3. 验证 DB 数据
        List<CrawlerGameDO> games = crawlerGameMapper.selectList();
        System.out.println("DB 中 crawler_game 总数: " + games.size());
        assertFalse(games.isEmpty(), "DB 应该有数据");

        // 打印前5条
        games.stream().limit(5).forEach(g -> {
            List<CrawlerGameCategoryDO> cats = crawlerGameCategoryMapper.selectByGameId(g.getId());
            System.out.printf("  DB[%d] %s | %s | %s | %s | 组别数=%d%n",
                    g.getId(), g.getName(), g.getGameDate(),
                    g.getRegionName(), g.getWorldAthleticsLevel(), cats.size());
        });
    }
}
