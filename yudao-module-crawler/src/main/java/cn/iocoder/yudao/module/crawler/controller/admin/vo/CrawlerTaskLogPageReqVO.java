package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 任务日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrawlerTaskLogPageReqVO extends SortablePageParam {

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "爬取类型", example = "GAME")
    private String crawlType;

    @Schema(description = "状态", example = "SUCCESS")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;
}
