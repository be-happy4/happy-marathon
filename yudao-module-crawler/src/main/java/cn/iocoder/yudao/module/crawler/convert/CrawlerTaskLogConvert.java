package cn.iocoder.yudao.module.crawler.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerTaskLogRespVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerTaskLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrawlerTaskLogConvert {

    CrawlerTaskLogConvert INSTANCE = Mappers.getMapper(CrawlerTaskLogConvert.class);

    CrawlerTaskLogRespVO convert(CrawlerTaskLogDO bean);

    PageResult<CrawlerTaskLogRespVO> convertPage(PageResult<CrawlerTaskLogDO> page);
}
