package cn.iocoder.yudao.module.crawler.core;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerSourceDO;
import cn.iocoder.yudao.module.crawler.dal.mysql.CrawlerSourceMapper;
import cn.iocoder.yudao.module.crawler.generic.GenericApiCrawler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 爬虫注册中心。
 * 启动时扫描 @Component Crawler 实现 + crawler_source 表中 handler_type=GENERIC 的记录，
 * 统一管理所有爬虫实例。
 */
@Component
@Slf4j
public class CrawlerRegistry {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private CrawlerSourceMapper crawlerSourceMapper;

    /** sourceKey → Crawler */
    private Map<String, Crawler> crawlerMap = Map.of();

    @PostConstruct
    public void init() {
        Map<String, Crawler> map = new HashMap<>();

        // 1. 扫描所有 @Component Crawler 实现（CUSTOM 类型）
        Map<String, Crawler> customCrawlers = applicationContext.getBeansOfType(Crawler.class);
        for (Crawler c : customCrawlers.values()) {
            map.put(c.getSourceKey(), c);
            log.info("[init][CUSTOM] 注册爬虫: {}", c.getSourceKey());
        }

        // 2. 加载 crawler_source 表中 handler_type=GENERIC 且 enabled=true 的记录
        List<CrawlerSourceDO> genericSources = crawlerSourceMapper.selectList(
                CrawlerSourceDO::getHandlerType, "GENERIC");
        for (CrawlerSourceDO source : genericSources) {
            if (source.getEnabled() != null && source.getEnabled()) {
                GenericApiCrawler generic = new GenericApiCrawler(source);
                map.put(source.getSourceKey(), generic);
                log.info("[init][GENERIC] 注册爬虫: {}", source.getSourceKey());
            }
        }

        this.crawlerMap = Collections.unmodifiableMap(map);
        log.info("[init] 共注册 {} 个爬虫", map.size());
    }

    /** 获取指定类型的爬虫列表 */
    public List<Crawler> getCrawlersByType(CrawlType type) {
        return crawlerMap.values().stream()
                .filter(c -> c.getType() == type || c.getType() == CrawlType.valueOf("BOTH"))
                .collect(Collectors.toList());
    }

    /** 根据 sourceKey 获取爬虫 */
    public Crawler getCrawler(String sourceKey) {
        return crawlerMap.get(sourceKey);
    }

    /** 所有已注册爬虫 */
    public Collection<Crawler> getAllCrawlers() {
        return crawlerMap.values();
    }
}
