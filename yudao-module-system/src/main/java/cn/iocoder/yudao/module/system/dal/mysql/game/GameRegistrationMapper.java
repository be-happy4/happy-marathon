package cn.iocoder.yudao.module.system.dal.mysql.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameRegistrationMapper extends BaseMapperX<GameRegistrationDO> {

    default PageResult<GameRegistrationDO> selectPage(GameRegistrationPageReqVO reqVO) {
        MPJLambdaWrapperX<GameRegistrationDO> query = new MPJLambdaWrapperX<GameRegistrationDO>()
                .eqIfPresent(GameRegistrationDO::getGameId, reqVO.getGameId())
                .inIfPresent(GameRegistrationDO::getGameId, reqVO.getGameIds())
                .eqIfPresent(GameRegistrationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(GameRegistrationDO::getRegistrationStatus, reqVO.getRegistrationStatus())
                .inIfPresent(GameRegistrationDO::getRegistrationStatus, reqVO.getRegistrationStatuses())                .eqIfPresent(GameRegistrationDO::getGameCategoryId, reqVO.getGameCategoryId())
                .inIfPresent(GameRegistrationDO::getGameCategoryId, reqVO.getGameCategoryIds())
                // LEFT JOIN game for date filter
                .leftJoin(GameDO.class, GameDO::getId, GameRegistrationDO::getGameId)
                .betweenIfPresent(GameDO::getGameDate, reqVO.getGameDate())
                .orderByDesc(GameRegistrationDO::getId);
        return selectJoinPage(reqVO, GameRegistrationDO.class, query);
    }

}
