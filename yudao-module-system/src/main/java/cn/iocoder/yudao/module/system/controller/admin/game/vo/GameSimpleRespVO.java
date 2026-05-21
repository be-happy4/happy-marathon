package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 赛事精简 Response VO")
@Data
public class GameSimpleRespVO {

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "赛事名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "2027上海马拉松")
    private String name;

    @Schema(description = "比赛日期", example = "2027-11-28")
    private LocalDate gameDate;

}
