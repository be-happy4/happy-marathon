package cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 赛事报名分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameRegistrationPageReqVO extends SortablePageParam {

    @Schema(description = "赛事编号", example = "1")
    private Long gameId;

    @Schema(description = "赛事编号多选")
    private List<Long> gameIds;

    @Schema(description = "比赛日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] gameDate;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "个人报名状态", example = "registered")
    private String registrationStatus;

    @Schema(description = "个人报名状态多选")
    private List<String> registrationStatuses;

    @Schema(description = "参赛组别，字典 game_type", example = "marathon")
    private String gameType;

    @Schema(description = "组别编号", example = "1")
    private Long gameCategoryId;

    @Schema(description = "组别编号多选")
    private List<Long> gameCategoryIds;

}
