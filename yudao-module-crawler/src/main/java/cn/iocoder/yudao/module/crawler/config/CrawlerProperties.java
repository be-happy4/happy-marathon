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

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofSeconds(5);
    }
}
