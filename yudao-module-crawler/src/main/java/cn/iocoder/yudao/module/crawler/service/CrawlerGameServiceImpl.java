package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGamePageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CrawlerGameServiceImpl implements CrawlerGameService {

    @Resource
    private CrawlerGameMapper crawlerGameMapper;

    @Override
    public PageResult<CrawlerGameDO> getGamePage(CrawlerGamePageReqVO pageReqVO) {
        return crawlerGameMapper.selectPage(pageReqVO);
    }
}
