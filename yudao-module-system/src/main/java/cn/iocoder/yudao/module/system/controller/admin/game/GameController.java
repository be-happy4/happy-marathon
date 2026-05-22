package cn.iocoder.yudao.module.system.controller.admin.game;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameCategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameExportVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameImportExcelVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GamePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameRespVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.game.vo.GameSimpleRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.RegionDO;
import cn.iocoder.yudao.module.system.service.game.GameCategoryService;
import cn.iocoder.yudao.module.system.service.game.GameService;
import cn.iocoder.yudao.module.system.service.game.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 赛事")
@RestController
@RequestMapping("/system/game")
@Validated
public class GameController {

    @Resource
    private GameService gameService;
    @Resource
    private GameCategoryService gameCategoryService;
    @Resource
    private RegionService regionService;

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
        GameRespVO vo = BeanUtils.toBean(game, GameRespVO.class);
        enrichRegionName(vo);
        return success(vo);
    }

    @GetMapping("/page")
    @Operation(summary = "获得赛事分页")
    @PreAuthorize("@ss.hasPermission('system:game:query')")
    public CommonResult<PageResult<GameRespVO>> getGamePage(@Valid GamePageReqVO pageReqVO) {
        PageResult<GameDO> pageResult = gameService.getGamePage(pageReqVO);
        PageResult<GameRespVO> result = BeanUtils.toBean(pageResult, GameRespVO.class);
        Set<Long> regionIds = result.getList().stream().map(GameRespVO::getRegionId).filter(id -> id != null).collect(Collectors.toSet());
        if (!regionIds.isEmpty()) {
            Map<Long, RegionDO> regionMap = regionService.getRegionList(regionIds).stream()
                    .collect(Collectors.toMap(RegionDO::getId, Function.identity()));
            result.getList().forEach(vo -> {
                if (vo.getRegionId() != null) {
                    RegionDO region = regionMap.get(vo.getRegionId());
                    if (region != null) {
                        vo.setRegionName(region.getName());
                    }
                }
            });
        }
        return success(result);
    }

    private void enrichRegionName(GameRespVO vo) {
        if (vo.getRegionId() != null) {
            RegionDO region = regionService.getRegion(vo.getRegionId());
            if (region != null) {
                vo.setRegionName(region.getName());
            }
        }
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

    @GetMapping("/categories")
    @Operation(summary = "获得赛事组别列表")
    @Parameter(name = "gameId", description = "赛事编号", required = true)
    public CommonResult<List<GameCategoryRespVO>> getCategories(@RequestParam("gameId") Long gameId) {
        List<GameCategoryDO> categories = gameCategoryService.getCategoriesByGameId(gameId);
        return success(BeanUtils.toBean(categories, GameCategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出赛事 Excel")
    @PreAuthorize("@ss.hasPermission('system:game:export')")
    public void exportExcel(@Valid GamePageReqVO reqVO, HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<GameDO> list = gameService.getGamePage(reqVO).getList();
        Map<Long, RegionDO> regionMap = list.stream()
                .filter(g -> g.getRegionId() != null)
                .collect(Collectors.toMap(GameDO::getRegionId, g -> regionService.getRegion(g.getRegionId()), (a, b) -> a));
        List<GameExportVO> exportList = new java.util.ArrayList<>();
        for (GameDO game : list) {
            GameExportVO vo = BeanUtils.toBean(game, GameExportVO.class);
            if (game.getRegionId() != null) {
                RegionDO region = regionMap.get(game.getRegionId());
                if (region != null) vo.setRegionName(region.getName());
            }
            exportList.add(vo);
        }
        ExcelUtils.write(response, "赛事数据.xls", "赛事", GameExportVO.class, exportList);
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入赛事模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        List<GameImportExcelVO> list = Arrays.asList(
                createTemplate("示例马拉松", "2027-01-01", "marathon", "", "", "uncertain", "", "https://example.com", ""),
                createTemplate("示例半马", "2027-06-01", "half_marathon", "gold", "a1", "announced", "wmm", "https://example.com", "")
        );
        ExcelUtils.write(response, "赛事导入模板.xls", "赛事", GameImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入赛事")
    @PreAuthorize("@ss.hasPermission('system:game:import')")
    public CommonResult<String> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        List<GameImportExcelVO> list = ExcelUtils.read(file, GameImportExcelVO.class);
        int count = 0;
        for (GameImportExcelVO vo : list) {
            GameSaveReqVO reqVO = new GameSaveReqVO();
            reqVO.setName(vo.getName());
            reqVO.setGameDate(vo.getGameDate() != null && !vo.getGameDate().isEmpty()
                    ? java.time.LocalDate.parse(vo.getGameDate()) : null);
            reqVO.setGameType(vo.getGameType());
            reqVO.setWorldAthleticsLevel(vo.getWorldAthleticsLevel());
            reqVO.setChinaRoadRunLevel(vo.getChinaRoadRunLevel());
            reqVO.setStatus(vo.getStatus() != null ? vo.getStatus() : "uncertain");
            reqVO.setTags(vo.getTags());
            reqVO.setUrl(vo.getUrl());
            reqVO.setRemark(vo.getRemark());
            gameService.createGame(reqVO);
            count++;
        }
        return success("成功导入 " + count + " 条赛事");
    }

    private GameImportExcelVO createTemplate(String name, String date, String type, String wal, String crrl,
                                              String status, String tags, String url, String remark) {
        GameImportExcelVO vo = new GameImportExcelVO();
        vo.setName(name); vo.setGameDate(date); vo.setGameType(type);
        vo.setWorldAthleticsLevel(wal); vo.setChinaRoadRunLevel(crrl);
        vo.setStatus(status); vo.setTags(tags); vo.setUrl(url); vo.setRemark(remark);
        return vo;
    }

}
