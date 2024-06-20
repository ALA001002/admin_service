package com.bigo.project.bigo.luck.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 抽奖码使用记录对象 bg_lottery_code_record
 * 
 * @author bigo
 * @date 2021-03-29
 */
@Data
public class LotteryCodeRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 抽奖码 */
    @Excel(name = "抽奖码")
    private String lotteryCode;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    private String username;
    private Long agentId;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotteryCode", getLotteryCode())
            .append("uid", getUid())
            .append("createTime", getCreateTime())
            .toString();
    }
}
