package cn.iocoder.yudao.module.crawler.generic;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlType;
import cn.iocoder.yudao.module.crawler.core.Crawler;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * 通用 API 爬虫（GENERIC 模式）。
 * 根据 crawler_source 的配置执行 HTTP 请求和响应映射。
 * 第一期不实现具体逻辑，后续有需要再完善。
 */
@Slf4j
public class GenericApiCrawler implements Crawler {

    private final CrawlerSourceDO source;

    public GenericApiCrawler(CrawlerSourceDO source) {
        this.source = source;
    }

    @Override
    public String getSourceKey() {
        return source.getSourceKey();
    }

    @Override
    public CrawlType getType() {
        try {
            return CrawlType.valueOf(source.getCrawlType());
        } catch (Exception e) {
            return CrawlType.GAME;
        }
    }

    @Override
    public List<CrawlResult> crawl(CrawlerContext ctx) throws Exception {
        // TODO: 实现通用 HTTP 请求 + JSONPath 响应解析
        log.warn("[GenericApiCrawler][{}] GENERIC 模式暂未实现", source.getSourceKey());
        return Collections.emptyList();
    }
}
