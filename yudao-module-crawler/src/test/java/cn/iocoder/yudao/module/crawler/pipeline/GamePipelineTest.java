package cn.iocoder.yudao.module.crawler.pipeline;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameCategoryMapper;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerGameMapper;
import cn.iocoder.yudao.module.crawler.merge.GameMergeStrategy;
import cn.iocoder.yudao.module.crawler.merge.MergeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameCategoryDO;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * GamePipeline 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class GamePipelineTest {

    @Mock
    private CrawlerGameMapper crawlerGameMapper;

    @Mock
    private CrawlerGameCategoryMapper crawlerGameCategoryMapper;

    @Mock
    private GameMergeStrategy gameMergeStrategy;

    @InjectMocks
    private GamePipeline gamePipeline;

    private CrawlResult sampleResult;

    @BeforeEach
    void setUp() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "北京马拉松");
        fields.put("game_date", "2026-10-18");
        fields.put("region_name", "北京");
        fields.put("world_athletics_level", "platinum");
        fields.put("status", "announced");
        fields.put("tags", "wmm");
        fields.put("categories", List.of(
                Map.of("game_type", "marathon", "distance_km", 42.195),
                Map.of("game_type", "half_marathon", "distance_km", 21.0975)
        ));

        sampleResult = CrawlResult.builder()
                .sourceId(1L)
                .sourceKey("world_athletics")
                .sourceGameId("WA-12345")
                .fields(fields)
                .rawData("{\"id\":12345,\"name\":\"北京马拉松\"}")
                .build();
    }

    @Test
    void shouldInsertNewGame() {
        when(crawlerGameMapper.selectByNameAndDate("北京马拉松", LocalDate.of(2026, 10, 18)))
                .thenReturn(null);
        when(gameMergeStrategy.decide(any(), isNull()))
                .thenReturn(MergeAction.INSERT);

        PipelineStats stats = gamePipeline.process(1L, "world_athletics", List.of(sampleResult));

        assertEquals(1, stats.getNewCount());
        assertEquals(0, stats.getUpdatedCount());
        verify(crawlerGameMapper).insert(any(CrawlerGameDO.class));
        // 两个组别
        verify(crawlerGameCategoryMapper, times(2)).insert(any(CrawlerGameCategoryDO.class));
    }

    @Test
    void shouldIgnoreWhenMergeStrategySaysSo() {
        when(crawlerGameMapper.selectByNameAndDate("北京马拉松", LocalDate.of(2026, 10, 18)))
                .thenReturn(new CrawlerGameDO());
        when(gameMergeStrategy.decide(any(), any()))
                .thenReturn(MergeAction.IGNORE);

        PipelineStats stats = gamePipeline.process(1L, "world_athletics", List.of(sampleResult));

        assertEquals(0, stats.getNewCount());
        assertEquals(0, stats.getUpdatedCount());
        assertEquals(1, stats.getIgnoredCount());
    }

    @Test
    void shouldHandleFieldsCorrectly() {
        when(crawlerGameMapper.selectByNameAndDate("北京马拉松", LocalDate.of(2026, 10, 18)))
                .thenReturn(null);
        when(gameMergeStrategy.decide(any(), isNull()))
                .thenReturn(MergeAction.INSERT);

        gamePipeline.process(1L, "world_athletics", List.of(sampleResult));

        // 验证爬取的字段在 rawData 中被正确传递
        assertEquals("北京马拉松", sampleResult.getString("name"));
        assertEquals("2026-10-18", sampleResult.getString("game_date"));
        assertEquals("platinum", sampleResult.getString("world_athletics_level"));
    }
}
