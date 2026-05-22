package cn.iocoder.yudao.module.system.dal.dataobject.game;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@TableName("system_game_category")
@KeySequence("system_game_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameCategoryDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 赛事编号 */
    private Long gameId;
    /** 组别类型，字典 game_type */
    private String gameType;
    /** 该组别距离（米） */
    private BigDecimal distanceM;

}
