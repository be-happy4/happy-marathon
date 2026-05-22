package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 赛事组别 Response VO")
@Data
public class GameCategoryRespVO {

    @Schema(description = "组别编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long gameId;

    @Schema(description = "赛事名称")
    private String gameName;

    @Schema(description = "比赛日期")
    private LocalDate gameDate;

    @Schema(description = "组别类型，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "该组别距离（米）", example = "42195")
    private BigDecimal distanceM;

}
