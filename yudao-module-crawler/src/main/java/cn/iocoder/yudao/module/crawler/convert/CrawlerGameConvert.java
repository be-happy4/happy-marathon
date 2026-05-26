package cn.iocoder.yudao.module.crawler.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameRespVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrawlerGameConvert {

    CrawlerGameConvert INSTANCE = Mappers.getMapper(CrawlerGameConvert.class);

    CrawlerGameRespVO convert(CrawlerGameDO bean);

    PageResult<CrawlerGameRespVO> convertPage(PageResult<CrawlerGameDO> page);
}
