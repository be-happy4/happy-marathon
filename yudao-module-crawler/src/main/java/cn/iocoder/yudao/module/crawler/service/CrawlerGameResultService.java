package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;

import java.util.List;

public interface CrawlerGameResultService {

    CrawlerGameResultDO getResult(Long id);

    PageResult<CrawlerGameResultDO> getResultPage(CrawlerGameResultPageReqVO pageReqVO);

    /** 批量确认导入 */
    int confirmImport(List<Long> ids, Long userId);

    /** 批量忽略 */
    int ignoreResults(List<Long> ids);

    /** 手动匹配 */
    void manualMatch(Long resultId, Long userId);
}
