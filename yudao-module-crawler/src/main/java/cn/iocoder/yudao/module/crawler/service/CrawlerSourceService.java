package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourcePageReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourceSaveReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;

import java.util.List;

public interface CrawlerSourceService {

    Long createSource(CrawlerSourceSaveReqVO reqVO);

    void updateSource(CrawlerSourceSaveReqVO reqVO);

    void deleteSource(Long id);

    CrawlerSourceDO getSource(Long id);

    PageResult<CrawlerSourceDO> getSourcePage(CrawlerSourcePageReqVO pageReqVO);

    List<CrawlerSourceDO> getEnabledSources(String crawlType);

    /** 更新最近执行状态 */
    void updateLastRun(Long sourceId, String status, String summary);

    /** 根据 sourceKey 查找数据源 */
    CrawlerSourceDO getSourceByKey(String sourceKey);
}
