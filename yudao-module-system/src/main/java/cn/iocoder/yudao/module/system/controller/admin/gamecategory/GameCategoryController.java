package cn.iocoder.yudao.module.system.controller.admin.gamecategory;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameCategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategoryPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gamecategory.vo.GameCategorySaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.service.game.GameCategoryService;
import cn.iocoder.yudao.module.system.service.game.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 赛事组别")
@RestController
@RequestMapping("/system/game-category")
@Validated
public class GameCategoryController {

    @Resource
    private GameCategoryService gameCategoryService;
    @Resource
    private GameService gameService;

    @PostMapping("/create")
    @Operation(summary = "创建赛事组别")
    @PreAuthorize("@ss.hasPermission('system:game-category:create')")
    public CommonResult<Long> createGameCategory(@Valid @RequestBody GameCategorySaveReqVO createReqVO) {
        return success(gameCategoryService.createGameCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新赛事组别")
    @PreAuthorize("@ss.hasPermission('system:game-category:update')")
    public CommonResult<Boolean> updateGameCategory(@Valid @RequestBody GameCategorySaveReqVO updateReqVO) {
        gameCategoryService.updateGameCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除赛事组别")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:game-category:delete')")
    public CommonResult<Boolean> deleteGameCategory(@RequestParam("id") Long id) {
        gameCategoryService.deleteGameCategory(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得赛事组别分页")
    @PreAuthorize("@ss.hasPermission('system:game-category:query')")
    public CommonResult<PageResult<GameCategoryRespVO>> getGameCategoryPage(@Valid GameCategoryPageReqVO pageReqVO) {
        PageResult<GameCategoryDO> pageResult = gameCategoryService.getGameCategoryPage(pageReqVO);
        PageResult<GameCategoryRespVO> result = BeanUtils.toBean(pageResult, GameCategoryRespVO.class);
        enrichGameInfo(result.getList());
        return success(result);
    }

    @GetMapping("/list")
    @Operation(summary = "获得赛事组别列表")
    @Parameter(name = "gameId", description = "赛事编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:game-category:query')")
    public CommonResult<List<GameCategoryRespVO>> getGameCategoryList(@RequestParam("gameId") Long gameId) {
        List<GameCategoryDO> categories = gameCategoryService.getCategoriesByGameId(gameId);
        List<GameCategoryRespVO> result = BeanUtils.toBean(categories, GameCategoryRespVO.class);
        enrichGameInfo(result);
        return success(result);
    }

    private void enrichGameInfo(List<GameCategoryRespVO> list) {
        Set<Long> gameIds = list.stream().map(GameCategoryRespVO::getGameId).collect(Collectors.toSet());
        Map<Long, GameDO> gameMap = gameService.getGameList(gameIds).stream()
                .collect(Collectors.toMap(GameDO::getId, Function.identity()));
        list.forEach(vo -> {
            GameDO game = gameMap.get(vo.getGameId());
            if (game != null) {
                vo.setGameName(game.getName());
                vo.setGameDate(game.getGameDate());
            }
        });
    }

}
