package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;

public interface CrawlerTaskLogService {

    Long createLog(CrawlerTaskLogDO log);

    void updateLogResult(Long logId, String status, int totalFetched, int totalNew, int totalUpdated, int totalIgnored, String errorMsg);

    CrawlerTaskLogDO getLog(Long id);

    PageResult<CrawlerTaskLogDO> getLogPage(CrawlerTaskLogPageReqVO pageReqVO);
}
