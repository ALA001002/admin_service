package com.bigo.project.bigo.api.vo;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransOrderVO {


    /** id */
    private Long id;

    /** 代付订单ID */
    private String transOrderId;


    /** 用户id */
    private Long uid;

    /** 代付金额（虚拟货币） */
    private BigDecimal amount;

    /** 手续费 */
    private BigDecimal fee;

    /** 货币金额（真实货币） */
    private BigDecimal currencyAmount;

    /** 支付状态：0-订单生成,1-代付中,2-代付成功,3-代付失败 */
    private Integer status;

    private Date transSuccTime;


}
