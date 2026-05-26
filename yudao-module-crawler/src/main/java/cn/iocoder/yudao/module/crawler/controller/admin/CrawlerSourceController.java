package cn.iocoder.yudao.module.crawler.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.*;
import cn.iocoder.yudao.module.crawler.convert.CrawlerSourceConvert;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import cn.iocoder.yudao.module.crawler.job.CrawlGameJob;
import cn.iocoder.yudao.module.crawler.service.CrawlerSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 爬虫数据源")
@RestController
@RequestMapping("/admin-api/crawler/source")
@Validated
public class CrawlerSourceController {

    @Resource
    private CrawlerSourceService crawlerSourceService;

    @Resource
    private CrawlGameJob crawlGameJob;

    @PostMapping("/create")
    @Operation(summary = "创建数据源")
    @PreAuthorize("@ss.hasPermission('crawler:source:create')")
    public CommonResult<Long> createSource(@Valid @RequestBody CrawlerSourceSaveReqVO reqVO) {
        return success(crawlerSourceService.createSource(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新数据源")
    @PreAuthorize("@ss.hasPermission('crawler:source:update')")
    public CommonResult<Boolean> updateSource(@Valid @RequestBody CrawlerSourceSaveReqVO reqVO) {
        crawlerSourceService.updateSource(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除数据源")
    @PreAuthorize("@ss.hasPermission('crawler:source:delete')")
    public CommonResult<Boolean> deleteSource(@RequestParam("id") Long id) {
        crawlerSourceService.deleteSource(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取数据源详情")
    @PreAuthorize("@ss.hasPermission('crawler:source:query')")
    public CommonResult<CrawlerSourceRespVO> getSource(@RequestParam("id") Long id) {
        CrawlerSourceDO source = crawlerSourceService.getSource(id);
        return success(CrawlerSourceConvert.INSTANCE.convert(source));
    }

    @GetMapping("/page")
    @Operation(summary = "获取数据源分页")
    @PreAuthorize("@ss.hasPermission('crawler:source:query')")
    public CommonResult<PageResult<CrawlerSourceRespVO>> getSourcePage(@Valid CrawlerSourcePageReqVO pageReqVO) {
        PageResult<CrawlerSourceDO> page = crawlerSourceService.getSourcePage(pageReqVO);
        return success(CrawlerSourceConvert.INSTANCE.convertPage(page));
    }

    @PostMapping("/trigger-game-crawl")
    @Operation(summary = "手动触发赛事爬取")
    @PreAuthorize("@ss.hasPermission('crawler:source:update')")
    public CommonResult<String> triggerGameCrawl() {
        String result = crawlGameJob.execute(null);
        return success(result);
    }
}
