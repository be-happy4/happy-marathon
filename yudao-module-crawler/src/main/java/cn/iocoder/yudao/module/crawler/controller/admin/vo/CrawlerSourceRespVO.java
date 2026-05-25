package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - 数据源 Response VO")
@Data
public class CrawlerSourceRespVO {

    @Schema(description = "数据源编号")
    private Long id;

    @Schema(description = "显示名称")
    private String name;

    @Schema(description = "程序标识")
    private String sourceKey;

    @Schema(description = "爬取类型")
    private String crawlType;

    @Schema(description = "来源类型")
    private String sourceType;

    @Schema(description = "处理器类型")
    private String handlerType;

    @Schema(description = "实现类")
    private String handlerClass;

    @Schema(description = "API 基础 URL")
    private String baseUrl;

    @Schema(description = "认证配置")
    private Map<String, Object> authConfig;

    @Schema(description = "请求配置")
    private Map<String, Object> requestConfig;

    @Schema(description = "响应映射")
    private Map<String, Object> responseMapping;

    @Schema(description = "Cron 表达式")
    private String cronExpression;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "最近执行时间")
    private LocalDateTime lastRunTime;

    @Schema(description = "最近执行状态")
    private String lastRunStatus;

    @Schema(description = "最近执行摘要")
    private String lastRunSummary;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
