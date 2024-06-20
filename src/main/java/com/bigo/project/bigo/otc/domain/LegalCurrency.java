package com.bigo.project.bigo.otc.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 法币
 * @author: wenxm
 * @date: 2020/7/20 20:29
 */
@Data
public class LegalCurrency extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 法币
     */
    @NotBlank(message = "法币编码不能为空")
    private String legalCurrency;
    /**
     * 名称
     */
    @NotBlank(message = "法币名称不能为空")
    private String name;
    /**
     * 买入汇率
     */
    @NotNull(message = "买入汇率不能为空")
    private BigDecimal buyRate;
    /**
     * 卖出汇率
     */
    @NotNull(message = "卖出汇率不能为空")
    private BigDecimal sellRate;
    /**
     * 银行
     */
    @NotNull(message = "银行不能为空")
    private String bankName;
    /**
     * 收款账户
     */
    @NotNull(message = "银行卡号不能为空")
    private String bankAccount;
    /**
     * 支行
     */
    private String bankBranch;
    /**
     * 收款人
     */
    @NotNull(message = "收款人不能为空")
    private String payee;
    /**
     * 0-可用 1-不可用
     */
    @NotNull(message = "可用状态不能为空")
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人ID
     */
    private Long creatorId;
    /**
     * 更新人ID
     */
    private Long operatorId;
}
