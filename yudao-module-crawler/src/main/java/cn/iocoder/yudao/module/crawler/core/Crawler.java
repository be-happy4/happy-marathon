package cn.iocoder.yudao.module.crawler.core;

import java.util.List;

/**
 * 爬虫接口。每个数据源实现此接口。
 */
public interface Crawler {

    /** 数据源标识，对应 crawler_source.source_key */
    String getSourceKey();

    /** 爬取数据类型 */
    CrawlType getType();

    /**
     * 执行爬取。
     * @param ctx 爬取上下文（全量/增量、时间范围等）
     * @return 解析后的原始数据列表
     */
    List<CrawlResult> crawl(CrawlerContext ctx) throws Exception;
}
