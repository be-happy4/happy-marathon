package cn.iocoder.yudao.module.system.controller.admin.region.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - 地区节点 Response VO")
@Data
public class RegionNodeRespVO {

    @Schema(description = "地区编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "地区名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国")
    private String name;

    @Schema(description = "ISO 3166 代码", example = "CN")
    private String code;

    @Schema(description = "地区类型：1=洲 2=国家 3=省份/州 4=城市", example = "2")
    private Integer type;

    @Schema(description = "子节点")
    private List<RegionNodeRespVO> children = new ArrayList<>();

}
