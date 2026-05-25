package cn.iocoder.yudao.module.crawler.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 临时组别表
 */
@TableName("crawler_game_category")
@KeySequence("crawler_game_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerGameCategoryDO extends BaseDO {

    @TableId
    private Long id;

    /** 关联 crawler_game.id */
    private Long crawlerGameId;

    /** 组别类型 dict code */
    private String gameType;

    /** 距离（km） */
    private BigDecimal distanceKm;
}
