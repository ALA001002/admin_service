package com.bigo.project.bigo.luck.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 中奖记录对象 bg_winning_record
 * 
 * @author bigo
 * @date 2020-12-31
 */
@Data
public class WinningRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 币种 */
    @Excel(name = "币种")
    private String coin;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal num;

    @Excel(name = "抽奖码")
    private String lotteryCode;

    // 扩展字段
    /** 用户账号 */
    private String username;

    private Long agentId;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("uid", getUid())
            .append("coin", getCoin())
            .append("num", getNum())
            .append("createTime", getCreateTime())
            .toString();
    }
}
