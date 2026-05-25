package cn.iocoder.yudao.module.crawler.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    // ========== 数据源 1_002_030_000 ==========
    ErrorCode SOURCE_NOT_FOUND = new ErrorCode(1_002_030_000, "数据源不存在");

    // ========== 任务日志 1_002_030_001 ==========
    ErrorCode TASK_LOG_NOT_FOUND = new ErrorCode(1_002_030_001, "任务日志不存在");

    // ========== 临时成绩 1_002_030_002 ==========
    ErrorCode CRAWLER_RESULT_NOT_FOUND = new ErrorCode(1_002_030_002, "临时成绩记录不存在");
}
