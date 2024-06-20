package com.bigo.project.bigo.holdings.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 智能持仓产品对象 bg_holdings_product
 * 
 * @author bigo
 * @date 2023-12-26
 */
@Data
public class HoldingsProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String name;

    /** 购买人数 */
    @Excel(name = "购买人数")
    private Long buyNumber;

    /** 交易参数 */
    @Excel(name = "交易参数")
    private String symbolJson;

    /** 持仓时间参数 */
    @Excel(name = "持仓时间参数")
    private String timeJson;

    /** 1-常规，2-信号 */
    @Excel(name = "1-常规，2-信号")
    private Long type;


}
