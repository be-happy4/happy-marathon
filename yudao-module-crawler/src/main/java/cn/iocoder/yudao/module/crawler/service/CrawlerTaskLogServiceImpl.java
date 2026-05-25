package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerTaskLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CrawlerTaskLogServiceImpl implements CrawlerTaskLogService {

    @Resource
    private CrawlerTaskLogMapper crawlerTaskLogMapper;

    @Override
    @Transactional
    public Long createLog(CrawlerTaskLogDO log) {
        log.setStartTime(LocalDateTime.now());
        log.setStatus("RUNNING");
        crawlerTaskLogMapper.insert(log);
        return log.getId();
    }

    @Override
    @Transactional
    public void updateLogResult(Long logId, String status, int totalFetched, int totalNew, int totalUpdated, int totalIgnored, String errorMsg) {
        CrawlerTaskLogDO log = crawlerTaskLogMapper.selectById(logId);
        if (log != null) {
            log.setEndTime(LocalDateTime.now());
            log.setStatus(status);
            log.setTotalFetched(totalFetched);
            log.setTotalNew(totalNew);
            log.setTotalUpdated(totalUpdated);
            log.setTotalIgnored(totalIgnored);
            log.setErrorMsg(errorMsg);
            crawlerTaskLogMapper.updateById(log);
        }
    }

    @Override
    public CrawlerTaskLogDO getLog(Long id) {
        return crawlerTaskLogMapper.selectById(id);
    }

    @Override
    public PageResult<CrawlerTaskLogDO> getLogPage(CrawlerTaskLogPageReqVO pageReqVO) {
        return crawlerTaskLogMapper.selectPage(pageReqVO);
    }
}
