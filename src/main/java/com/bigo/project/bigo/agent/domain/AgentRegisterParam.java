package com.bigo.project.bigo.agent.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @description: 代理商注册参数
 * @author: wenxm
 * @date: 2020/7/29 16:41
 */
@Data
public class AgentRegisterParam {

    /**
     * 代理商ID
     */
    private Long agentId;
    /** 用户账号 */
    @NotBlank(message = "用户名不能为空")
    private String userName;
    /** 用户昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickName;
    /** 用户邮箱 */
    @NotBlank(message = "邮箱不能为空")
    private String email;
    /** 手机号码 */
    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;
    /** 用户性别 */
    private String sex;
    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 交易手续费分成比例
     */
    @NotNull(message = "手续费分成比例不能为空")
    private BigDecimal feeShareRate;
    /**
     * 头寸分成比例
     */
    @NotNull(message = "头寸分成比例不能为空")
    private BigDecimal profitShareRate;
    /**
     * 备付金
     */
    @NotNull(message = "备付金不能为空")
    private BigDecimal cashDeposit;

}
