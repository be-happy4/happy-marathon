package cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo;

import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 赛事组别分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameCategoryPageReqVO extends SortablePageParam {

    @Schema(description = "赛事编号", example = "1")
    private Long gameId;

}
