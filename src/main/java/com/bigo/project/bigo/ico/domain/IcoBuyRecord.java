package com.bigo.project.bigo.ico.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 现货预售购买记录对象 bg_ico_buy_record
 * 
 * @author bigo
 * @date 2023-05-11
 */
@Data
public class IcoBuyRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** 购买币种 */
    @Excel(name = "购买币种")
    private String buyCurrency;

    /** 基础币种 */
    @Excel(name = "基础币种")
    private String quoteCurrency;

    /** 购买数量 */
    @Excel(name = "购买数量")
    private BigDecimal buyNumber;

    /** 购买金额 */
    @Excel(name = "购买金额")
    private BigDecimal buyAmount;

    /** 购买单价 */
    @Excel(name = "购买单价")
    private BigDecimal buyPrice;

    /** 中签概率 */
    @Excel(name = "中签概率")
    private BigDecimal probability;

    /** 申购成功数量 */
    @Excel(name = "申购成功数量")
    private BigDecimal successNumber;

    /** 0-申购中，1-已释放 */
    @Excel(name = "0-申购中，1-已释放")
    private Long status;

    /** 释放时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "释放时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;


}
