package cn.iocoder.yudao.module.crawler.pipeline;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;

import java.util.List;

/**
 * 数据处理管道接口。
 * 将爬虫原始结果转换为临时表 DO 并写入。
 */
public interface DataPipeline {

    /** 处理的数据类型 */
    String getCrawlType();

    /**
     * 处理爬取结果，写入临时表。
     * @param sourceId 数据源 ID
     * @param sourceKey 数据源标识
     * @param results 爬取结果列表
     * @return 处理统计 {new: N, updated: N, ignored: N}
     */
    PipelineStats process(Long sourceId, String sourceKey, List<CrawlResult> results);
}
