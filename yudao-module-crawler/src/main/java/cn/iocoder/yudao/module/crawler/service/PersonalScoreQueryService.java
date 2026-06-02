package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchRespVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryRespVO;

public interface PersonalScoreQueryService {

    PersonalScoreQueryRespVO query(PersonalScoreQueryReqVO reqVO);

    PersonalScoreBatchRespVO batchQuery(PersonalScoreBatchReqVO reqVO);
}
