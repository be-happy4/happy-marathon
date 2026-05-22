package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 赛事分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class GamePageReqVO extends SortablePageParam {

    @Schema(description = "赛事名称，模糊匹配", example = "上海马拉松")
    private String name;

    @Schema(description = "比赛日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] gameDate;

    @Schema(description = "赛事类型，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "赛事类型多选", example = "[\"marathon\",\"half_marathon\"]")
    private List<String> gameTypes;

    @Schema(description = "赛事状态，字典 game_status", example = "uncertain")
    private String status;

    @Schema(description = "赛事状态多选", example = "[\"uncertain\",\"announced\"]")
    private List<String> statuses;

    @Schema(description = "国际田联标牌等级", example = "platinum")
    private String worldAthleticsLevel;

    @Schema(description = "国际田联标牌等级多选", example = "[\"platinum\",\"gold\"]")
    private List<String> worldAthleticsLevels;

    @Schema(description = "中国田联路跑赛事等级", example = "a")
    private String chinaRoadRunLevel;

    @Schema(description = "中国田联路跑赛事等级多选", example = "[\"a\",\"a1\"]")
    private List<String> chinaRoadRunLevels;

}
