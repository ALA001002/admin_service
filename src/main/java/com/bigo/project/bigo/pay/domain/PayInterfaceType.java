package com.bigo.project.bigo.pay.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 支付接口类型对象 bg_pay_interface_type
 * 
 * @author bigo
 * @date 2021-05-20
 */
@Data
public class PayInterfaceType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    @Excel(name = "id")
    private Long id;

    /** 接口类型代码 */
    @Excel(name = "接口类型代码")
    private String ifTypeCode;

    /** 接口类型名称 */
    @Excel(name = "接口类型名称")
    private String ifTypeName;

    /** 状态,0-关闭,1-开启 */
    @Excel(name = "状态,0-关闭,1-开启")
    private Long status;

    /** 接口配置定义描述,json字符串 */
    @Excel(name = "接口配置定义描述,json字符串")
    private String param;


}
