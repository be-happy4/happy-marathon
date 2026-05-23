package cn.iocoder.yudao.module.system.service.game;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategoryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategorySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;

import java.util.Collection;
import java.util.List;

public interface GameCategoryService {

    Long createGameCategory(GameCategorySaveReqVO createReqVO);

    void updateGameCategory(GameCategorySaveReqVO updateReqVO);

    void deleteGameCategory(Long id);

    List<GameCategoryDO> getCategoriesByGameId(Long gameId);

    List<GameCategoryDO> getCategoriesByIds(Collection<Long> gameIds);

    PageResult<GameCategoryDO> getGameCategoryPage(GameCategoryPageReqVO pageReqVO);

}
