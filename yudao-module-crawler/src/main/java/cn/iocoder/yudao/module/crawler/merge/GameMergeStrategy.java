package cn.iocoder.yudao.module.crawler.merge;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameDO;
import org.springframework.stereotype.Component;

/**
 * 赛事去重合并策略。
 * 唯一键：(name, game_date)
 * - 不存在 → INSERT
 * - 存在 + 状态 PENDING → UPDATE
 * - 存在 + 状态已导入 → IGNORE
 */
@Component
public class GameMergeStrategy implements MergeStrategy<CrawlerGameDO> {

    @Override
    public MergeAction decide(CrawlerGameDO crawled, CrawlerGameDO existing) {
        if (existing == null) {
            return MergeAction.INSERT;
        }
        // 已导入的不再更新
        if ("IMPORTED".equals(existing.getImportStatus())) {
            return MergeAction.IGNORE;
        }
        // PENDING / IGNORED 状态允许更新
        return MergeAction.UPDATE;
    }
}
