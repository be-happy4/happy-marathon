package cn.iocoder.yudao.module.system.dal.dataobject.game;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("system_region")
@KeySequence("system_region_seq")
@Data
@EqualsAndHashCode(callSuper = true)
public class RegionDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 地区名称 */
    private String name;
    /** ISO 3166 代码 */
    private String code;
    /** 地区类型：1=洲 2=国家 3=省份/州 4=城市 */
    private Integer type;
    /** 父级编号 */
    private Long parentId;
    /** 排序 */
    private Integer sort;
    /** GB/T 2260 行政区划代码（中国地区） */
    private String gbCode;
    /** 租户编号 */
    private Long tenantId;

}
