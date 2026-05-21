package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 赛事 Response VO")
@Data
public class GameRespVO {

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "赛事名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "2027上海马拉松")
    private String name;

    @Schema(description = "比赛日期", example = "2027-11-28")
    private LocalDate gameDate;

    @Schema(description = "赛事类型，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "国际田联标牌等级", example = "platinum")
    private String worldAthleticsLevel;

    @Schema(description = "中国田联路跑赛事等级", example = "a")
    private String chinaRoadRunLevel;

    @Schema(description = "赛事状态，字典 game_status", example = "uncertain")
    private String status;

    @Schema(description = "赛事标签，多个以逗号分隔", example = "wmm,cmm")
    private String tags;

    @Schema(description = "赛事官网链接")
    private String url;

    @Schema(description = "赛事距离（公里）", example = "42.195")
    private BigDecimal distanceKm;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
