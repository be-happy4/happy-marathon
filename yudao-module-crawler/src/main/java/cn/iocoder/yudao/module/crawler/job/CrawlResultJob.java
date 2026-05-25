package cn.iocoder.yudao.module.crawler.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.crawler.core.CrawlType;
import cn.iocoder.yudao.module.crawler.core.Crawler;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import cn.iocoder.yudao.module.crawler.core.CrawlerRegistry;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import cn.iocoder.yudao.module.crawler.pipeline.PipelineStats;
import cn.iocoder.yudao.module.crawler.pipeline.ResultPipeline;
import cn.iocoder.yudao.module.crawler.service.CrawlerTaskLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 成绩信息爬取 Job。
 * Quartz 每 5 分钟触发一次，实际执行由各数据源的 cron 决定。
 */
@Component
@Slf4j
public class CrawlResultJob implements JobHandler {

    @Resource
    private CrawlerRegistry registry;

    @Resource
    private CrawlerTaskLogService taskLogService;

    @Resource
    private ResultPipeline resultPipeline;

    @Override
    @TenantJob
    public String execute(String param) {
        List<Crawler> crawlers = registry.getCrawlersByType(CrawlType.RESULT);
        if (crawlers.isEmpty()) {
            return "无已注册的成绩爬虫";
        }

        int totalNew = 0;
        int totalUpdated = 0;
        int totalIgnored = 0;
        int successCount = 0;
        int failCount = 0;

        for (Crawler crawler : crawlers) {
            CrawlerTaskLogDO taskLog = new CrawlerTaskLogDO();
            taskLog.setCrawlType("RESULT");
            taskLog.setCrawlMode("INCREMENTAL");

            try {
                Long logId = taskLogService.createLog(taskLog);

                List<cn.iocoder.yudao.module.crawler.core.CrawlResult> results =
                        crawler.crawl(CrawlerContext.incremental(null));

                PipelineStats stats = resultPipeline.process(
                        0L, // sourceId from DB lookup
                        crawler.getSourceKey(), results);

                taskLogService.updateLogResult(logId, "SUCCESS",
                        results.size(), stats.getNewCount(), stats.getUpdatedCount(), stats.getIgnoredCount(), null);

                totalNew += stats.getNewCount();
                totalUpdated += stats.getUpdatedCount();
                totalIgnored += stats.getIgnoredCount();
                successCount++;
            } catch (Exception e) {
                log.error("[CrawlResultJob][{}] 爬取失败", crawler.getSourceKey(), e);
                taskLog.setStatus("FAILED");
                taskLog.setErrorMsg(e.getMessage());
                taskLogService.createLog(taskLog);
                failCount++;
            }
        }

        return String.format("成绩爬取完成: 成功 %d, 失败 %d, 新增 %d, 更新 %d, 跳过 %d",
                successCount, failCount, totalNew, totalUpdated, totalIgnored);
    }
}
