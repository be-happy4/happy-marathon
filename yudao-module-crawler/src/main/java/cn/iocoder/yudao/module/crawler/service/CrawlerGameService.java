package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGamePageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;

import java.util.List;

public interface CrawlerGameService {

    PageResult<CrawlerGameDO> getGamePage(CrawlerGamePageReqVO pageReqVO);
}
