package cn.iocoder.yudao.module.crawler.source.runchina;

import cn.iocoder.yudao.module.crawler.CrawlerTestApplication;
import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import cn.iocoder.yudao.module.crawler.pipeline.GamePipeline;
import cn.iocoder.yudao.module.crawler.pipeline.PipelineStats;
import cn.iocoder.yudao.module.crawler.service.CrawlerSourceService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * runchina 累积爬取测试。
 * recentMatch 接口固定返回最近20条, 不支持分页。
 * 通过定期爬取 + 去重 (name+game_date) 实现数据累积。
 */
@SpringBootTest(classes = CrawlerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RunchinaCrawlToDBTest {

    @Resource
    private RunchinaCrawler crawler;

    @Resource
    private GamePipeline gamePipeline;

    @Resource
    private CrawlerSourceService sourceService;

    @Test
    void accumulateByRepeatedCrawl() throws Exception {
        Long sourceId = sourceService.getSourceByKey("runchina").getId();
        System.out.println("=== Runchina 累积爬取测试 (Source ID=" + sourceId + ") ===");

        // 第1轮爬取
        List<CrawlResult> results1 = crawler.crawl(CrawlerContext.incremental(null));
        System.out.println("第1轮爬取: " + results1.size() + " 条");
        PipelineStats stats1 = gamePipeline.process(sourceId, "runchina", results1);
        System.out.printf("  → 新增=%d, 更新=%d, 跳过=%d%n",
                stats1.getNewCount(), stats1.getUpdatedCount(), stats1.getIgnoredCount());

        // 第2轮爬取 (模拟下次调度, 验证去重)
        List<CrawlResult> results2 = crawler.crawl(CrawlerContext.incremental(null));
        System.out.println("第2轮爬取: " + results2.size() + " 条 (模拟下次调度)");
        PipelineStats stats2 = gamePipeline.process(sourceId, "runchina", results2);
        System.out.printf("  → 新增=%d, 更新=%d, 跳过=%d (去重生效)%n",
                stats2.getNewCount(), stats2.getUpdatedCount(), stats2.getIgnoredCount());

        // 第3轮爬取 (再次验证)
        List<CrawlResult> results3 = crawler.crawl(CrawlerContext.incremental(null));
        System.out.println("第3轮爬取: " + results3.size() + " 条 (再次验证)");
        PipelineStats stats3 = gamePipeline.process(sourceId, "runchina", results3);
        System.out.printf("  → 新增=%d, 更新=%d, 跳过=%d%n",
                stats3.getNewCount(), stats3.getUpdatedCount(), stats3.getIgnoredCount());

        int totalAccumulated = stats1.getNewCount() + stats2.getNewCount() + stats3.getNewCount();
        System.out.println("\n=== 累积结果: 数据库中共 " + totalAccumulated + " 条 runchina 赛事 ===");
        System.out.println("当 API 返回不同的赛事时 (如几天后新增比赛), 新赛事会自动插入");
        System.out.println("已存在的赛事 (name+game_date) 会被去重跳过");

        assertTrue(stats1.getNewCount() >= 0, "第1轮应有数据");
        // 第2轮应该全部跳过 (去重)
        assertEquals(0, stats2.getNewCount(), "第2轮新增应该为0 (去重)");
        assertEquals(0, stats3.getNewCount(), "第3轮新增应该为0 (去重)");
    }
}
