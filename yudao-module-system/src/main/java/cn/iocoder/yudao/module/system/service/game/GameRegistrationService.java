package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationRespVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import jakarta.validation.Valid;

import java.util.List;

public interface GameRegistrationService {

    Long createGameRegistration(@Valid GameRegistrationSaveReqVO createReqVO);

    void updateGameRegistration(@Valid GameRegistrationSaveReqVO updateReqVO);

    void deleteGameRegistration(Long id);

    void deleteGameRegistrationList(List<Long> ids);

    GameRegistrationDO getGameRegistration(Long id);

    GameRegistrationRespVO getGameRegistrationDetail(Long id);

    PageResult<GameRegistrationRespVO> getGameRegistrationPage(GameRegistrationPageReqVO pageReqVO);

}
