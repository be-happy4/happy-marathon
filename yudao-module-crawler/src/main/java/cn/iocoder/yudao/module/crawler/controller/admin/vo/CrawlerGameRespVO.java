package cn.iocoder.yudao.module.crawler.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 临时赛事 Response VO")
@Data
public class CrawlerGameRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "数据源 ID")
    private Long sourceId;

    @Schema(description = "赛事名称")
    private String name;

    @Schema(description = "英文名称")
    private String nameEn;

    @Schema(description = "比赛日期")
    private LocalDate gameDate;

    @Schema(description = "地区名称")
    private String regionName;

    @Schema(description = "地区 ID")
    private Long regionId;

    @Schema(description = "世界田联等级")
    private String worldAthleticsLevel;

    @Schema(description = "中国田协等级")
    private String chinaRoadRunLevel;

    @Schema(description = "赛事状态")
    private String status;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "来源描述")
    private String remark;

    @Schema(description = "导入状态")
    private String importStatus;

    @Schema(description = "导入时间")
    private LocalDateTime importTime;

    @Schema(description = "导入后赛事 ID")
    private Long importGameId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "组别列表")
    private List<CrawlerGameCategoryVO> categories;

    @Data
    public static class CrawlerGameCategoryVO {
        @Schema(description = "组别 ID")
        private Long id;

        @Schema(description = "组别类型")
        private String gameType;

        @Schema(description = "距离(km)")
        private BigDecimal distanceKm;
    }
}
