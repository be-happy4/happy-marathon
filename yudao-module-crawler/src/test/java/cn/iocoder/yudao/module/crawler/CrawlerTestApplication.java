package cn.iocoder.yudao.module.crawler;

import cn.iocoder.yudao.module.crawler.match.MatchEngine;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "cn.iocoder.yudao.module.crawler",
        "cn.iocoder.yudao.module.system",
        "cn.iocoder.yudao.module.infra"
}, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = MatchEngine.class
))
public class CrawlerTestApplication {
}
