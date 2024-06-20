package com.bigo.project.bigo.luck.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 奖池对象 bg_jackpot
 * 
 * @author bigo
 * @date 2020-12-31
 */
@Data
public class Jackpot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 币种 */
    @Excel(name = "币种")
    private String coin;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal num;

    /** 概率 */
    @Excel(name = "概率")
    private BigDecimal probability;

    public Jackpot() {
    }

    public Jackpot(Long id, String coin, BigDecimal num, BigDecimal probability) {
        this.id = id;
        this.coin = coin;
        this.num = num;
        this.probability = probability;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("coin", getCoin())
            .append("num", getNum())
            .append("probability", getProbability())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .toString();
    }
}
