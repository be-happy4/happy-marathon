package cn.iocoder.yudao.module.system.controller.admin.game;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GamePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameRespVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSimpleRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.service.game.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 赛事")
@RestController
@RequestMapping("/system/game")
@Validated
public class GameController {

    @Resource
    private GameService gameService;

    @PostMapping("/create")
    @Operation(summary = "创建赛事")
    @PreAuthorize("@ss.hasPermission('system:game:create')")
    public CommonResult<Long> createGame(@Valid @RequestBody GameSaveReqVO createReqVO) {
        return success(gameService.createGame(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新赛事")
    @PreAuthorize("@ss.hasPermission('system:game:update')")
    public CommonResult<Boolean> updateGame(@Valid @RequestBody GameSaveReqVO updateReqVO) {
        gameService.updateGame(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除赛事")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:game:delete')")
    public CommonResult<Boolean> deleteGame(@RequestParam("id") Long id) {
        gameService.deleteGame(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除赛事")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:game:delete')")
    public CommonResult<Boolean> deleteGameList(@RequestParam("ids") List<Long> ids) {
        gameService.deleteGameList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得赛事")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:game:query')")
    public CommonResult<GameRespVO> getGame(@RequestParam("id") Long id) {
        GameDO game = gameService.getGame(id);
        return success(BeanUtils.toBean(game, GameRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得赛事分页")
    @PreAuthorize("@ss.hasPermission('system:game:query')")
    public CommonResult<PageResult<GameRespVO>> getGamePage(@Valid GamePageReqVO pageReqVO) {
        PageResult<GameDO> pageResult = gameService.getGamePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, GameRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得赛事精简列表", description = "用于下拉选择")
    public CommonResult<List<GameSimpleRespVO>> getGameSimpleList() {
        GamePageReqVO pageReqVO = new GamePageReqVO();
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GameDO> list = gameService.getGamePage(pageReqVO).getList();
        list.sort(Comparator.comparing(GameDO::getGameDate, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(GameDO::getId, Comparator.reverseOrder()));
        return success(BeanUtils.toBean(list, GameSimpleRespVO.class));
    }

}
