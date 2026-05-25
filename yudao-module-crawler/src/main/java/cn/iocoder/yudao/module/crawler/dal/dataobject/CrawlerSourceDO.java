package cn.iocoder.yudao.module.crawler.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 数据源配置
 */
@TableName(value = "crawler_source", autoResultMap = true)
@KeySequence("crawler_source_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerSourceDO extends BaseDO {

    @TableId
    private Long id;

    /** 显示名称 */
    private String name;

    /** 程序标识 */
    private String sourceKey;

    /** 爬取类型：GAME / RESULT */
    private String crawlType;

    /** 来源类型：OFFICIAL / PLATFORM */
    private String sourceType;

    /** 处理器类型：CUSTOM / GENERIC */
    private String handlerType;

    /** CUSTOM 类型的实现类全限定名 */
    private String handlerClass;

    /** API 基础 URL */
    private String baseUrl;

    /** 认证配置 JSON */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> authConfig;

    /** 请求配置 JSON */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> requestConfig;

    /** 响应映射 JSON（GENERIC 模式） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> responseMapping;

    /** 定时 cron 表达式 */
    private String cronExpression;

    /** 是否启用 */
    private Boolean enabled;

    /** 排序 */
    private Integer sortOrder;

    /** 最近执行时间 */
    private LocalDateTime lastRunTime;

    /** 最近执行状态 */
    private String lastRunStatus;

    /** 最近执行摘要 */
    private String lastRunSummary;
}
