package cn.iocoder.yudao.module.system.dal.mysql.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GameRegistrationMapper extends BaseMapperX<GameRegistrationDO> {

    default PageResult<GameRegistrationDO> selectPage(GameRegistrationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GameRegistrationDO>()
                .eqIfPresent(GameRegistrationDO::getGameId, reqVO.getGameId())
                .eqIfPresent(GameRegistrationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(GameRegistrationDO::getRegistrationStatus, reqVO.getRegistrationStatus())
                .eqIfPresent(GameRegistrationDO::getGameType, reqVO.getGameType())
                .orderByDesc(GameRegistrationDO::getId));
    }

}
