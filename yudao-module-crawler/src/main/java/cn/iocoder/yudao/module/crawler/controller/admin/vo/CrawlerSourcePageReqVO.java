package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 数据源分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerSourcePageReqVO extends PageParam {

    @Schema(description = "数据源名称", example = "数字心动")
    private String name;

    @Schema(description = "数据源标识", example = "runchina")
    private String sourceKey;

    @Schema(description = "爬取类型", example = "RESULT")
    private String crawlType;

    @Schema(description = "处理器类型", example = "CUSTOM")
    private String handlerType;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;
}
