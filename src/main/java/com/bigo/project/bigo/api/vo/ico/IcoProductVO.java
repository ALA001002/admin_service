package com.bigo.project.bigo.api.vo.ico;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class IcoProductVO {

    /** id */
    private Long id;

    /** 活动币种 */
    private String icoCurrency;

    /** 状态：0-进行中，1-已结束 */
    private Long status;

    /** 活动总量 */
    private Long totalNum;

    /** 购买价格 */
    private BigDecimal buyPrice;

    /** 购买币种 */
    private String buyCurrency;

    /** 个人购买次数 */
    private Long buyTimes;

    /** 个人购买数量 */
    private Long buyNum;

    /** 已购买数量 */
    private Long boughtNum;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date overTime;

    /**
     * 币种图标
     */
    private String logoImg;

    public IcoProductVO() {
    }

    public IcoProductVO(Long id) {
        this.id = id;
    }
}
