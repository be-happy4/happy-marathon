package cn.iocoder.yudao.module.crawler.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 爬取上下文，承载本次爬取的参数和状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlerContext {

    /** 爬取模式 */
    private CrawlMode crawlMode;

    /** 增量模式：爬取此时间之后的数据 */
    private LocalDateTime since;

    /** 全量模式：分页参数 */
    private Integer page;

    /** 全量模式：每页大小 */
    private Integer pageSize;

    /** 上次爬取成功的时间（用于增量） */
    private LocalDateTime lastSuccessTime;

    /** 扩展参数 */
    @Builder.Default
    private Map<String, Object> extra = new HashMap<>();

    public static CrawlerContext incremental(LocalDateTime since) {
        return CrawlerContext.builder()
                .crawlMode(CrawlMode.INCREMENTAL)
                .since(since)
                .build();
    }

    public static CrawlerContext full() {
        return CrawlerContext.builder()
                .crawlMode(CrawlMode.FULL)
                .page(1)
                .pageSize(100)
                .build();
    }
}
