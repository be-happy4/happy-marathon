package cn.iocoder.yudao.server.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.*;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultPageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultRespVO;
import cn.iocoder.yudao.module.crawler.convert.CrawlerGameConvert;
import cn.iocoder.yudao.module.crawler.convert.CrawlerGameResultConvert;
import cn.iocoder.yudao.module.crawler.convert.CrawlerSourceConvert;
import cn.iocoder.yudao.module.crawler.convert.CrawlerTaskLogConvert;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import cn.iocoder.yudao.module.crawler.job.CrawlGameJob;
import cn.iocoder.yudao.module.crawler.service.CrawlerGameResultService;
import cn.iocoder.yudao.module.crawler.service.CrawlerGameService;
import cn.iocoder.yudao.module.crawler.service.CrawlerSourceService;
import cn.iocoder.yudao.module.crawler.service.CrawlerTaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 爬虫管理 API 代理。
 * yudao-module-crawler 的 Controller 因框架扫描机制无法自动注册,
 * 这里在 yudao-server 模块中直接暴露端点, 代理到 crawler 服务层。
 */
@Tag(name = "管理后台 - 爬虫管理")
@RestController
@RequestMapping("/admin-api/crawler")
@Validated
public class CrawlerApiController {

    // ===== Source 数据源管理 =====

    @Resource
    private CrawlerSourceService crawlerSourceService;

    @Resource
    private CrawlGameJob crawlGameJob;

    @GetMapping("/source/page")
    @Operation(summary = "获取数据源分页")
    public CommonResult<PageResult<CrawlerSourceRespVO>> getSourcePage(@Valid CrawlerSourcePageReqVO reqVO) {
        PageResult<CrawlerSourceDO> page = crawlerSourceService.getSourcePage(reqVO);
        return success(CrawlerSourceConvert.INSTANCE.convertPage(page));
    }

    @PostMapping("/source/trigger-game-crawl")
    @Operation(summary = "手动触发赛事爬取")
    public CommonResult<String> triggerGameCrawl(@RequestParam(value = "sourceKey", required = false) String sourceKey) {
        return success(crawlGameJob.execute(sourceKey));
    }

    @GetMapping("/source/get")
    @Operation(summary = "获取数据源详情")
    public CommonResult<CrawlerSourceRespVO> getSource(@RequestParam("id") Long id) {
        CrawlerSourceDO source = crawlerSourceService.getSource(id);
        return success(CrawlerSourceConvert.INSTANCE.convert(source));
    }

    @PutMapping("/source/update")
    @Operation(summary = "更新数据源")
    public CommonResult<Boolean> updateSource(@Valid @RequestBody CrawlerSourceSaveReqVO reqVO) {
        crawlerSourceService.updateSource(reqVO);
        return success(true);
    }

    // ===== TaskLog 任务日志 =====

    @Resource
    private CrawlerTaskLogService crawlerTaskLogService;

    @GetMapping("/task-log/page")
    @Operation(summary = "获取任务日志分页")
    public CommonResult<PageResult<CrawlerTaskLogRespVO>> getTaskLogPage(@Valid CrawlerTaskLogPageReqVO reqVO) {
        PageResult<CrawlerTaskLogDO> page = crawlerTaskLogService.getLogPage(reqVO);
        return success(CrawlerTaskLogConvert.INSTANCE.convertPage(page));
    }

    // ===== Game 爬取赛事 =====

    @Resource
    private CrawlerGameService crawlerGameService;

    @GetMapping("/game/page")
    @Operation(summary = "获取临时赛事分页")
    public CommonResult<PageResult<CrawlerGameRespVO>> getGamePage(@Valid CrawlerGamePageReqVO reqVO) {
        PageResult<CrawlerGameDO> page = crawlerGameService.getGamePage(reqVO);
        return success(CrawlerGameConvert.INSTANCE.convertPage(page));
    }

    // ===== Result 爬取成绩 =====

    @Resource
    private CrawlerGameResultService crawlerGameResultService;

    @GetMapping("/result/page")
    @Operation(summary = "获取临时成绩分页")
    public CommonResult<PageResult<CrawlerGameResultRespVO>> getResultPage(@Valid CrawlerGameResultPageReqVO reqVO) {
        PageResult<CrawlerGameResultDO> page = crawlerGameResultService.getResultPage(reqVO);
        return success(CrawlerGameResultConvert.INSTANCE.convertPage(page));
    }

    // ===== Personal Score 个人成绩查询 =====

    @Resource
    private cn.iocoder.yudao.module.crawler.service.PersonalScoreQueryService personalScoreQueryService;

    @PostMapping("/personal-score/query")
    @Operation(summary = "根据姓名+证件号查询个人成绩")
    public CommonResult<cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryRespVO> queryPersonalScore(
            @Valid @RequestBody cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryReqVO reqVO) {
        return success(personalScoreQueryService.query(reqVO));
    }

    @PostMapping("/personal-score/batch-query")
    @Operation(summary = "批量查询个人成绩（手动过一次验证码后自动查询多人）")
    public CommonResult<cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchRespVO> batchQueryPersonalScore(
            @Valid @RequestBody cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchReqVO reqVO) {
        return success(personalScoreQueryService.batchQuery(reqVO));
    }
}
