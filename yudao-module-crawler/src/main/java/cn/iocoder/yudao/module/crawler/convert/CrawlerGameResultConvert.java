package cn.iocoder.yudao.module.crawler.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultRespVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrawlerGameResultConvert {

    CrawlerGameResultConvert INSTANCE = Mappers.getMapper(CrawlerGameResultConvert.class);

    CrawlerGameResultRespVO convert(CrawlerGameResultDO bean);

    PageResult<CrawlerGameResultRespVO> convertPage(PageResult<CrawlerGameResultDO> page);
}
