package com.bigo.project.bigo.pay.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 银行信息对象 bg_bank_info
 * 
 * @author bigo
 * @date 2022-05-23
 */
@Data
public class BankInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 国家名称(中文) */
    @Excel(name = "国家名称(中文)")
    private String countryCh;

    /** 国家名称(英文) */
    @Excel(name = "国家名称(英文)")
    private String countryEn;

    /** 区号 */
    @Excel(name = "区号")
    private String mobilePrefix;

    /** 银行编码 */
    @Excel(name = "银行编码")
    private String bankCode;

    /** 银行名称 */
    @Excel(name = "银行名称")
    private String bankName;

    /** 是否可用 0-否，1-是 */
    @Excel(name = "是否可用 0-否，1-是")
    private Long status;



    @Excel(name = "银行数字编码")
    private Long bankNumber;

    private String currency;


}
