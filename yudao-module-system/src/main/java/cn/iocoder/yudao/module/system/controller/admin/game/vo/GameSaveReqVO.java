package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 赛事创建/修改 Request VO")
@Data
public class GameSaveReqVO {

    @Schema(description = "赛事编号", example = "1")
    private Long id;

    @Schema(description = "赛事名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "2027上海马拉松")
    @NotBlank(message = "赛事名称不能为空")
    @Size(max = 200, message = "赛事名称长度不能超过 200 个字符")
    private String name;

    @Schema(description = "比赛日期", example = "2027-11-28")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
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

    @Schema(description = "赛事官网链接", example = "https://www.shanghai-marathon.com")
    private String url;

    @Schema(description = "地区编号，关联 system_region", example = "45")
    private Long regionId;

    @Schema(description = "赛事主页，支持 Markdown 格式")
    private String remark;

}
