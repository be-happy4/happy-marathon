package cn.iocoder.yudao.module.crawler.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrawlerTaskLogMapper extends BaseMapperX<CrawlerTaskLogDO> {

    default PageResult<CrawlerTaskLogDO> selectPage(CrawlerTaskLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrawlerTaskLogDO>()
                .eqIfPresent(CrawlerTaskLogDO::getSourceId, reqVO.getSourceId())
                .eqIfPresent(CrawlerTaskLogDO::getCrawlType, reqVO.getCrawlType())
                .eqIfPresent(CrawlerTaskLogDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CrawlerTaskLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CrawlerTaskLogDO::getId));
    }
}
