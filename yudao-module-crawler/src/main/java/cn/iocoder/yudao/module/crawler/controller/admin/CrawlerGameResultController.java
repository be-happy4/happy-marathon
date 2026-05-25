package cn.iocoder.yudao.module.crawler.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultPageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultRespVO;
import cn.iocoder.yudao.module.crawler.convert.CrawlerGameResultConvert;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.service.CrawlerGameResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 爬虫成绩管理")
@RestController
@RequestMapping("/admin-api/crawler/result")
@Validated
public class CrawlerGameResultController {

    @Resource
    private CrawlerGameResultService crawlerGameResultService;

    @GetMapping("/page")
    @Operation(summary = "获取临时成绩分页")
    @PreAuthorize("@ss.hasPermission('crawler:result:query')")
    public CommonResult<PageResult<CrawlerGameResultRespVO>> getResultPage(@Valid CrawlerGameResultPageReqVO pageReqVO) {
        PageResult<CrawlerGameResultDO> page = crawlerGameResultService.getResultPage(pageReqVO);
        return success(CrawlerGameResultConvert.INSTANCE.convertPage(page));
    }

    @PostMapping("/confirm")
    @Operation(summary = "批量确认导入成绩")
    @PreAuthorize("@ss.hasPermission('crawler:result:confirm')")
    public CommonResult<Integer> confirmImport(@RequestParam("ids") List<Long> ids,
                                               @RequestParam("userId") Long userId) {
        return success(crawlerGameResultService.confirmImport(ids, userId));
    }

    @PostMapping("/ignore")
    @Operation(summary = "批量忽略成绩")
    @PreAuthorize("@ss.hasPermission('crawler:result:ignore')")
    public CommonResult<Integer> ignoreResults(@RequestParam("ids") List<Long> ids) {
        return success(crawlerGameResultService.ignoreResults(ids));
    }

    @PostMapping("/manual-match")
    @Operation(summary = "手动匹配用户")
    @PreAuthorize("@ss.hasPermission('crawler:result:match')")
    public CommonResult<Boolean> manualMatch(@RequestParam("resultId") Long resultId,
                                             @RequestParam("userId") Long userId) {
        crawlerGameResultService.manualMatch(resultId, userId);
        return success(true);
    }
}
