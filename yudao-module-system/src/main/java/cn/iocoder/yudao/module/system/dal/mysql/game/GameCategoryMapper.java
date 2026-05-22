package cn.iocoder.yudao.module.system.dal.mysql.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategoryPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GameCategoryMapper extends BaseMapperX<GameCategoryDO> {

    default List<GameCategoryDO> selectByGameId(Long gameId) {
        return selectList(new LambdaQueryWrapperX<GameCategoryDO>()
                .eq(GameCategoryDO::getGameId, gameId));
    }

    default List<GameCategoryDO> selectByGameIds(Collection<Long> gameIds) {
        return selectList(new LambdaQueryWrapperX<GameCategoryDO>()
                .in(GameCategoryDO::getGameId, gameIds));
    }

    default PageResult<GameCategoryDO> selectPage(GameCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GameCategoryDO>()
                .eqIfPresent(GameCategoryDO::getGameId, reqVO.getGameId())
                .orderByAsc(GameCategoryDO::getId));
    }

}
