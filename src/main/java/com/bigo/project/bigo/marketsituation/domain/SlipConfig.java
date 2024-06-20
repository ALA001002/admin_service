package com.bigo.project.bigo.marketsituation.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 滑点配置对象 bg_slip_config
 * 
 * @author bigo
 * @date 2021-03-30
 */
@Data
public class SlipConfig {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 开始时间 */
    @Excel(name = "开始时间")
    private Long startTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeDate;

    /** 结束时间 */
    @Excel(name = "结束时间")
    private Long endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTimeDate;

    /** 0表示关闭1表示开启 */
    @Excel(name = "0表示关闭1表示开启")
    private Boolean openFlag;

    private Integer openFlagInter;

    /** 增量 */
    @Excel(name = "增量")
    private BigDecimal addValue;

    private String addValueStr;

    /** 交易对 */
    @Excel(name = "交易对")
    private String symbol;

    /** 0表示未删除 1表示删除 */
    @Excel(name = "0表示未删除 1表示删除")
    private Boolean delFlag;

    private Integer delFlagInter;

    /** 花费时间 */
    @Excel(name = "花费时间")
    private Long intervalTime;

    /** 交易数量 */
    @Excel(name = "交易数量")
    private BigDecimal addAmount;

    /** 交易数量 */
    @Excel(name = "0 递增 1震荡")
    private Byte type;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("openFlag", getOpenFlag())
            .append("addValue", getAddValue())
            .append("symbol", getSymbol())
            .append("delFlag", getDelFlag())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("intervalTime", getIntervalTime())
            .append("addAmount", getAddAmount())
            .toString();
    }
}
