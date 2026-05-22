package cn.iocoder.yudao.module.system.controller.admin.region;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.region.vo.RegionNodeRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.RegionDO;
import cn.iocoder.yudao.module.system.service.game.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 地区")
@RestController
@RequestMapping("/system/region")
@Validated
public class RegionController {

    @Resource
    private RegionService regionService;

    @GetMapping("/tree")
    @Operation(summary = "获得地区树")
    @PreAuthorize("@ss.hasPermission('system:region:query')")
    public CommonResult<List<RegionNodeRespVO>> getRegionTree() {
        List<RegionDO> allRegions = regionService.getRegionTree();
        Map<Long, RegionNodeRespVO> nodeMap = new LinkedHashMap<>();
        List<RegionNodeRespVO> roots = new ArrayList<>();

        for (RegionDO r : allRegions) {
            RegionNodeRespVO node = new RegionNodeRespVO();
            node.setId(r.getId());
            node.setName(r.getName());
            node.setCode(r.getCode());
            node.setType(r.getType());
            nodeMap.put(r.getId(), node);
        }

        for (RegionDO r : allRegions) {
            RegionNodeRespVO node = nodeMap.get(r.getId());
            if (r.getParentId() == null || r.getParentId() == 0) {
                roots.add(node);
            } else {
                RegionNodeRespVO parent = nodeMap.get(r.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return success(roots);
    }

}
