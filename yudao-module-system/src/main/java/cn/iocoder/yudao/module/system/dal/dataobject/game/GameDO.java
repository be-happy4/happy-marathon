package cn.iocoder.yudao.module.system.dal.dataobject.game;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 赛事表
 */
@TableName("system_game")
@KeySequence("system_game_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 赛事名称 */
    private String name;
    /** 比赛日期 */
    private LocalDate gameDate;
    /** 赛事类型，字典 game_type */
    private String gameType;
    /** 国际田联标牌等级，字典 game_world_athletics_label_level */
    private String worldAthleticsLevel;
    /** 中国田联路跑赛事等级，字典 game_china_road_run_game_level */
    private String chinaRoadRunLevel;
    /** 赛事状态，字典 game_status */
    private String status;
    /** 赛事标签，多个以逗号分隔，字典 game_tag */
    private String tags;
    /** 赛事官网链接 */
    private String url;
    /** 赛事距离（公里） */
    private BigDecimal distanceKm;
    /** 备注 */
    private String remark;

}
