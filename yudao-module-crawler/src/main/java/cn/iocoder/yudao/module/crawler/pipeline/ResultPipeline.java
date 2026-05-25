package cn.iocoder.yudao.module.crawler.pipeline;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameResultMapper;
import cn.iocoder.yudao.module.crawler.merge.MergeAction;
import cn.iocoder.yudao.module.crawler.merge.ResultMergeStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 成绩数据管道：CrawlResult → crawler_game_result
 */
@Component
@Slf4j
public class ResultPipeline implements DataPipeline {

    @Resource
    private CrawlerGameResultMapper crawlerGameResultMapper;

    @Resource
    private ResultMergeStrategy resultMergeStrategy;

    @Override
    public String getCrawlType() {
        return "RESULT";
    }

    @Override
    public PipelineStats process(Long sourceId, String sourceKey, List<CrawlResult> results) {
        PipelineStats stats = new PipelineStats();
        for (CrawlResult result : results) {
            try {
                processSingle(sourceId, result, stats);
            } catch (Exception e) {
                log.error("[ResultPipeline][{}] 处理单条失败: {}", sourceKey, result.getSourceGameId(), e);
                stats.addIgnored();
            }
        }
        return stats;
    }

    private void processSingle(Long sourceId, CrawlResult result, PipelineStats stats) {
        String idCard = result.getString("id_card");
        Long crawlerGameId = result.getLong("crawler_game_id");
        Long crawlerCategoryId = result.getLong("crawler_category_id");

        // 去重查询
        CrawlerGameResultDO existing = crawlerGameResultMapper
                .selectByUniqueKey(crawlerGameId, crawlerCategoryId, idCard);

        CrawlerGameResultDO entity = buildResultDO(sourceId, result);
        MergeAction action = resultMergeStrategy.decide(entity, existing);

        switch (action) {
            case INSERT -> {
                entity.setMatchStatus("UNMATCHED");
                entity.setImportStatus("PENDING");
                crawlerGameResultMapper.insert(entity);
                stats.addNew();
            }
            case UPDATE -> {
                entity.setId(existing.getId());
                entity.setMatchStatus(existing.getMatchStatus());
                entity.setImportStatus(existing.getImportStatus());
                crawlerGameResultMapper.updateById(entity);
                stats.addUpdated();
            }
            case IGNORE -> stats.addIgnored();
        }
    }

    private CrawlerGameResultDO buildResultDO(Long sourceId, CrawlResult result) {
        CrawlerGameResultDO entity = new CrawlerGameResultDO();
        entity.setSourceId(sourceId);
        entity.setCrawlerGameId(result.getLong("crawler_game_id"));
        entity.setCrawlerCategoryId(result.getLong("crawler_category_id"));
        entity.setGameName(result.getString("game_name"));
        entity.setGameDate(parseDate(result.getString("game_date")));
        entity.setBibNumber(result.getString("bib_number"));
        entity.setName(result.getString("name"));
        entity.setNameEn(result.getString("name_en"));
        entity.setNationality(result.getString("nationality"));
        entity.setGender(result.getString("gender"));
        entity.setAgeGroup(result.getString("age_group"));
        entity.setIdCard(result.getString("id_card"));
        entity.setPassport(result.getString("passport"));
        entity.setGunTimeMs(result.getLong("gun_time_ms"));
        entity.setNetTimeMs(result.getLong("net_time_ms"));
        entity.setRank(result.getInteger("rank"));
        entity.setGenderRank(result.getInteger("gender_rank"));
        entity.setCategoryRank(result.getInteger("category_rank"));
        entity.setRawData(result.getRawData());
        return entity;
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            log.warn("[ResultPipeline] 日期解析失败: {}", s);
            return null;
        }
    }
}
