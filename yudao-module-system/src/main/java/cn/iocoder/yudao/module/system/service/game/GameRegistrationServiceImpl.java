package cn.iocoder.yudao.module.system.service.game;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationRespVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.GameRegistrationMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private GameCategoryService gameCategoryService;
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
        return buildDetail(registration);
    }

    @Override
    public PageResult<GameRegistrationRespVO> getGameRegistrationPage(GameRegistrationPageReqVO pageReqVO) {
        // Strip computed fields from sorting (not supported at DB level)
        if (pageReqVO.getSortingFields() != null) {
            pageReqVO.getSortingFields().removeIf(sf -> "speed".equals(sf.getField()));
        }

        PageResult<GameRegistrationDO> pageResult = gameRegistrationMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }

        // Batch load related data
        Set<Long> gameIds = CollectionUtils.convertSet(pageResult.getList(), GameRegistrationDO::getGameId);
        Set<Long> userIds = CollectionUtils.convertSet(pageResult.getList(), GameRegistrationDO::getUserId);
        Set<Long> categoryIds = CollectionUtils.convertSet(pageResult.getList(), GameRegistrationDO::getGameCategoryId);
        Map<Long, GameDO> gameMap = CollectionUtils.convertMap(gameService.getGameList(gameIds), GameDO::getId);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(adminUserService.getUserList(userIds), AdminUserDO::getId);
        Map<Long, GameCategoryDO> categoryMap = CollectionUtils.convertMap(
                gameCategoryService.getCategoriesByGameIds(categoryIds), GameCategoryDO::getId);

        // Build response VOs
        List<GameRegistrationRespVO> resultList = CollectionUtils.convertList(pageResult.getList(), registration -> {
            GameRegistrationRespVO vo = BeanUtils.toBean(registration, GameRegistrationRespVO.class);
            GameDO game = gameMap.get(registration.getGameId());
            if (game != null) {
                vo.setGameName(game.getName());
                vo.setGameDate(game.getGameDate());
            }
            AdminUserDO user = userMap.get(registration.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
            }
            GameCategoryDO category = categoryMap.get(registration.getGameCategoryId());
            if (category != null) {
                vo.setGameCategoryName(category.getGameType());
                vo.setCategoryDistanceM(category.getDistanceM());
            }
            computePaceAndSpeed(vo);
            return vo;
        });

        return new PageResult<>(resultList, pageResult.getTotal());
    }

    private GameRegistrationRespVO buildDetail(GameRegistrationDO registration) {
        Set<Long> gameIds = Set.of(registration.getGameId());
        Set<Long> userIds = Set.of(registration.getUserId());
        Set<Long> categoryIds = registration.getGameCategoryId() != null
                ? Set.of(registration.getGameCategoryId()) : Set.of();
        Map<Long, GameDO> gameMap = CollectionUtils.convertMap(gameService.getGameList(gameIds), GameDO::getId);
        Map<Long, AdminUserDO> userMap = CollectionUtils.convertMap(adminUserService.getUserList(userIds), AdminUserDO::getId);
        Map<Long, GameCategoryDO> categoryMap = CollectionUtils.convertMap(
                gameCategoryService.getCategoriesByGameIds(categoryIds), GameCategoryDO::getId);

        GameRegistrationRespVO vo = BeanUtils.toBean(registration, GameRegistrationRespVO.class);
        GameDO game = gameMap.get(registration.getGameId());
        if (game != null) {
            vo.setGameName(game.getName());
            vo.setGameDate(game.getGameDate());
        }
        AdminUserDO user = userMap.get(registration.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        GameCategoryDO category = categoryMap.get(registration.getGameCategoryId());
        if (category != null) {
            vo.setGameCategoryName(category.getGameType());
            vo.setCategoryDistanceM(category.getDistanceM());
        }
        computePaceAndSpeed(vo);
        return vo;
    }

    private void computePaceAndSpeed(GameRegistrationRespVO vo) {
        if (vo.getNetTimeMs() != null && vo.getNetTimeMs() > 0
                && vo.getCategoryDistanceM() != null
                && vo.getCategoryDistanceM().compareTo(BigDecimal.ZERO) > 0) {
            double distanceKm = vo.getCategoryDistanceM().doubleValue() / 1000.0;
            double netTimeSeconds = vo.getNetTimeMs() / 1000.0;
            double paceSeconds = netTimeSeconds / distanceKm;
            long paceMin = (long) (paceSeconds / 60);
            long paceSec = (long) (paceSeconds % 60);
            vo.setPace(String.format("%d:%02d", paceMin, paceSec));
            double speed = distanceKm / (netTimeSeconds / 3600.0);
            vo.setSpeed(BigDecimal.valueOf(speed).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
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
