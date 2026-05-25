package cn.iocoder.yudao.module.crawler.source.worldathletics;

import lombok.Data;
import java.util.List;

/**
 * GraphQL query: getCalendarEvents 的响应结构
 */
@Data
public class WorldAthleticsCalendarResponse {

    private List<CalendarEvent> results;
    private Integer total;

    /**
     * 单个赛事事件
     */
    @Data
    public static class CalendarEvent {
        private Long id;
        private String name;
        private String venue;
        private String area;
        private String startDate;
        private String endDate;
        private String rankingCategory;
        private String dateRange;
        private List<String> disciplines;
        private Boolean hasResults;
        private Boolean hasApiResults;
    }
}
