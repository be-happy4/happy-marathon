package cn.iocoder.yudao.module.crawler.core;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 爬取结果封装。每个 CrawlResult 代表一条解析后的原始记录。
 * rawData 保留完整原始 JSON，供后续 re-ETL 使用。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlResult {

    /** 所属数据源 ID */
    private Long sourceId;

    /** 数据源标识 */
    private String sourceKey;

    /** 来源系统中的原始 ID */
    private String sourceGameId;

    /** 已解析的字段，key 为 DO 字段名，value 为解析后的值 */
    private Map<String, Object> fields;

    /** 完整原始数据（JSON 字符串） */
    private String rawData;

    public String getString(String field) {
        Object v = fields.get(field);
        return v != null ? v.toString() : null;
    }

    public Long getLong(String field) {
        Object v = fields.get(field);
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString());
    }

    public Integer getInteger(String field) {
        Object v = fields.get(field);
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        return Integer.parseInt(v.toString());
    }
}
