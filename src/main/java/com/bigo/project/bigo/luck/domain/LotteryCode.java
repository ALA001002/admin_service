package com.bigo.project.bigo.luck.domain;

import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 抽奖码对象 bg_lottery_code
 * 
 * @author bigo
 * @date 2020-12-30
 */
@Data
public class LotteryCode extends BaseEntity
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

    /** 抽奖码状态：1.未使用，2.已使用，3.已过期 */
    @Excel(name = "抽奖码状态：1.未使用，2.已使用，3.已过期")
    private Integer status;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date overdueTime;

    /**
     * 代理商IDList
     */
    private String agentIds;

    public LotteryCode() {
    }

    public LotteryCode(Long uid, String lotteryCode, Integer status) {
        this.uid = uid;
        this.lotteryCode = lotteryCode;
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("lotteryCode", getLotteryCode())
            .append("uid", getUid())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("overdueTime", getOverdueTime())
            .toString();
    }
}
