package cn.iocoder.yudao.module.system.controller.admin.gameregistration;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationRespVO;
import cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo.GameRegistrationSaveReqVO;
import cn.iocoder.yudao.module.system.service.game.GameRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 赛事报名")
@RestController
@RequestMapping("/system/game-registration")
@Validated
public class GameRegistrationController {

    @Resource
    private GameRegistrationService gameRegistrationService;

    @PostMapping("/create")
    @Operation(summary = "创建赛事报名")
    @PreAuthorize("@ss.hasPermission('system:game-registration:create')")
    public CommonResult<Long> createGameRegistration(@Valid @RequestBody GameRegistrationSaveReqVO createReqVO) {
        return success(gameRegistrationService.createGameRegistration(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新赛事报名")
    @PreAuthorize("@ss.hasPermission('system:game-registration:update')")
    public CommonResult<Boolean> updateGameRegistration(@Valid @RequestBody GameRegistrationSaveReqVO updateReqVO) {
        gameRegistrationService.updateGameRegistration(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除赛事报名")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:game-registration:delete')")
    public CommonResult<Boolean> deleteGameRegistration(@RequestParam("id") Long id) {
        gameRegistrationService.deleteGameRegistration(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除赛事报名")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:game-registration:delete')")
    public CommonResult<Boolean> deleteGameRegistrationList(@RequestParam("ids") List<Long> ids) {
        gameRegistrationService.deleteGameRegistrationList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得赛事报名")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:game-registration:query')")
    public CommonResult<GameRegistrationRespVO> getGameRegistration(@RequestParam("id") Long id) {
        return success(gameRegistrationService.getGameRegistrationDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得赛事报名分页")
    @PreAuthorize("@ss.hasPermission('system:game-registration:query')")
    public CommonResult<PageResult<GameRegistrationRespVO>> getGameRegistrationPage(
            @Valid GameRegistrationPageReqVO pageReqVO) {
        return success(gameRegistrationService.getGameRegistrationPage(pageReqVO));
    }

}
