package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 临时成绩分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerGameResultPageReqVO extends PageParam {

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "匹配状态", example = "UNMATCHED")
    private String matchStatus;

    @Schema(description = "导入状态", example = "PENDING")
    private String importStatus;

    @Schema(description = "选手姓名")
    private String name;

    @Schema(description = "赛事名称")
    private String gameName;

    @Schema(description = "身份证号")
    private String idCard;
}
