package cn.iocoder.yudao.module.system.controller.admin.gameregistration.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ExcelIgnoreUnannotated
@Data
public class GameRegistrationExportVO {

    @ExcelProperty("赛事名称")
    private String gameName;

    @ExcelProperty("比赛日期")
    private String gameDate;

    @ExcelProperty("用户昵称")
    private String userNickname;

    @ExcelProperty("报名状态")
    private String registrationStatus;

    @ExcelProperty("组别")
    private String gameCategoryName;

    @ExcelProperty("距离(km)")
    private String distanceKm;

    @ExcelProperty("配速")
    private String pace;

    @ExcelProperty("时速(km/h)")
    private Double speed;

    @ExcelProperty("优先级")
    private Integer priority;

    @ExcelProperty("参赛号")
    private String bibNumber;

    @ExcelProperty("净成绩")
    private String netTime;

    @ExcelProperty("枪声成绩")
    private String gunTime;

    @ExcelProperty("总排名")
    private Integer overallPlace;

    @ExcelProperty("备注")
    private String remark;

}
