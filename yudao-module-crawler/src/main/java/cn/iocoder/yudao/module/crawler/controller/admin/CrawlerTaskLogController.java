package cn.iocoder.yudao.module.crawler.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogPageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogRespVO;
import cn.iocoder.yudao.module.crawler.convert.CrawlerTaskLogConvert;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import cn.iocoder.yudao.module.crawler.service.CrawlerTaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 爬虫任务日志")
@RestController
@RequestMapping("/admin-api/crawler/task-log")
@Validated
public class CrawlerTaskLogController {

    @Resource
    private CrawlerTaskLogService crawlerTaskLogService;

    @GetMapping("/page")
    @Operation(summary = "获取任务日志分页")
    @PreAuthorize("@ss.hasPermission('crawler:task-log:query')")
    public CommonResult<PageResult<CrawlerTaskLogRespVO>> getLogPage(@Valid CrawlerTaskLogPageReqVO pageReqVO) {
        PageResult<CrawlerTaskLogDO> page = crawlerTaskLogService.getLogPage(pageReqVO);
        return success(CrawlerTaskLogConvert.INSTANCE.convertPage(page));
    }

    @GetMapping("/get")
    @Operation(summary = "获取任务日志详情")
    @PreAuthorize("@ss.hasPermission('crawler:task-log:query')")
    public CommonResult<CrawlerTaskLogRespVO> getLog(@RequestParam("id") Long id) {
        CrawlerTaskLogDO log = crawlerTaskLogService.getLog(id);
        return success(CrawlerTaskLogConvert.INSTANCE.convert(log));
    }
}
