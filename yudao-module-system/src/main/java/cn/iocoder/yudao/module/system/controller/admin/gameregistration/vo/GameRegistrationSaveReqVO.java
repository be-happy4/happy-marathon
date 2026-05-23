package cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 赛事报名创建/修改 Request VO")
@Data
public class GameRegistrationSaveReqVO {

    @Schema(description = "报名编号", example = "1")
    private Long id;

    @Schema(description = "赛事编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "赛事编号不能为空")
    private Long gameId;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "个人报名状态，字典 game_registration_status", example = "registered")
    private String registrationStatus;

    @Schema(description = "组别编号，关联 system_game_category", example = "1")
    private Long gameCategoryId;

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

}
