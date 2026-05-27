package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Schema(description = "管理后台 - 临时成绩分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerGameResultPageReqVO extends SortablePageParam {

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "匹配状态(多选)", example = "[\"UNMATCHED\",\"MATCHED\"]")
    private List<String> matchStatuses;

    @Schema(description = "导入状态(多选)", example = "[\"PENDING\",\"CONFIRMED\"]")
    private List<String> importStatuses;

    @Schema(description = "选手姓名")
    private String name;

    @Schema(description = "赛事名称")
    private String gameName;

    @Schema(description = "身份证号")
    private String idCard;
}
