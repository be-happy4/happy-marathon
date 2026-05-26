package cn.iocoder.yudao.module.crawler.job;

import cn.iocoder.yudao.module.crawler.CrawlerTestApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 手动触发赛事爬取（测试用）
 */
@SpringBootTest(classes = CrawlerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CrawlGameJobManualTest {

    @Resource
    private CrawlGameJob crawlGameJob;

    @Test
    void crawlGames() throws Exception {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7897");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7897");

        String result = crawlGameJob.execute(null);
        System.out.println("Result: " + result);
    }
}
