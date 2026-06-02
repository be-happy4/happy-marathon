package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 个人成绩批量查询 Request VO")
@Data
public class PersonalScoreBatchReqVO {

    @Schema(description = "查询请求列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "查询列表不能为空")
    @Valid
    private List<PersonItem> persons;

    @Data
    public static class PersonItem {
        @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
        @jakarta.validation.constraints.NotBlank(message = "姓名不能为空")
        private String name;

        @Schema(description = "身份证号")
        private String idCard;
    }
}
