package cn.iocoder.yudao.module.crawler.source.runchina;

import cn.iocoder.yudao.module.crawler.CrawlerTestApplication;
import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import cn.iocoder.yudao.module.crawler.core.CrawlerContext;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CrawlerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RunchinaCrawlerTest {

    @Resource
    private RunchinaCrawler crawler;

    @Test
    void shouldFetchRecentMatches() throws Exception {
        List<CrawlResult> results = crawler.crawl(CrawlerContext.incremental(null));
        System.out.println("Runchina 爬取结果: " + results.size() + " 条");
        for (CrawlResult r : results) {
            System.out.printf("  %s | %s | 城市=%s | 等级=%s | 组别=%s%n",
                    r.getFields().get("name"),
                    r.getFields().get("game_date"),
                    r.getFields().get("region_name"),
                    r.getFields().get("china_road_run_level"),
                    r.getFields().get("categories"));
        }
        assertFalse(results.isEmpty(), "应该有赛事数据");
        for (CrawlResult r : results) {
            assertNotNull(r.getSourceGameId());
            assertNotNull(r.getFields().get("name"));
            assertNotNull(r.getFields().get("game_date"));
        }
    }
}
