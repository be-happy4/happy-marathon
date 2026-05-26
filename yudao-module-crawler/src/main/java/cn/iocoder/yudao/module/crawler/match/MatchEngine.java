package cn.iocoder.yudao.module.crawler.match;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameResultMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 成绩匹配引擎。
 * 按策略优先级依次尝试匹配，第一个匹配成功的策略即为结果。
 */
@Component
@Slf4j
public class MatchEngine {

    @Autowired(required = false)
    private List<MatchStrategy> strategies = new ArrayList<>();

    @Resource
    private CrawlerGameResultMapper crawlerGameResultMapper;

    /**
     * 对指定成绩记录执行自动匹配。
     */
    public void autoMatch(CrawlerGameResultDO result) {
        for (MatchStrategy strategy : strategies) {
            Long userId = strategy.match(result);
            if (userId != null) {
                result.setMatchStatus("MATCHED");
                result.setMatchUserId(userId);
                result.setMatchConfidence(strategy.getConfidence());
                result.setMatchStrategy(strategy.getName());
                crawlerGameResultMapper.updateById(result);
                log.info("[autoMatch] {} → userId={} (strategy={})", result.getName(), userId, strategy.getName());
                return;
            }
        }
        // 所有策略都未匹配
        result.setMatchStatus("UNMATCHED");
        crawlerGameResultMapper.updateById(result);
    }

    /**
     * 批量自动匹配所有 UNMATCHED 记录。
     */
    public int autoMatchAll() {
        List<CrawlerGameResultDO> unmatched = crawlerGameResultMapper.selectByMatchStatus("UNMATCHED");
        int count = 0;
        for (CrawlerGameResultDO result : unmatched) {
            autoMatch(result);
            count++;
        }
        return count;
    }
}
