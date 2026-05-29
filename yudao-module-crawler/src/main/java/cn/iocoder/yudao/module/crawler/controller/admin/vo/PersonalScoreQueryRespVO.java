package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 个人成绩查询 Response VO")
@Data
@Builder
public class PersonalScoreQueryRespVO {

    @Schema(description = "查询状态：OK / CAPTCHA_REQUIRED / NO_RESULT / ERROR")
    private String status;

    @Schema(description = "状态描述")
    private String message;

    @Schema(description = "查询到的成绩列表")
    private List<ResultItem> results;

    @Schema(description = "匹配摘要")
    private MatchSummary matchSummary;

    @Data
    @Builder
    public static class ResultItem {
        @Schema(description = "赛事名称")
        private String gameName;

        @Schema(description = "赛事日期")
        private String gameDate;

        @Schema(description = "参赛号")
        private String bibNumber;

        @Schema(description = "枪声成绩")
        private String gunTime;

        @Schema(description = "净成绩")
        private String netTime;

        @Schema(description = "总排名")
        private Integer rank;

        @Schema(description = "组别")
        private String ageGroup;

        @Schema(description = "匹配状态")
        private String matchStatus;

        @Schema(description = "匹配到的用户名")
        private String matchedUser;
    }

    @Data
    @Builder
    public static class MatchSummary {
        @Schema(description = "查询到的总记录数")
        private int totalFound;

        @Schema(description = "自动匹配成功的记录数")
        private int autoMatched;

        @Schema(description = "未匹配的记录数")
        private int unmatched;
    }
}
