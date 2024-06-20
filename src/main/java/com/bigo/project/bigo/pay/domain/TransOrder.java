package com.bigo.project.bigo.pay.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.bigo.framework.web.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

/**
 * 代付对象 bg_trans_order
 * 
 * @author bigo
 * @date 2022-05-23
 */
@Data
public class TransOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 代付订单ID */
    @Excel(name = "代付订单ID")
    private String transOrderId;

    /** 上游订单号 */
    @Excel(name = "上游订单号")
    private String channelOrderId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 代付金额（虚拟货币） */
    @Excel(name = "代付金额", readConverterExp = "虚=拟货币")
    private BigDecimal amount;

    /** 手续费 */
    @Excel(name = "手续费")
    private BigDecimal fee;

    /** 货币金额（真实货币） */
    @Excel(name = "货币金额", readConverterExp = "真=实货币")
    private BigDecimal currencyAmount;

    /** 支付状态：0-订单生成,1-代付中,2-代付成功,3-代付失败 */
    @Excel(name = "支付状态：0-订单生成,1-代付中,2-代付成功,3-代付失败")
    private Integer status;

    /** 通道id */
    @Excel(name = "通道id")
    private Long payPassageId;

    /** 提现id */
    @Excel(name = "提现id")
    private Long withdrawId;

    /** 三位货币代码:美元USD */
    @Excel(name = "三位货币代码:美元USD")
    private String currency;

    /** 收款银行代码 */
    @Excel(name = "收款银行代码")
    private String bankCode;

    @Excel(name = "银行编码")
    private Long bankNumber;

    /** 账户名 */
    @Excel(name = "账户名")
    private String accountName;

    /** 账户号 */
    @Excel(name = "账户号")
    private String accountNo;

    /** 开户行名称 */
    @Excel(name = "开户行名称")
    private String bankName;

    /** 收款人当地手机号码 */
    @Excel(name = "收款人当地手机号码")
    private String receiverPhone;

    /** 扩展参数 */
    @Excel(name = "扩展参数")
    private String extra;

    /** 转账成功时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "转账成功时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date transSuccTime;

    /** 回调地址 */
    @Excel(name = "回调地址")
    private String notifyUrl;

    private Long[] ids;


    private String username;

    // 审核状态
    private Integer checkStatus;

    //谷歌验证码
    private Long googleCaptcha;

}
