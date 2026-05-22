package cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 赛事组别创建/修改 Request VO")
@Data
public class GameCategorySaveReqVO {

    @Schema(description = "组别编号", example = "1")
    private Long id;

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "赛事编号不能为空")
    private Long gameId;

    @Schema(description = "组别类型，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "该组别距离（米）", example = "42195")
    private BigDecimal distanceM;

}
