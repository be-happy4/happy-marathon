package cn.iocoder.yudao.module.crawler.match;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import org.springframework.stereotype.Component;

/**
 * 默认策略：不自动匹配任何记录，由人工处理。
 */
@Component
public class NoOpMatchStrategy implements MatchStrategy {

    @Override
    public String getName() {
        return "NO_OP";
    }

    @Override
    public String getConfidence() {
        return "NONE";
    }

    @Override
    public Long match(CrawlerGameResultDO result) {
        return null;
    }
}
