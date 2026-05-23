package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategoryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategorySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.GameCategoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.GAME_CATEGORY_NOT_FOUND;

@Service
public class GameCategoryServiceImpl implements GameCategoryService {

    @Resource
    private GameCategoryMapper gameCategoryMapper;

    @Override
    public Long createGameCategory(GameCategorySaveReqVO createReqVO) {
        GameCategoryDO category = BeanUtils.toBean(createReqVO, GameCategoryDO.class);
        gameCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateGameCategory(GameCategorySaveReqVO updateReqVO) {
        validateExists(updateReqVO.getId());
        GameCategoryDO updateObj = BeanUtils.toBean(updateReqVO, GameCategoryDO.class);
        gameCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteGameCategory(Long id) {
        validateExists(id);
        gameCategoryMapper.deleteById(id);
    }

    @Override
    public List<GameCategoryDO> getCategoriesByGameId(Long gameId) {
        return gameCategoryMapper.selectByGameId(gameId);
    }

    @Override
    public List<GameCategoryDO> getCategoriesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return gameCategoryMapper.selectByIds(ids);
    }

    @Override
    public PageResult<GameCategoryDO> getGameCategoryPage(GameCategoryPageReqVO pageReqVO) {
        return gameCategoryMapper.selectPage(pageReqVO);
    }

    private void validateExists(Long id) {
        if (id == null) {
            return;
        }
        if (gameCategoryMapper.selectById(id) == null) {
            throw exception(GAME_CATEGORY_NOT_FOUND);
        }
    }

}
