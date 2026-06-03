package cn.iocoder.yudao.module.crawler.service;

import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchReqVO.PersonItem;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchRespVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchRespVO.BatchSummary;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreBatchRespVO.PersonResult;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryReqVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryRespVO;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryRespVO.MatchSummary;
import cn.iocoder.yudao.module.crawler.controller.admin.vo.PersonalScoreQueryRespVO.ResultItem;
import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameResultMapper;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerSourceMapper;
import cn.iocoder.yudao.module.crawler.match.MatchEngine;
import cn.iocoder.yudao.module.crawler.pipeline.PipelineStats;
import cn.iocoder.yudao.module.crawler.pipeline.ResultPipeline;
import cn.iocoder.yudao.module.crawler.source.runchina.CaptchaRequiredException;
import cn.iocoder.yudao.module.crawler.source.runchina.RunchinaScoreQuerier;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PersonalScoreQueryServiceImpl implements PersonalScoreQueryService {

    private static final String SOURCE_KEY = "runchina_personal";

    @Resource
    private RunchinaScoreQuerier runchinaScoreQuerier;
    @Resource
    private CrawlerSourceMapper crawlerSourceMapper;
    @Resource
    private ResultPipeline resultPipeline;
    @Resource
    private CrawlerGameResultMapper crawlerGameResultMapper;
    @Resource
    private MatchEngine matchEngine;

    @Override
    @Transactional
    public PersonalScoreQueryRespVO query(PersonalScoreQueryReqVO reqVO) {
        String name = reqVO.getName();
        String idCard = reqVO.getIdCard();
        String phone = reqVO.getPhone();

        log.info("[PersonalScoreQuery] query: name={}, idCard={}, phone={}",
                name, mask(idCard), mask(phone));

        // 1. 获取或创建数据源
        CrawlerSourceDO source = getOrCreateSource();
        Long sourceId = source.getId();

        // 2. 浏览器查询
        List<CrawlResult> crawlResults;
        try {
            crawlResults = runchinaScoreQuerier.query(name, idCard);
        } catch (CaptchaRequiredException e) {
            return PersonalScoreQueryRespVO.builder()
                    .status("CAPTCHA_REQUIRED")
                    .message(e.getMessage())
                    .results(List.of())
                    .matchSummary(MatchSummary.builder().build())
                    .build();
        } catch (Exception e) {
            log.error("[PersonalScoreQuery] Query failed", e);
            return PersonalScoreQueryRespVO.builder()
                    .status("ERROR")
                    .message("查询失败: " + e.getMessage())
                    .results(List.of())
                    .matchSummary(MatchSummary.builder().build())
                    .build();
        }

        if (crawlResults.isEmpty()) {
            return PersonalScoreQueryRespVO.builder()
                    .status("NO_RESULT")
                    .message("未查询到成绩记录")
                    .results(List.of())
                    .matchSummary(MatchSummary.builder().build())
                    .build();
        }

        // 3. 设置 sourceId 并通过 ResultPipeline 持久化
        for (CrawlResult cr : crawlResults) {
            cr.setSourceId(sourceId);
        }
        PipelineStats stats = resultPipeline.process(sourceId, SOURCE_KEY, crawlResults);

        // 4. 自动匹配
        int matched = matchEngine.autoMatchAll();

        log.info("[PersonalScoreQuery] persisted: new={} updated={} ignored={} matched={}",
                stats.getNewCount(), stats.getUpdatedCount(), stats.getIgnoredCount(), matched);

        // 5. 查询刚写入的记录用于响应
        List<CrawlerGameResultDO> savedResults = crawlerGameResultMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrawlerGameResultDO>()
                        .eq(CrawlerGameResultDO::getSourceId, sourceId)
                        .orderByDesc(CrawlerGameResultDO::getId)
                        .last("limit " + crawlResults.size()));

        // 6. 构建响应
        List<ResultItem> items = new ArrayList<>();
        int autoMatched = 0;
        for (CrawlerGameResultDO r : savedResults) {
            boolean isMatched = "MATCHED".equals(r.getMatchStatus());
            if (isMatched) autoMatched++;

            items.add(ResultItem.builder()
                    .gameName(r.getGameName())
                    .gameDate(r.getGameDate() != null ? r.getGameDate().toString() : null)
                    .bibNumber(r.getBibNumber())
                    .gunTime(formatMs(r.getGunTimeMs()))
                    .netTime(formatMs(r.getNetTimeMs()))
                    .rank(r.getRank())
                    .ageGroup(r.getAgeGroup())
                    .matchStatus(r.getMatchStatus())
                    .matchedUser(isMatched ? "userId=" + r.getMatchUserId() : null)
                    .build());
        }

        return PersonalScoreQueryRespVO.builder()
                .status("OK")
                .message("查询到 " + items.size() + " 条成绩记录")
                .results(items)
                .matchSummary(MatchSummary.builder()
                        .totalFound(items.size())
                        .autoMatched(autoMatched)
                        .unmatched(items.size() - autoMatched)
                        .build())
                .build();
    }

    private CrawlerSourceDO getOrCreateSource() {
        CrawlerSourceDO source = crawlerSourceMapper.selectBySourceKey(SOURCE_KEY);
        if (source == null) {
            source = new CrawlerSourceDO();
            source.setName("中国马拉松官网-个人成绩查询");
            source.setSourceKey(SOURCE_KEY);
            source.setCrawlType("RESULT");
            source.setSourceType("OFFICIAL");
            source.setHandlerType("CUSTOM");
            source.setEnabled(true);
            crawlerSourceMapper.insert(source);
            log.info("[PersonalScoreQuery] Created crawler_source: id={}", source.getId());
        }
        return source;
    }

    static String formatMs(Long ms) {
        if (ms == null) return null;
        long seconds = ms / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    private String mask(String s) {
        if (s == null || s.length() <= 4) return "***";
        return s.substring(0, 4) + "***";
    }

    // ============================================================
    // Batch query
    // ============================================================

    @Override
    @Transactional
    public PersonalScoreBatchRespVO batchQuery(PersonalScoreBatchReqVO reqVO) {
        List<PersonItem> persons = reqVO.getPersons();
        log.info("[PersonalScoreBatch] Starting batch query for {} persons", persons.size());

        // 获取或创建数据源
        CrawlerSourceDO source = getOrCreateSource();
        Long sourceId = source.getId();

        // 打开批量 session
        RunchinaScoreQuerier.BatchSession session;
        try {
            session = runchinaScoreQuerier.startBatchSession();
        } catch (CaptchaRequiredException e) {
            return PersonalScoreBatchRespVO.builder()
                    .status("CAPTCHA_REQUIRED")
                    .message(e.getMessage())
                    .personResults(List.of())
                    .summary(BatchSummary.builder().totalPersons(persons.size()).build())
                    .build();
        } catch (Exception e) {
            log.error("[PersonalScoreBatch] Session creation failed", e);
            return PersonalScoreBatchRespVO.builder()
                    .status("ERROR")
                    .message("浏览器初始化失败: " + e.getMessage())
                    .personResults(List.of())
                    .summary(BatchSummary.builder().totalPersons(persons.size()).build())
                    .build();
        }

        List<PersonResult> personResults = new ArrayList<>();
        int totalResults = 0;
        int matched = 0;
        int successCount = 0;

        try {
            for (int i = 0; i < persons.size(); i++) {
                PersonItem p = persons.get(i);
                try {
                    List<CrawlResult> crawlResults = runchinaScoreQuerier.queryInSession(
                            session, p.getName(), p.getIdCard());

                    if (crawlResults.isEmpty()) {
                        personResults.add(PersonResult.builder()
                                .name(p.getName())
                                .idCardMasked(mask(p.getIdCard()))
                                .queryStatus("NO_RESULT")
                                .resultCount(0)
                                .results(List.of())
                                .build());
                        continue;
                    }

                    // 通过 ResultPipeline 持久化
                    for (CrawlResult cr : crawlResults) {
                        cr.setSourceId(sourceId);
                    }
                    resultPipeline.process(sourceId, SOURCE_KEY, crawlResults);

                    // 查询刚写入的记录
                    List<CrawlerGameResultDO> savedResults = crawlerGameResultMapper.selectList(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CrawlerGameResultDO>()
                                    .eq(CrawlerGameResultDO::getSourceId, sourceId)
                                    .orderByDesc(CrawlerGameResultDO::getId)
                                    .last("limit " + crawlResults.size()));

                    List<ResultItem> items = new ArrayList<>();
                    int personMatched = 0;
                    for (CrawlerGameResultDO r : savedResults) {
                        boolean isMatched = "MATCHED".equals(r.getMatchStatus());
                        if (isMatched) personMatched++;
                        items.add(ResultItem.builder()
                                .gameName(r.getGameName())
                                .gameDate(r.getGameDate() != null ? r.getGameDate().toString() : null)
                                .bibNumber(r.getBibNumber())
                                .gunTime(formatMs(r.getGunTimeMs()))
                                .netTime(formatMs(r.getNetTimeMs()))
                                .rank(r.getRank())
                                .ageGroup(r.getAgeGroup())
                                .matchStatus(r.getMatchStatus())
                                .matchedUser(isMatched ? "userId=" + r.getMatchUserId() : null)
                                .build());
                    }

                    totalResults += items.size();
                    matched += personMatched;
                    successCount++;

                    personResults.add(PersonResult.builder()
                            .name(p.getName())
                            .idCardMasked(mask(p.getIdCard()))
                            .queryStatus("OK")
                            .resultCount(items.size())
                            .results(items)
                            .build());

                    log.info("[PersonalScoreBatch] [{}/{}] {} → {} results (matched={})",
                            i + 1, persons.size(), p.getName(), items.size(), personMatched);

                } catch (CaptchaRequiredException e) {
                    log.warn("[PersonalScoreBatch] Captcha re-appeared, stopping batch");
                    personResults.add(PersonResult.builder()
                            .name(p.getName())
                            .idCardMasked(mask(p.getIdCard()))
                            .queryStatus("ERROR")
                            .errorMsg("验证码重新出现，已停止")
                            .resultCount(0)
                            .results(List.of())
                            .build());
                    break;
                } catch (Exception e) {
                    log.error("[PersonalScoreBatch] Query failed for {}", p.getName(), e);
                    personResults.add(PersonResult.builder()
                            .name(p.getName())
                            .idCardMasked(mask(p.getIdCard()))
                            .queryStatus("ERROR")
                            .errorMsg(e.getMessage())
                            .resultCount(0)
                            .results(List.of())
                            .build());
                }

                // 间隔，避免触发反爬
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
        } finally {
            session.close();
        }

        // 自动匹配
        int autoMatched = matchEngine.autoMatchAll();

        return PersonalScoreBatchRespVO.builder()
                .status("OK")
                .message(String.format("批量查询完成: %d/%d 人成功, 共 %d 条成绩",
                        successCount, persons.size(), totalResults))
                .personResults(personResults)
                .summary(BatchSummary.builder()
                        .totalPersons(persons.size())
                        .successPersons(successCount)
                        .totalResults(totalResults)
                        .matched(matched + autoMatched)
                        .unmatched(totalResults - matched - autoMatched)
                        .build())
                .build();
    }
}
