package cn.iocoder.yudao.module.crawler.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourcePageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrawlerSourceMapper extends BaseMapperX<CrawlerSourceDO> {

    default PageResult<CrawlerSourceDO> selectPage(CrawlerSourcePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrawlerSourceDO>()
                .likeIfPresent(CrawlerSourceDO::getName, reqVO.getName())
                .eqIfPresent(CrawlerSourceDO::getSourceKey, reqVO.getSourceKey())
                .eqIfPresent(CrawlerSourceDO::getCrawlType, reqVO.getCrawlType())
                .eqIfPresent(CrawlerSourceDO::getHandlerType, reqVO.getHandlerType())
                .eqIfPresent(CrawlerSourceDO::getEnabled, reqVO.getEnabled())
                .orderByAsc(CrawlerSourceDO::getSortOrder));
    }

    default CrawlerSourceDO selectBySourceKey(String sourceKey) {
        return selectOne(CrawlerSourceDO::getSourceKey, sourceKey);
    }
}
