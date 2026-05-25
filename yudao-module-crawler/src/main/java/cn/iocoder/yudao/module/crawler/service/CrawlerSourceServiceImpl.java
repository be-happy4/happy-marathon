package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourcePageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourceSaveReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerSourceMapper;
import cn.iocoder.yudao.module.crawler.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class CrawlerSourceServiceImpl implements CrawlerSourceService {

    @Resource
    private CrawlerSourceMapper crawlerSourceMapper;

    @Override
    @Transactional
    public Long createSource(CrawlerSourceSaveReqVO reqVO) {
        CrawlerSourceDO entity = new CrawlerSourceDO();
        fillEntity(entity, reqVO);
        crawlerSourceMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void updateSource(CrawlerSourceSaveReqVO reqVO) {
        CrawlerSourceDO existing = crawlerSourceMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw exception(ErrorCodeConstants.SOURCE_NOT_FOUND);
        }
        fillEntity(existing, reqVO);
        crawlerSourceMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void deleteSource(Long id) {
        CrawlerSourceDO existing = crawlerSourceMapper.selectById(id);
        if (existing == null) {
            throw exception(ErrorCodeConstants.SOURCE_NOT_FOUND);
        }
        crawlerSourceMapper.deleteById(id);
    }

    @Override
    public CrawlerSourceDO getSource(Long id) {
        return crawlerSourceMapper.selectById(id);
    }

    @Override
    public PageResult<CrawlerSourceDO> getSourcePage(CrawlerSourcePageReqVO pageReqVO) {
        return crawlerSourceMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CrawlerSourceDO> getEnabledSources(String crawlType) {
        return crawlerSourceMapper.selectList(
                CrawlerSourceDO::getEnabled, true,
                CrawlerSourceDO::getCrawlType, crawlType);
    }

    @Override
    public void updateLastRun(Long sourceId, String status, String summary) {
        CrawlerSourceDO entity = crawlerSourceMapper.selectById(sourceId);
        if (entity != null) {
            entity.setLastRunTime(LocalDateTime.now());
            entity.setLastRunStatus(status);
            entity.setLastRunSummary(summary);
            crawlerSourceMapper.updateById(entity);
        }
    }

    private void fillEntity(CrawlerSourceDO entity, CrawlerSourceSaveReqVO vo) {
        entity.setName(vo.getName());
        entity.setSourceKey(vo.getSourceKey());
        entity.setCrawlType(vo.getCrawlType());
        entity.setSourceType(vo.getSourceType());
        entity.setHandlerType(vo.getHandlerType());
        entity.setHandlerClass(vo.getHandlerClass());
        entity.setBaseUrl(vo.getBaseUrl());
        entity.setAuthConfig(vo.getAuthConfig());
        entity.setRequestConfig(vo.getRequestConfig());
        entity.setResponseMapping(vo.getResponseMapping());
        entity.setCronExpression(vo.getCronExpression());
        entity.setEnabled(vo.getEnabled() != null ? vo.getEnabled() : false);
        entity.setSortOrder(vo.getSortOrder() != null ? vo.getSortOrder() : 0);
    }
}
