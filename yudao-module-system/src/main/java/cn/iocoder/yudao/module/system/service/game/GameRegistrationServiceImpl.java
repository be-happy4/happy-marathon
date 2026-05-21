package cn.iocoder.yudao.module.system.service.game;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationRespVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.GameRegistrationMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.GAME_NOT_FOUND;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.GAME_REGISTRATION_NOT_FOUND;

@Service
@Validated
public class GameRegistrationServiceImpl implements GameRegistrationService {

    @Resource
    private GameRegistrationMapper gameRegistrationMapper;
    @Resource
    private GameService gameService;
    @Resource
    private AdminUserService adminUserService;

    @Override
    public Long createGameRegistration(GameRegistrationSaveReqVO createReqVO) {
        validateGameExists(createReqVO.getGameId());
        GameRegistrationDO registration = BeanUtils.toBean(createReqVO, GameRegistrationDO.class);
        gameRegistrationMapper.insert(registration);
        return registration.getId();
    }

    @Override
    public void updateGameRegistration(GameRegistrationSaveReqVO updateReqVO) {
        validateGameRegistrationExists(updateReqVO.getId());
        validateGameExists(updateReqVO.getGameId());
        GameRegistrationDO updateObj = BeanUtils.toBean(updateReqVO, GameRegistrationDO.class);
        gameRegistrationMapper.updateById(updateObj);
    }

    @Override
    public void deleteGameRegistration(Long id) {
        validateGameRegistrationExists(id);
        gameRegistrationMapper.deleteById(id);
    }

    @Override
    public void deleteGameRegistrationList(List<Long> ids) {
        gameRegistrationMapper.deleteByIds(ids);
    }

    @Override
    public GameRegistrationDO getGameRegistration(Long id) {
        return gameRegistrationMapper.selectById(id);
    }

    @Override
    public GameRegistrationRespVO getGameRegistrationDetail(Long id) {
        GameRegistrationDO registration = gameRegistrationMapper.selectById(id);
        if (registration == null) {
            throw exception(GAME_REGISTRATION_NOT_FOUND);
        }
        return buildRegistrationRespVOList(List.of(registration)).get(0);
    }

    @Override
    public PageResult<GameRegistrationRespVO> getGameRegistrationPage(GameRegistrationPageReqVO pageReqVO) {
        PageResult<GameRegistrationDO> pageResult = gameRegistrationMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        Set<Long> gameIds = CollectionUtils.convertSet(pageResult.getList(), GameRegistrationDO::getGameId);
        Set<Long> userIds = CollectionUtils.convertSet(pageResult.getList(), GameRegistrationDO::getUserId);
        Map<Long, GameDO> gameMap = CollectionUtils.convertMap(gameService.getGameList(gameIds), GameDO::getId);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(adminUserService.getUserList(userIds), AdminUserDO::getId);

        return new PageResult<>(buildRegistrationRespVOList(pageResult.getList(), gameMap, userMap), pageResult.getTotal());
    }

    private List<GameRegistrationRespVO> buildRegistrationRespVOList(List<GameRegistrationDO> registrations) {
        if (CollUtil.isEmpty(registrations)) {
            return List.of();
        }
        Set<Long> gameIds = CollectionUtils.convertSet(registrations, GameRegistrationDO::getGameId);
        Set<Long> userIds = CollectionUtils.convertSet(registrations, GameRegistrationDO::getUserId);
        Map<Long, GameDO> gameMap = CollectionUtils.convertMap(gameService.getGameList(gameIds), GameDO::getId);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(adminUserService.getUserList(userIds), AdminUserDO::getId);
        return buildRegistrationRespVOList(registrations, gameMap, userMap);
    }

    private List<GameRegistrationRespVO> buildRegistrationRespVOList(List<GameRegistrationDO> registrations,
                                                                       Map<Long, GameDO> gameMap,
                                                                       Map<Long, AdminUserDO> userMap) {
        return CollectionUtils.convertList(registrations, registration -> {
            GameRegistrationRespVO respVO = BeanUtils.toBean(registration, GameRegistrationRespVO.class);
            GameDO game = gameMap.get(registration.getGameId());
            if (game != null) {
                respVO.setGameName(game.getName());
            }
            AdminUserDO user = userMap.get(registration.getUserId());
            if (user != null) {
                respVO.setUserNickname(user.getNickname());
            }
            return respVO;
        });
    }

    private void validateGameRegistrationExists(Long id) {
        if (id == null) {
            return;
        }
        if (gameRegistrationMapper.selectById(id) == null) {
            throw exception(GAME_REGISTRATION_NOT_FOUND);
        }
    }

    private void validateGameExists(Long gameId) {
        if (gameId == null) {
            return;
        }
        if (gameService.getGame(gameId) == null) {
            throw exception(GAME_NOT_FOUND);
        }
    }

}
