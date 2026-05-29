package cn.iocoder.yudao.module.crawler.match;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdCardMatchStrategy implements MatchStrategy {

    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public String getName() {
        return "ID_CARD";
    }

    @Override
    public String getConfidence() {
        return "HIGH";
    }

    @Override
    public Long match(CrawlerGameResultDO result) {
        String idCard = result.getIdCard();
        if (idCard == null || idCard.isBlank()) {
            return null;
        }
        AdminUserDO user = adminUserMapper.selectByIdCard(idCard);
        if (user != null) {
            log.info("[IdCardMatch] {} → userId={}", result.getName(), user.getId());
            return user.getId();
        }
        return null;
    }
}
