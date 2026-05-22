package cn.iocoder.yudao.module.system.dal.mysql.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GamePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameMapper extends BaseMapperX<GameDO> {

    default PageResult<GameDO> selectPage(GamePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GameDO>()
                .likeIfPresent(GameDO::getName, reqVO.getName())
                .betweenIfPresent(GameDO::getGameDate, reqVO.getGameDate())
                .eqIfPresent(GameDO::getGameType, reqVO.getGameType())
                .inIfPresent(GameDO::getGameType, reqVO.getGameTypes())
                .eqIfPresent(GameDO::getStatus, reqVO.getStatus())
                .inIfPresent(GameDO::getStatus, reqVO.getStatuses())
                .eqIfPresent(GameDO::getWorldAthleticsLevel, reqVO.getWorldAthleticsLevel())
                .inIfPresent(GameDO::getWorldAthleticsLevel, reqVO.getWorldAthleticsLevels())
                .eqIfPresent(GameDO::getChinaRoadRunLevel, reqVO.getChinaRoadRunLevel())
                .inIfPresent(GameDO::getChinaRoadRunLevel, reqVO.getChinaRoadRunLevels())
                .orderByDesc(GameDO::getId));
    }

}
