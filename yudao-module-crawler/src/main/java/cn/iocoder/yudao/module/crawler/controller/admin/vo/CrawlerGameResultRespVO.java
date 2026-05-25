package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 临时成绩 Response VO")
@Data
public class CrawlerGameResultRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "临时赛事 ID")
    private Long crawlerGameId;

    @Schema(description = "赛事名称")
    private String gameName;

    @Schema(description = "赛事日期")
    private LocalDate gameDate;

    @Schema(description = "参赛号")
    private String bibNumber;

    @Schema(description = "选手姓名")
    private String name;

    @Schema(description = "英文名")
    private String nameEn;

    @Schema(description = "国籍")
    private String nationality;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "年龄组")
    private String ageGroup;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "护照号")
    private String passport;

    @Schema(description = "枪声成绩(毫秒)")
    private Long gunTimeMs;

    @Schema(description = "净成绩(毫秒)")
    private Long netTimeMs;

    @Schema(description = "总排名")
    private Integer rank;

    @Schema(description = "性别排名")
    private Integer genderRank;

    @Schema(description = "组别排名")
    private Integer categoryRank;

    @Schema(description = "匹配状态")
    private String matchStatus;

    @Schema(description = "匹配用户 ID")
    private Long matchUserId;

    @Schema(description = "匹配置信度")
    private String matchConfidence;

    @Schema(description = "匹配策略")
    private String matchStrategy;

    @Schema(description = "导入状态")
    private String importStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
