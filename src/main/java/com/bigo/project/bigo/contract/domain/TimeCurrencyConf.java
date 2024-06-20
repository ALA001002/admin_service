package com.bigo.project.bigo.contract.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 限时币种配置对象 bg_time_currency_conf
 * 
 * @author WY
 * @date 2021-02-01
 */
@Data
public class TimeCurrencyConf extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /**名称*/
    private String name;

    /** 交易对 */
    @Excel(name = "交易对")
    private String symbol;

    /** 盈 */
    @Excel(name = "盈")
    private BigDecimal surplus;

    /** 亏 */
    @Excel(name = "亏")
    private BigDecimal deficit;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("symbol", getSymbol())
            .append("surplus", getSurplus())
            .append("deficit", getDeficit())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
