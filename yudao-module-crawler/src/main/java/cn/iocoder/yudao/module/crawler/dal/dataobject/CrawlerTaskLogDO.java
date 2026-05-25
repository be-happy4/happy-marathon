package cn.iocoder.yudao.module.crawler.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 爬取任务日志
 */
@TableName("crawler_task_log")
@KeySequence("crawler_task_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrawlerTaskLogDO extends BaseDO {

    @TableId
    private Long id;

    /** 数据源 ID */
    private Long sourceId;

    /** 爬取类型 */
    private String crawlType;

    /** 爬取模式 */
    private String crawlMode;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 状态：RUNNING / SUCCESS / FAILED */
    private String status;

    /** 本次爬取到的原始条数 */
    private Integer totalFetched;

    /** 新增条数 */
    private Integer totalNew;

    /** 更新条数 */
    private Integer totalUpdated;

    /** 跳过条数 */
    private Integer totalIgnored;

    /** 错误信息 */
    private String errorMsg;

    /** ETL 版本标识 */
    private String etlVersion;
}
