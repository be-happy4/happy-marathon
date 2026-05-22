package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.module.system.dal.dataobject.game.RegionDO;

import java.util.Collection;
import java.util.List;

public interface RegionService {

    List<RegionDO> getRegionTree();

    RegionDO getRegion(Long id);

    List<RegionDO> getRegionList(Collection<Long> ids);

}
