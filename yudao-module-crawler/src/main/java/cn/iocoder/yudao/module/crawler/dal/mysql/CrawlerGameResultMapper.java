package cn.iocoder.yudao.module.crawler.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 临时成绩 Mapper
 */
@Mapper
public interface CrawlerGameResultMapper extends BaseMapperX<CrawlerGameResultDO> {

    default PageResult<CrawlerGameResultDO> selectPage(CrawlerGameResultPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrawlerGameResultDO>()
                .eqIfPresent(CrawlerGameResultDO::getSourceId, reqVO.getSourceId())
                .eqIfPresent(CrawlerGameResultDO::getMatchStatus, reqVO.getMatchStatus())
                .eqIfPresent(CrawlerGameResultDO::getImportStatus, reqVO.getImportStatus())
                .likeIfPresent(CrawlerGameResultDO::getName, reqVO.getName())
                .likeIfPresent(CrawlerGameResultDO::getGameName, reqVO.getGameName())
                .eqIfPresent(CrawlerGameResultDO::getIdCard, reqVO.getIdCard())
                .orderByDesc(CrawlerGameResultDO::getId));
    }

    default CrawlerGameResultDO selectByUniqueKey(Long crawlerGameId, Long crawlerCategoryId, String idCard) {
        return selectOne(new LambdaQueryWrapperX<CrawlerGameResultDO>()
                .eq(CrawlerGameResultDO::getCrawlerGameId, crawlerGameId)
                .eq(CrawlerGameResultDO::getCrawlerCategoryId, crawlerCategoryId)
                .eq(CrawlerGameResultDO::getIdCard, idCard));
    }

    default List<CrawlerGameResultDO> selectByMatchStatus(String matchStatus) {
        return selectList(new LambdaQueryWrapperX<CrawlerGameResultDO>()
                .eq(CrawlerGameResultDO::getMatchStatus, matchStatus));
    }
}
