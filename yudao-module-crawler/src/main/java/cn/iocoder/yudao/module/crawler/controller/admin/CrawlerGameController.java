package cn.iocoder.yudao.module.crawler.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGamePageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameRespVO;
import cn.iocoder.yudao.module.crawler.convert.CrawlerGameConvert;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.service.CrawlerGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 爬虫赛事管理")
@RestController
@RequestMapping("/admin-api/crawler/game")
@Validated
public class CrawlerGameController {

    @Resource
    private CrawlerGameService crawlerGameService;

    @GetMapping("/page")
    @Operation(summary = "获取临时赛事分页")
    @PreAuthorize("@ss.hasPermission('crawler:game:query')")
    public CommonResult<PageResult<CrawlerGameRespVO>> getGamePage(@Valid CrawlerGamePageReqVO pageReqVO) {
        PageResult<CrawlerGameDO> page = crawlerGameService.getGamePage(pageReqVO);
        return success(CrawlerGameConvert.INSTANCE.convertPage(page));
    }
}
