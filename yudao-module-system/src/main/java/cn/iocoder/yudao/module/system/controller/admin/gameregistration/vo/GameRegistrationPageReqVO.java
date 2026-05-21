package cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 赛事报名分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameRegistrationPageReqVO extends PageParam {

    @Schema(description = "赛事编号", example = "1")
    private Long gameId;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "个人报名状态，字典 game_registration_status", example = "registered")
    private String registrationStatus;

    @Schema(description = "参赛组别，字典 game_type", example = "marathon")
    private String gameType;

}
