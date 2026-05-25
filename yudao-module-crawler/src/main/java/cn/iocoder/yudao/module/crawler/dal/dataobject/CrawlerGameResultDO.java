package cn.iocoder.yudao.module.crawler.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 临时成绩表（爬虫数据隔离）
 */
@TableName(value = "crawler_game_result", autoResultMap = true)
@KeySequence("crawler_game_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerGameResultDO extends BaseDO {

    @TableId
    private Long id;

    /** 数据源 ID */
    private Long sourceId;

    /** 关联 crawler_game.id */
    private Long crawlerGameId;

    /** 关联 crawler_game_category.id */
    private Long crawlerCategoryId;

    /** 赛事名称（冗余） */
    private String gameName;

    /** 赛事日期（冗余） */
    private LocalDate gameDate;

    /** 参赛号 */
    private String bibNumber;

    /** 选手姓名 */
    private String name;

    /** 英文名/拼音 */
    private String nameEn;

    /** 国籍 */
    private String nationality;

    /** 性别 */
    private String gender;

    /** 年龄组 */
    private String ageGroup;

    /** 身份证号 */
    private String idCard;

    /** 护照号 */
    private String passport;

    /** 枪声成绩（毫秒） */
    private Long gunTimeMs;

    /** 净成绩（毫秒） */
    private Long netTimeMs;

    /** 总排名 */
    private Integer rank;

    /** 性别排名 */
    private Integer genderRank;

    /** 组别排名 */
    private Integer categoryRank;

    /** 完整原始数据 JSON */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String rawData;

    /** 匹配状态：UNMATCHED / MATCHED / CONFLICT / DUPLICATE */
    private String matchStatus;

    /** 匹配到的系统用户 ID */
    private Long matchUserId;

    /** 匹配置信度：HIGH / MEDIUM / LOW */
    private String matchConfidence;

    /** 使用的匹配策略 */
    private String matchStrategy;

    /** 导入状态：PENDING / CONFIRMED / IGNORED / CLAIMED */
    private String importStatus;

    /** 确认导入的用户 ID */
    private Long importUserId;

    /** 导入后的 system_game_registration.id */
    private Long importRegistrationId;

    /** 导入时间 */
    private LocalDateTime importTime;
}
