package cn.iocoder.yudao.module.crawler.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGamePageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 临时赛事 Mapper
 */
@Mapper
public interface CrawlerGameMapper extends BaseMapperX<CrawlerGameDO> {

    default PageResult<CrawlerGameDO> selectPage(CrawlerGamePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CrawlerGameDO>()
                .likeIfPresent(CrawlerGameDO::getName, reqVO.getName())
                .eqIfPresent(CrawlerGameDO::getSourceId, reqVO.getSourceId())
                .inIfPresent(CrawlerGameDO::getWorldAthleticsLevel, reqVO.getWorldAthleticsLevels())
                .inIfPresent(CrawlerGameDO::getChinaRoadRunLevel, reqVO.getChinaRoadRunLevels())
                .inIfPresent(CrawlerGameDO::getStatus, reqVO.getStatuses())
                .inIfPresent(CrawlerGameDO::getImportStatus, reqVO.getImportStatuses())
                .betweenIfPresent(CrawlerGameDO::getGameDate, reqVO.getGameDate()));
    }

    default CrawlerGameDO selectBySourceAndGameId(Long sourceId, String sourceGameId) {
        return selectOne(new LambdaQueryWrapperX<CrawlerGameDO>()
                .eq(CrawlerGameDO::getSourceId, sourceId)
                .eq(CrawlerGameDO::getSourceGameId, sourceGameId));
    }

    default CrawlerGameDO selectByNameAndDate(String name, java.time.LocalDate gameDate) {
        return selectOne(new LambdaQueryWrapperX<CrawlerGameDO>()
                .eq(CrawlerGameDO::getName, name)
                .eq(CrawlerGameDO::getGameDate, gameDate));
    }
}
