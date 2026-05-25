package cn.iocoder.yudao.module.crawler.source.worldathletics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过 nimarion REST API 访问 World Athletics 数据。
 *
 * 该服务是 World Athletics GraphQL API 的社区包装（https://worldathletics.nimarion.de），
 * 提供简洁的 REST 接口。
 *
 * 配置:
 *   happy.crawler.world-athletics.api-base-url: https://worldathletics.nimarion.de
 */
@Component
@Slf4j
public class WorldAthleticsGraphQLClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${happy.crawler.world-athletics.api-base-url:https://worldathletics.nimarion.de}")
    private String apiBaseUrl;

    @Value("${happy.crawler.world-athletics.proxy-host:}")
    private String proxyHost;

    @Value("${happy.crawler.world-athletics.proxy-port:0}")
    private int proxyPort;

    public WorldAthleticsGraphQLClient() {
        if (System.getProperty("http.proxyHost") == null && System.getenv("HTTP_PROXY") == null) {
            // 如果 JVM 未配代理，则直接使用默认 RestTemplate
        }
        this.restTemplate = createRestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    private RestTemplate createRestTemplate() {
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setProxy(new java.net.Proxy(java.net.Proxy.Type.HTTP,
                    new java.net.InetSocketAddress(proxyHost, proxyPort)));
            factory.setConnectTimeout(Duration.ofSeconds(10));
            factory.setReadTimeout(Duration.ofSeconds(30));
            return new RestTemplate(factory);
        }
        return new RestTemplateBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * 搜索赛事（按名称关键词）
     */
    public List<NimarionCompetition> searchCompetitions(String name) {
        String url = apiBaseUrl + "/competitions?name=" + encode(name);
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, NimarionCompetition.class));
            }
        } catch (Exception e) {
            log.error("[WA Client] 搜索赛事失败: name={}", name, e);
        }
        return List.of();
    }

    /**
     * 获取赛事成绩
     */
    public NimarionResultsResponse getCompetitionResults(Long competitionId) {
        String url = apiBaseUrl + "/competitions/" + competitionId + "/results";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), NimarionResultsResponse.class);
            }
        } catch (Exception e) {
            log.error("[WA Client] 获取成绩失败: id={}", competitionId, e);
        }
        return null;
    }

    /**
     * 获取赛事组织者信息
     */
    public JsonNode getCompetitionOrganiser(Long competitionId) {
        String url = apiBaseUrl + "/competitions/" + competitionId + "/organiser";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readTree(response.getBody());
            }
        } catch (Exception e) {
            log.error("[WA Client] 获取组织者失败: id={}", competitionId, e);
        }
        return null;
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    // ============ DTOs ============

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionCompetition {
        private Long id;
        private String name;
        private NimarionLocation location;
        private String rankingCategory;
        private List<String> disciplines;
        private String start;
        private String end;
        private String competitionGroup;
        private String competitionSubgroup;
        private Boolean hasResults;
        private Boolean hasStartlist;
        private Boolean hasCompetitionInformation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionLocation {
        private String stadium;
        private String city;
        private String country;
        private Boolean indoor;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionResultsResponse {
        private List<NimarionEvent> events;
        private NimarionOptions options;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionEvent {
        private String eventName;
        private String category;
        private Long eventId;
        private String sex;
        private String disciplineCode;
        private String discipline;
        private Boolean isTechnical;
        private List<NimarionRace> races;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionRace {
        private String date;
        private Integer day;
        private String race;
        private Long raceId;
        private Integer raceNumber;
        private List<NimarionResult> results;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionResult {
        private Integer place;
        private String mark;
        private Long performanceValue; // 毫秒
        private String country;
        private String disciplineCode;
        private String discipline;
        private String date;
        private List<NimarionAthlete> athletes;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionAthlete {
        private Long id;
        private String firstname;
        private String lastname;
        private String birthdate;
        private String country;
        private String sex;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionOptions {
        private List<NimarionDayOption> days;
        private List<NimarionEventOption> events;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionDayOption {
        private String date;
        private Integer day;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NimarionEventOption {
        private Long id;
        private String name;
        private String disciplineCode;
        private String discipline;
        private String sex;
    }
}
