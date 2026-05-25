package cn.iocoder.yudao.module.crawler.pipeline;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameCategoryDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameCategoryMapper;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import cn.iocoder.yudao.module.crawler.merge.GameMergeStrategy;
import cn.iocoder.yudao.module.crawler.merge.MergeAction;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 赛事数据管道：CrawlResult → crawler_game + crawler_game_category
 */
@Component
@Slf4j
public class GamePipeline implements DataPipeline {

    @Resource
    private CrawlerGameMapper crawlerGameMapper;

    @Resource
    private CrawlerGameCategoryMapper crawlerGameCategoryMapper;

    @Resource
    private GameMergeStrategy gameMergeStrategy;

    @Override
    public String getCrawlType() {
        return "GAME";
    }

    @Override
    public PipelineStats process(Long sourceId, String sourceKey, List<CrawlResult> results) {
        PipelineStats stats = new PipelineStats();
        for (CrawlResult result : results) {
            try {
                processSingle(sourceId, result, stats);
            } catch (Exception e) {
                log.error("[GamePipeline][{}] 处理单条失败: {}", sourceKey, result.getSourceGameId(), e);
                stats.addIgnored();
            }
        }
        return stats;
    }

    private void processSingle(Long sourceId, CrawlResult result, PipelineStats stats) {
        String name = result.getString("name");
        LocalDate gameDate = parseDate(result.getString("game_date"));

        // 查询已存在的记录
        CrawlerGameDO existing = crawlerGameMapper.selectByNameAndDate(name, gameDate);
        CrawlerGameDO entity = buildGameDO(sourceId, result);

        MergeAction action = gameMergeStrategy.decide(entity, existing);
        switch (action) {
            case INSERT -> {
                entity.setImportStatus("PENDING");
                crawlerGameMapper.insert(entity);
                // 处理组别
                processCategories(entity.getId(), result);
                stats.addNew();
            }
            case UPDATE -> {
                entity.setId(existing.getId());
                crawlerGameMapper.updateById(entity);
                // 更新组别：先删后插
                crawlerGameCategoryMapper.deleteByGameId(existing.getId());
                processCategories(existing.getId(), result);
                stats.addUpdated();
            }
            case IGNORE -> stats.addIgnored();
        }
    }

    private CrawlerGameDO buildGameDO(Long sourceId, CrawlResult result) {
        CrawlerGameDO entity = new CrawlerGameDO();
        entity.setSourceId(sourceId);
        entity.setSourceGameId(result.getSourceGameId());
        entity.setName(result.getString("name"));
        entity.setNameEn(result.getString("name_en"));
        entity.setGameDate(parseDate(result.getString("game_date")));
        entity.setRegionName(result.getString("region_name"));
        entity.setWorldAthleticsLevel(result.getString("world_athletics_level"));
        entity.setChinaRoadRunLevel(result.getString("china_road_run_level"));
        entity.setStatus(result.getString("status"));
        entity.setTags(result.getString("tags"));
        entity.setRemark(result.getString("remark"));
        entity.setRawData(result.getRawData());
        return entity;
    }

    @SuppressWarnings("unchecked")
    private void processCategories(Long crawlerGameId, CrawlResult result) {
        Object cats = result.getFields().get("categories");
        if (!(cats instanceof List<?> catList)) return;

        for (Object item : catList) {
            if (item instanceof java.util.Map<?, ?> catMap) {
                CrawlerGameCategoryDO cat = new CrawlerGameCategoryDO();
                cat.setCrawlerGameId(crawlerGameId);
                cat.setGameType((String) catMap.get("game_type"));
                Object dist = catMap.get("distance_km");
                if (dist instanceof Number n) {
                    cat.setDistanceKm(BigDecimal.valueOf(n.doubleValue()));
                } else if (dist instanceof String s) {
                    cat.setDistanceKm(new BigDecimal(s));
                }
                crawlerGameCategoryMapper.insert(cat);
            }
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            log.warn("[GamePipeline] 日期解析失败: {}", s);
            return null;
        }
    }
}
