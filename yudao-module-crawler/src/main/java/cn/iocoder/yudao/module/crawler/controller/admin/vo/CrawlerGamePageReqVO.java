package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 临时赛事分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerGamePageReqVO extends SortablePageParam {

    @Schema(description = "赛事名称")
    private String name;

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "世界田联等级(多选)")
    private List<String> worldAthleticsLevels;

    @Schema(description = "中国田协等级(多选)")
    private List<String> chinaRoadRunLevels;

    @Schema(description = "赛事状态(多选)")
    private List<String> statuses;

    @Schema(description = "导入状态(多选)")
    private List<String> importStatuses;

    @Schema(description = "比赛日期")
    private LocalDate[] gameDate;
}
