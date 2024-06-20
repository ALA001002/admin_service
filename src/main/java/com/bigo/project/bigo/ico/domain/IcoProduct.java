package com.bigo.project.bigo.ico.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * ico产品对象 bg_ico_product
 * 
 * @author xx
 * @date 2023-01-07
 */
@Data
public class IcoProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 活动币种 */
    @Excel(name = "活动币种")
    private String icoCurrency;

    /** 状态：0-进行中，1-已结束 */
    @Excel(name = "状态：0-进行中，1-已结束")
    private Long status;

    /** 活动总量 */
    @Excel(name = "活动总量")
    private Long totalNum;

    /** 购买价格 */
    @Excel(name = "购买价格")
    private BigDecimal buyPrice;

    /** 购买币种 */
    @Excel(name = "购买币种")
    private String buyCurrency;

    /** 个人购买次数 */
    @Excel(name = "个人购买次数")
    private Long buyTimes;

    /** 个人购买数量 */
    @Excel(name = "个人购买数量")
    private Long buyNum;

    /** 已购买数量 */
    @Excel(name = "已购买数量")
    private Long boughtNum;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date overTime;


    /**
     * 币种图标
     */
    private String logoImg;

    public IcoProduct() {
    }

    public IcoProduct(String icoCurrency) {
        this.icoCurrency = icoCurrency;
    }
}
