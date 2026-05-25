package cn.iocoder.yudao.module.crawler.merge;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultMergeStrategy 单元测试
 */
class ResultMergeStrategyTest {

    private final ResultMergeStrategy strategy = new ResultMergeStrategy();

    @Test
    void shouldInsertWhenNoExisting() {
        assertEquals(MergeAction.INSERT, strategy.decide(new CrawlerGameResultDO(), null));
    }

    @Test
    void shouldUpdateWhenPending() {
        CrawlerGameResultDO existing = new CrawlerGameResultDO();
        existing.setImportStatus("PENDING");
        assertEquals(MergeAction.UPDATE, strategy.decide(new CrawlerGameResultDO(), existing));
    }

    @Test
    void shouldIgnoreWhenConfirmed() {
        CrawlerGameResultDO existing = new CrawlerGameResultDO();
        existing.setImportStatus("CONFIRMED");
        assertEquals(MergeAction.IGNORE, strategy.decide(new CrawlerGameResultDO(), existing));
    }

    @Test
    void shouldIgnoreWhenIgnored() {
        CrawlerGameResultDO existing = new CrawlerGameResultDO();
        existing.setImportStatus("IGNORED");
        assertEquals(MergeAction.IGNORE, strategy.decide(new CrawlerGameResultDO(), existing));
    }

    @Test
    void shouldUpdateWhenClaimed() {
        CrawlerGameResultDO existing = new CrawlerGameResultDO();
        existing.setImportStatus("CLAIMED");
        assertEquals(MergeAction.UPDATE, strategy.decide(new CrawlerGameResultDO(), existing));
    }
}
