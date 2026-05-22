package cn.iocoder.yudao.module.system.dal.mysql.game;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.game.RegionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RegionMapper extends BaseMapperX<RegionDO> {

    default List<RegionDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<RegionDO>()
                .orderByAsc(RegionDO::getSort));
    }

    default List<RegionDO> selectByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<RegionDO>()
                .eq(RegionDO::getParentId, parentId)
                .orderByAsc(RegionDO::getSort));
    }

}
