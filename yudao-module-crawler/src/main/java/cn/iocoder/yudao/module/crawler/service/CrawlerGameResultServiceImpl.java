package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.CrawlerGameResultPageReqVO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameResultMapper;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CrawlerGameResultServiceImpl implements CrawlerGameResultService {

    @Resource
    private CrawlerGameResultMapper crawlerGameResultMapper;

    @Resource
    private CrawlerGameMapper crawlerGameMapper;

    @Override
    public CrawlerGameResultDO getResult(Long id) {
        return crawlerGameResultMapper.selectById(id);
    }

    @Override
    public PageResult<CrawlerGameResultDO> getResultPage(CrawlerGameResultPageReqVO pageReqVO) {
        return crawlerGameResultMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional
    public int confirmImport(List<Long> ids, Long userId) {
        int count = 0;
        for (Long id : ids) {
            CrawlerGameResultDO result = crawlerGameResultMapper.selectById(id);
            if (result == null || !"PENDING".equals(result.getImportStatus())) {
                continue;
            }
            result.setImportStatus("CONFIRMED");
            result.setImportUserId(userId);
            result.setImportTime(LocalDateTime.now());
            // TODO: 实际写入 system_game_registration（等 ETL 模块完善）
            crawlerGameResultMapper.updateById(result);
            count++;
            log.info("[confirmImport] 成绩确认: id={}, userId={}", id, userId);
        }
        return count;
    }

    @Override
    @Transactional
    public int ignoreResults(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            CrawlerGameResultDO result = crawlerGameResultMapper.selectById(id);
            if (result == null) continue;
            result.setImportStatus("IGNORED");
            crawlerGameResultMapper.updateById(result);
            count++;
        }
        return count;
    }

    @Override
    @Transactional
    public void manualMatch(Long resultId, Long userId) {
        CrawlerGameResultDO result = crawlerGameResultMapper.selectById(resultId);
        if (result == null) return;
        result.setMatchStatus("MATCHED");
        result.setMatchUserId(userId);
        result.setMatchConfidence("HIGH");
        result.setMatchStrategy("MANUAL");
        crawlerGameResultMapper.updateById(result);
    }
}
