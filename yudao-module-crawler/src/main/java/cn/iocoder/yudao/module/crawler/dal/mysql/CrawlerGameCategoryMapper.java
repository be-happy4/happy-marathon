package cn.iocoder.yudao.module.crawler.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 临时组别 Mapper
 */
@Mapper
public interface CrawlerGameCategoryMapper extends BaseMapperX<CrawlerGameCategoryDO> {

    default List<CrawlerGameCategoryDO> selectByGameId(Long crawlerGameId) {
        return selectList(new LambdaQueryWrapperX<CrawlerGameCategoryDO>()
                .eq(CrawlerGameCategoryDO::getCrawlerGameId, crawlerGameId));
    }

    default void deleteByGameId(Long crawlerGameId) {
        delete(new LambdaQueryWrapperX<CrawlerGameCategoryDO>()
                .eq(CrawlerGameCategoryDO::getCrawlerGameId, crawlerGameId));
    }
}
