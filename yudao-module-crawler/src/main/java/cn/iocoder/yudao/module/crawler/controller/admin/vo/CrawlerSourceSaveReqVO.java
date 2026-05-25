package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 数据源新增/修改 Request VO")
@Data
public class CrawlerSourceSaveReqVO {

    @Schema(description = "数据源编号", example = "1")
    private Long id;

    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "数字心动")
    @NotBlank(message = "名称不能为空")
    private String name;

    @Schema(description = "程序标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "runchina")
    @NotBlank(message = "标识不能为空")
    private String sourceKey;

    @Schema(description = "爬取类型", example = "RESULT")
    private String crawlType;

    @Schema(description = "来源类型", example = "PLATFORM")
    private String sourceType;

    @Schema(description = "处理器类型", example = "CUSTOM")
    private String handlerType;

    @Schema(description = "实现类全限定名", example = "cn.iocoder.yudao.module.crawler.source.runchina.RunchinaCrawler")
    private String handlerClass;

    @Schema(description = "API 基础 URL")
    private String baseUrl;

    @Schema(description = "认证配置")
    private Map<String, Object> authConfig;

    @Schema(description = "请求配置")
    private Map<String, Object> requestConfig;

    @Schema(description = "响应映射")
    private Map<String, Object> responseMapping;

    @Schema(description = "Cron 表达式", example = "0 30 3 * * ?")
    private String cronExpression;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序")
    private Integer sortOrder;
}
