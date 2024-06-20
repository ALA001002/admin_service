package com.bigo.project.bigo.wallet.entity;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/6/27 12:51
 */
@Data
public class WithdrawEntity extends BaseEntity {

    /**
     * 主键id
     */
    private Long id;
    private Long[] ids;
    /**
     * 用户id
     */
    private Long uid;
    private String uids;
    /**
     * 用户id
     */
    private String username;
    /**
     * 代理ID
     */
    private Long agentId;
    /**
     * 币种
     */
    private String coin;
    /**
     * 提币金额
     */
    private BigDecimal money;
    /**
     * 提币手续费
     */
    private BigDecimal fee;
    /**
     * 交易hash值
     */
    private String hash;
    /**
     * 转入地址
     */
    private String from;
    /**
     * 提币到此地址
     */
    private String toAddress;
    /**
     * 备注
     */
    private String remark;
    /**
     * 提现状态 0-待处理 1-成功 2-失败 3-提币中
     */
    private Integer status;
    /**
     * 提币类型 1：内转-出 2：外提-出 3：内转-入 4：外充-入
     */
    private Integer type;
    /**
     * 审核人id （系统用户id)
     */
    private Long operatorId;
    /**
     * 审核人 （系统用户)
     */
    private String operatorName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 审核时间
     */
    private Date verifyTime;
    /**
     * 关联wallet_transaction提币记录id
     */
    private Long transactionId;
    /**
     * 提现状态 0-待审核 1-已通过 2-已驳回
     */
    private Integer checkStatus;
    /**
     * 实际到账金额
     * @return
     */
    public BigDecimal actualMoney;
    /**
     * 充值凭证
     */
    private String photo;
    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 位置
     */
    private String position;

    /**
     * 失败原因
     */
    private String error;

    /**
     * 谷歌验证码
     */
    private Long googleCaptcha;

    private Map withdrawSuccess;

    private Map withdrawFail;
    private Map rechargeSuccess;

    private String auditBeginTime;
    private String auditEndTime;

    private List<Integer> types;

    private String toAddressArr;

    /**
     * 账号类型
     */
    private Integer accountStatus;

    /**
     * 代理商名称
     */
    private String agentName;
    /**
     * 登录IP归属地
     */
    private String lastLoginArea;

    /**
     * 上次登录ip
     */
    private String lastLoginIp;
}
