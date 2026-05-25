package cn.iocoder.yudao.module.crawler.merge;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameMergeStrategy 单元测试
 */
class GameMergeStrategyTest {

    private final GameMergeStrategy strategy = new GameMergeStrategy();

    @Test
    void shouldInsertWhenNoExisting() {
        assertEquals(MergeAction.INSERT, strategy.decide(new CrawlerGameDO(), null));
    }

    @Test
    void shouldUpdateWhenPending() {
        CrawlerGameDO existing = new CrawlerGameDO();
        existing.setImportStatus("PENDING");
        assertEquals(MergeAction.UPDATE, strategy.decide(new CrawlerGameDO(), existing));
    }

    @Test
    void shouldIgnoreWhenImported() {
        CrawlerGameDO existing = new CrawlerGameDO();
        existing.setImportStatus("IMPORTED");
        assertEquals(MergeAction.IGNORE, strategy.decide(new CrawlerGameDO(), existing));
    }

    @Test
    void shouldUpdateWhenIgnored() {
        CrawlerGameDO existing = new CrawlerGameDO();
        existing.setImportStatus("IGNORED");
        assertEquals(MergeAction.UPDATE, strategy.decide(new CrawlerGameDO(), existing));
    }
}
