package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 钱包充提币
 * @Author wenxm
 * @Date 2020/6/18 9:58
 */
@Data
public class WalletTransaction extends BaseEntity {

    /**
     * 主键
     */
    @Excel(name = "id")
    private Long id;
    /**
     * 用户id
     */
    @Excel(name = "用户ID")
    private Long uid;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String username;

    /**
     * 上级ID
     */
    @Excel(name = "上级ID")
    private Long parentUid;

    @Excel(name = "业务员ID")
    private Long salesmanId;

    /**
     * 顶级ID
     */
    @Excel(name = "顶级ID")
    private Long topUid;
    /**
     * 币种
     */
    @Excel(name = "币种")
    private String coin;
    /**
     * 方向 1|充值 2|提现
     */
    @Excel(name = "交易类型", readConverterExp = "0=打手续费,1=充值,2=提现")
    private Integer type;
    /**
     * 金额
     */
    @Excel(name = "金额")
    private BigDecimal money;
    /**
     * 折算价格
     */
    @Excel(name = "折算价格")
    private BigDecimal convertedPrice;
    /**
     * 交易对价格
     */
    @Excel(name = "行情价格")
    private BigDecimal symbolPrice;
    /**
     * 费用
     */
    private BigDecimal fee;
    /**
     * 转出地址
     */
    @Excel(name = "转出地址")
    private String from;
    /**
     * 目标地址
     */
    @Excel(name = "目标地址")
    private String to;
    /**
     * 状态 0|待处理 1|处理中 2|成功 3|失败
     */
    @Excel(name = "状态", readConverterExp = "0=待处理,1=处理中,2=成功,3=失败")
    private Integer status;

    /**
     * 交易哈希
     */
    @Excel(name = "交易哈希")
    private String hash;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 错误信息
     */
    @Excel(name = "错误信息")
    private String error;
    /**
     * 钱包处理状态 0-未处理 1-已加入余额 2-处理失败
     */
    @Excel(name = "钱包处理状态", readConverterExp = "0=未处理,1=已加入余额,2=处理失败")
    private Integer handleStatus;

    private String lowerUids;

}
