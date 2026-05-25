package cn.iocoder.yudao.module.crawler.merge;

import cn.iocoder.yudao.module.crawler.dal.dataobject.CrawlerGameResultDO;
import org.springframework.stereotype.Component;

/**
 * 成绩去重合并策略。
 * 唯一键：(crawler_game_id, crawler_category_id, id_card)
 * - 不存在 → INSERT
 * - 存在 + 状态 PENDING / UNMATCHED → UPDATE
 * - 存在 + 状态 CONFIRMED / IGNORED → IGNORE
 */
@Component
public class ResultMergeStrategy implements MergeStrategy<CrawlerGameResultDO> {

    @Override
    public MergeAction decide(CrawlerGameResultDO crawled, CrawlerGameResultDO existing) {
        if (existing == null) {
            return MergeAction.INSERT;
        }
        String importStatus = existing.getImportStatus();
        if ("CONFIRMED".equals(importStatus) || "IGNORED".equals(importStatus)) {
            return MergeAction.IGNORE;
        }
        // PENDING / CLAIMED 允许更新（更新成绩数据）
        return MergeAction.UPDATE;
    }
}
