package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.module.system.dal.dataobject.game.RegionDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.RegionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private RegionMapper regionMapper;

    @Override
    public List<RegionDO> getRegionTree() {
        return regionMapper.selectAll();
    }

    @Override
    public RegionDO getRegion(Long id) {
        return regionMapper.selectById(id);
    }

    @Override
    public List<RegionDO> getRegionList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return regionMapper.selectBatchIds(ids);
    }

}
