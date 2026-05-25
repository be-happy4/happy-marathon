package cn.iocoder.yudao.module.crawler.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerSourceRespVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrawlerSourceConvert {

    CrawlerSourceConvert INSTANCE = Mappers.getMapper(CrawlerSourceConvert.class);

    CrawlerSourceRespVO convert(CrawlerSourceDO bean);

    PageResult<CrawlerSourceRespVO> convertPage(PageResult<CrawlerSourceDO> page);
}
