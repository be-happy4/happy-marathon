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
 * 临时赛事表（爬虫数据隔离）
 */
@TableName(value = "crawler_game", autoResultMap = true)
@KeySequence("crawler_game_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerGameDO extends BaseDO {

    @TableId
    private Long id;

    /** 数据源 ID */
    private Long sourceId;

    /** 源系统中的原始 ID */
    private String sourceGameId;

    /** 赛事名称 */
    private String name;

    /** 英文名称 */
    private String nameEn;

    /** 比赛日期 */
    private LocalDate gameDate;

    /** 原始地区名称 */
    private String regionName;

    /** ETL 后匹配的 region_id */
    private Long regionId;

    /** 国际田联标牌等级 */
    private String worldAthleticsLevel;

    /** 中国田协路跑等级 */
    private String chinaRoadRunLevel;

    /** 赛事状态 */
    private String status;

    /** 标签 */
    private String tags;

    /** 来源系统描述 */
    private String remark;

    /** 完整原始数据 JSON */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String rawData;

    /** 导入状态：PENDING / IMPORTED / IGNORED */
    private String importStatus;

    /** ETL 导入时间 */
    private LocalDateTime importTime;

    /** 导入后对应的 system_game.id */
    private Long importGameId;

    /** 导入时的 ETL 版本 */
    private String importEtlVersion;
}
