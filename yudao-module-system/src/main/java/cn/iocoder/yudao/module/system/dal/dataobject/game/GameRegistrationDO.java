package cn.iocoder.yudao.module.system.dal.dataobject.game;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 赛事个人报名表
 */
@TableName("system_game_registration")
@KeySequence("system_game_registration_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class GameRegistrationDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 赛事编号 */
    private Long gameId;
    /** 用户编号 */
    private Long userId;
    /** 个人报名状态，字典 game_registration_status */
    private String registrationStatus;
    /** 参赛组别，字典 game_type */
    private String gameType;
    /** 个人优先级 */
    private Integer priority;
    /** 个人标签，多个以逗号分隔，字典 game_tag */
    private String tags;
    /** 参赛号码 */
    private String bibNumber;
    /** 枪声成绩（毫秒） */
    private Long gunTimeMs;
    /** 净成绩（毫秒） */
    private Long netTimeMs;
    /** 性别排名 */
    private Integer genderPlace;
    /** 总排名 */
    private Integer overallPlace;
    /** 实际完成距离（公里） */
    private BigDecimal distanceKm;
    /** 备注 */
    private String remark;

}
