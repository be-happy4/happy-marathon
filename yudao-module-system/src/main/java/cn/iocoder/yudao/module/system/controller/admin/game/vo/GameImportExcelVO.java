package cn.iocoder.yudao.module.system.controller.admin.game.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GameImportExcelVO {

    @ExcelProperty("赛事名称")
    private String name;

    @ExcelProperty("比赛日期")
    private String gameDate;

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

    @ExcelProperty("赛事主页")
    private String remark;

}
