package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 任务日志 Response VO")
@Data
public class CrawlerTaskLogRespVO {

    @Schema(description = "日志编号")
    private Long id;

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "爬取类型")
    private String crawlType;

    @Schema(description = "爬取模式")
    private String crawlMode;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "爬取原始条数")
    private Integer totalFetched;

    @Schema(description = "新增条数")
    private Integer totalNew;

    @Schema(description = "更新条数")
    private Integer totalUpdated;

    @Schema(description = "跳过条数")
    private Integer totalIgnored;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "ETL 版本")
    private String etlVersion;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
