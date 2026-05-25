package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Schema(description = "管理后台 - 临时赛事分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerGamePageReqVO extends PageParam {

    @Schema(description = "赛事名称")
    private String name;

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "导入状态", example = "PENDING")
    private String importStatus;

    @Schema(description = "比赛日期")
    private LocalDate[] gameDate;
}
