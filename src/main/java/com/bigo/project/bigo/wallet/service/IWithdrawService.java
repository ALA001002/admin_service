package com.bigo.project.bigo.wallet.service;

import com.bigo.project.bigo.api.dto.TransDTO;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.system.domain.SysUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 提现service
 * @Author wenxm
 * @Date 2020/6/20 10:02
 */
public interface IWithdrawService {

    /**
     * 提现
     * @param withdraw
     * @return
     */
    Boolean withdraw(Withdraw withdraw);

    /**
     * 插入充提币记录
     * @param withdraw
     * @return
     */
    int insert(Withdraw withdraw);

    /**
     * 查询提现列表
     * @param withdraw
     * @return
     */
    List<Withdraw> listWithdraw(Withdraw withdraw);

    /**
     * 根据钱包交易信息获取提币信息
     * @param transactionId
     * @return
     */
    Withdraw getByTransactionId(Long transactionId);

    /**
     * 根据id获取提币信息
     * @param id
     * @return
     */
    Withdraw getById(Long id);

    /**
     * 更新
     * @param withdraw
     * @return
     */
    int update(Withdraw withdraw);

    /**
     * 后台提现审核查询
     * @param entity
     * @return
     */
    List<WithdrawEntity> listByEntity(WithdrawEntity entity);

    /**
     * 审核提币申请
     * @param withdraw
     * @return
     */
    Boolean checkWithdraw(Withdraw withdraw);


    BigDecimal getWithdraAmount(Long uid, String currency, Integer type, Integer checkStatus, Integer Status);


    Object withdrawAuditRecord(WithdrawEntity entity);

    void checkRecharge(Withdraw withdraw);

    void agentPayWithdraw(WithdrawEntity entity, SysUser sysUser);

    void offlinePay(WithdrawEntity entity, SysUser sysUser);

    List<WithdrawEntity> withdrawListByEntity(WithdrawEntity entity);

    void manualPayment(Withdraw withdraw);

    List<Withdraw> getManualPayment(Withdraw params);

    Map<String, BigDecimal> getTotalForm(WithdrawEntity entity);

    BigDecimal rechargeAndWithdrawAmount(Withdraw params);
}
