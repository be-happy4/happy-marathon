package cn.iocoder.yudao.module.crawler.match;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.system.dal.dataobject.game.GameRegistrationDO;
import cn.iocoder.yudao.module.system.dal.mysql.game.GameRegistrationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BibNameGameMatchStrategy implements MatchStrategy {

    @Resource
    private GameRegistrationMapper gameRegistrationMapper;

    @Override
    public String getName() {
        return "BIB_NAME_GAME";
    }

    @Override
    public String getConfidence() {
        return "MEDIUM";
    }

    @Override
    public Long match(CrawlerGameResultDO result) {
        String bibNumber = result.getBibNumber();
        String name = result.getName();
        if (bibNumber == null || bibNumber.isBlank() || name == null || name.isBlank()) {
            return null;
        }
        // Query registrations with matching bib number
        List<GameRegistrationDO> registrations = gameRegistrationMapper.selectList(
                new LambdaQueryWrapper<GameRegistrationDO>()
                        .eq(GameRegistrationDO::getBibNumber, bibNumber));
        if (registrations.size() == 1) {
            Long userId = registrations.get(0).getUserId();
            log.info("[BibNameGameMatch] bib={} → userId={}", bibNumber, userId);
            return userId;
        }
        return null;
    }
}
