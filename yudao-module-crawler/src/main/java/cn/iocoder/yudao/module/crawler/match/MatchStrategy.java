package cn.iocoder.yudao.module.crawler.match;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;

/**
 * 成绩匹配策略接口。
 */
public interface MatchStrategy {

    /** 策略名称 */
    String getName();

    /** 匹配置信度 */
    String getConfidence();

    /**
     * 尝试匹配用户。
     * @return 匹配到的用户 ID，null 表示未匹配
     */
    Long match(CrawlerGameResultDO result);
}
