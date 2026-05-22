package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExcelIgnoreUnannotated
@Data
public class GameExportVO {

    @ExcelProperty("赛事名称")
    private String name;

    @ExcelProperty("比赛日期")
    private LocalDate gameDate;

    @ExcelProperty("赛事类型")
    private String gameType;

    @ExcelProperty("国际田联")
    private String worldAthleticsLevel;

    @ExcelProperty("中国田联")
    private String chinaRoadRunLevel;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("标签")
    private String tags;

    @ExcelProperty("官网")
    private String url;

    @ExcelProperty("地区")
    private String regionName;

    @ExcelProperty("赛事主页")
    private String remark;

}
