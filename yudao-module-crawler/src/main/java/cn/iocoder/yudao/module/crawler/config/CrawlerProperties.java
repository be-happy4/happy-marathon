package cn.iocoder.yudao.module.crawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 爬虫全局配置。
 */
@Component
@ConfigurationProperties(prefix = "happy.crawler")
@Data
public class CrawlerProperties {

    /** 全局开关 */
    private boolean enabled = true;

    /** 连接超时 */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /** 读取超时 */
    private Duration readTimeout = Duration.ofSeconds(30);

    /** 重试配置 */
    private Retry retry = new Retry();

    /** Playwright 浏览器配置 */
    private Playwright playwright = new Playwright();

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofSeconds(5);
    }

    @Data
    public static class Playwright {
        /** 是否无头模式，默认 false（方便开发调试） */
        private boolean headless = false;
        /** 浏览器超时 */
        private Duration timeout = Duration.ofSeconds(60);
    }
}
