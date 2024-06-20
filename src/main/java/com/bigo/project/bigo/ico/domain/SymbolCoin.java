package com.bigo.project.bigo.ico.domain;

import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 发币对象 bg_symbol_coin
 * 
 * @date 2023-01-06
 */
@Data
public class SymbolCoin extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 名称 */
    @Excel(name = "名称")
    private String enumName;

    /** 币种代码 */
    @Excel(name = "币种代码")
    private String code;

    /** 币种名称 */
    @Excel(name = "币种名称")
    private String name;

    /** 状态：0-锁仓，1-开放 */
    @Excel(name = "状态：0-锁仓，1-开放")
    private Integer status;

    /** 交易次数 */
    @Excel(name = "交易次数")
    private Long tradeCount;


    /** 锁仓结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "锁仓结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lockoutEndTime;


}
