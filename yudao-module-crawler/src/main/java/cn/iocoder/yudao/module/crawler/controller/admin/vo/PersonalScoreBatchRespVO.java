package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 个人成绩批量查询 Response VO")
@Data
@Builder
public class PersonalScoreBatchRespVO {

    @Schema(description = "状态: OK / CAPTCHA_REQUIRED / ERROR")
    private String status;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "每人查询结果")
    private List<PersonResult> personResults;

    @Schema(description = "汇总")
    private BatchSummary summary;

    @Data
    @Builder
    public static class PersonResult {
        @Schema(description = "姓名")
        private String name;
        @Schema(description = "身份证号(脱敏)")
        private String idCardMasked;
        @Schema(description = "查询状态")
        private String queryStatus;       // OK / ERROR / NO_RESULT
        @Schema(description = "错误信息")
        private String errorMsg;
        @Schema(description = "成绩条数")
        private int resultCount;
        @Schema(description = "成绩详情")
        private List<PersonalScoreQueryRespVO.ResultItem> results;
    }

    @Data
    @Builder
    public static class BatchSummary {
        @Schema(description = "查询人数")
        private int totalPersons;
        @Schema(description = "成功人数")
        private int successPersons;
        @Schema(description = "总成绩条数")
        private int totalResults;
        @Schema(description = "已匹配")
        private int matched;
        @Schema(description = "未匹配")
        private int unmatched;
    }
}
