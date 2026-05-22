package cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 赛事报名 Response VO")
@Data
public class GameRegistrationRespVO {

    @Schema(description = "报名编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long gameId;

    @Schema(description = "赛事名称", example = "2027上海马拉松")
    private String gameName;

    @Schema(description = "比赛日期")
    private LocalDate gameDate;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "用户昵称", example = "芋道")
    private String userNickname;

    @Schema(description = "个人报名状态，字典 game_registration_status", example = "registered")
    private String registrationStatus;

    @Schema(description = "参赛组别，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "组别编号", example = "1")
    private Long gameCategoryId;

    @Schema(description = "组别名称", example = "全程马拉松")
    private String gameCategoryName;

    @Schema(description = "组别距离（米）")
    private BigDecimal categoryDistanceM;

    @Schema(description = "配速（/km），格式 MM:SS")
    private String pace;

    @Schema(description = "时速（km/h）")
    private Double speed;

    @Schema(description = "个人优先级", example = "10")
    private Integer priority;

    @Schema(description = "个人标签，多个以逗号分隔", example = "wmm")
    private String tags;

    @Schema(description = "参赛号码", example = "A12345")
    private String bibNumber;

    @Schema(description = "枪声成绩（毫秒）", example = "10712456")
    private Long gunTimeMs;

    @Schema(description = "净成绩（毫秒）", example = "10712456")
    private Long netTimeMs;

    @Schema(description = "性别排名", example = "100")
    private Integer genderPlace;

    @Schema(description = "总排名", example = "500")
    private Integer overallPlace;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
