package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 提现信息mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface WithdrawMapper {

    /**
     * 插入记录
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
     * 更新
     * @param withdraw
     * @return
     */
    int update(Withdraw withdraw);

    /**
     * 获取用户每日提币数量
     * @param params
     * @return
     */
    BigDecimal getDayWithdrawQuantity(Map<String,Object> params);

    /**
     * 是否存在正在审核的提币
     * @param params
     * @return
     */
    Integer isExistWithdraw(Map<String,Object> params);

    /**
     * 根据钱包交易信息获取提币信息
     * @param withdraw
     * @return
     */
    Withdraw getByParam(Withdraw withdraw);

    /**
     * 根据钱包交易信息获取提币信息
     * @param transactionId
     * @return
     */
    Withdraw getById(Long transactionId);

    /**
     * 后台提现审核查询
     * @param entity
     * @return
     */
    List<WithdrawEntity> listByEntity(WithdrawEntity entity);

    BigDecimal getWithdraAmount(WithdrawEntity entity);

    BigDecimal withdrawAuditRecord(WithdrawEntity entity);

    Integer getWithdrawCount(Long uid);

    List<WithdrawEntity> withdrawListByEntity(WithdrawEntity entity);

    List<Withdraw> getManualPayment(Withdraw params);

    BigDecimal getTotalRecharge(WithdrawEntity entity);

    BigDecimal getTotalWithdraw(WithdrawEntity entity);

    BigDecimal rechargeAndWithdrawAmount(Withdraw withdraw);
}
