package cn.iocoder.yudao.module.crawler.source.worldathletics;

import cn.iocoder.yudao.module.crawler.core.CrawlResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WorldAthleticsCrawler 单元测试。
 * 不依赖网络，直接测解析逻辑。
 */
class WorldAthleticsCrawlerTest {

    private final WorldAthleticsCrawler crawler = new WorldAthleticsCrawler(null);

    @Test
    void shouldMapNimarionCompetitionToResult() {
        WorldAthleticsGraphQLClient.NimarionCompetition comp = new WorldAthleticsGraphQLClient.NimarionCompetition();
        comp.setId(7242302L);
        comp.setName("Nanning Marathon");
        WorldAthleticsGraphQLClient.NimarionLocation loc = new WorldAthleticsGraphQLClient.NimarionLocation();
        loc.setCity("Nanning");
        loc.setCountry("CHN");
        comp.setLocation(loc);
        comp.setStart("2026-12-20T00:00:00.000Z");
        comp.setEnd("2026-12-20T00:00:00.000Z");
        comp.setCompetitionGroup("World Athletics Label Road Races – Elite");
        comp.setDisciplines(List.of("Road Running"));
        comp.setHasResults(false);

        CrawlResult result = crawler.mapToResult(comp);

        assertEquals("Nanning Marathon", result.getString("name"));
        assertEquals("Nanning, CHN", result.getString("region_name"));
        assertEquals("elite", result.getString("world_athletics_level"));
        assertEquals("2026-12-20", result.getString("game_date"));
        assertEquals("7242302", result.getSourceGameId());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cats = (List<Map<String, Object>>) result.getFields().get("categories");
        assertNotNull(cats);
        assertFalse(cats.isEmpty());
        assertEquals("marathon", cats.get(0).get("game_type"));
    }

    @Test
    void shouldDeriveLabelFromCompetitionGroup() {
        assertEquals("platinum", WorldAthleticsCrawler.deriveLabel("World Athletics Label Road Races – Platinum"));
        assertEquals("gold", WorldAthleticsCrawler.deriveLabel("World Athletics Label Road Races – Gold"));
        assertEquals("elite", WorldAthleticsCrawler.deriveLabel("World Athletics Label Road Races – Elite"));
        assertEquals("label", WorldAthleticsCrawler.deriveLabel("World Athletics Label Road Races – Label"));
        assertNull(WorldAthleticsCrawler.deriveLabel(null));
        assertNull(WorldAthleticsCrawler.deriveLabel("Some Other Category"));
    }

    @Test
    void shouldBuildRegion() {
        WorldAthleticsGraphQLClient.NimarionLocation loc = new WorldAthleticsGraphQLClient.NimarionLocation();
        loc.setCity("Beijing");
        loc.setCountry("CHN");
        assertEquals("Beijing, CHN", WorldAthleticsCrawler.buildRegion(loc));
    }

    @Test
    void shouldDeriveStatus() {
        // 未来
        assertEquals("announced", WorldAthleticsCrawler.deriveStatus("2026-12-20T00:00:00.000Z", null));
        // 过去
        assertEquals("finished", WorldAthleticsCrawler.deriveStatus("2020-01-01T00:00:00.000Z", "2020-01-01T00:00:00.000Z"));
        // 空
        assertEquals("uncertain", WorldAthleticsCrawler.deriveStatus(null, null));
    }

    @Test
    void shouldInferGameTypeFromName() {
        assertEquals("marathon", WorldAthleticsCrawler.inferGameType("nanning marathon", "Road Running"));
        assertEquals("half_marathon", WorldAthleticsCrawler.inferGameType("yangling half marathon", "Road Running"));
        assertEquals("road_run", WorldAthleticsCrawler.inferGameType("some 10km race", "Road Running"));
        assertEquals("trail_run", WorldAthleticsCrawler.inferGameType("mountain trail run", "Road Running"));
    }

    @Test
    void shouldFilterNonRoadRunning() {
        assertTrue(WorldAthleticsCrawler.isRoadRunning(List.of("Road Running")));
        assertFalse(WorldAthleticsCrawler.isRoadRunning(List.of("Track and Field")));
        assertFalse(WorldAthleticsCrawler.isRoadRunning(List.of("Cross Country")));
    }
}
