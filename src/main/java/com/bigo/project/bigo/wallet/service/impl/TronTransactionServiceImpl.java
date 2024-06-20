package com.bigo.project.bigo.wallet.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.wallet.dao.TransactionRepository;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.jpaEntity.Transaction;
import com.bigo.project.bigo.wallet.mapper.TronTransactionMapper;
import com.bigo.project.bigo.wallet.mapper.WithdrawMapper;
import com.bigo.project.bigo.wallet.service.ITronTransactionService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Trx充提Service业务层处理
 * 
 * @author bigo
 * @date 2021-11-23
 */
@Service
public class TronTransactionServiceImpl implements ITronTransactionService 
{
    @Autowired
    private TronTransactionMapper tronTransactionMapper;

    @Resource
    IWalletService iWalletService;

    @Resource
    WithdrawMapper withdrawMapper;

    @Resource
    TransactionRepository transactionRepository;

    /**
     * 查询Trx充提
     * 
     * @param id Trx充提ID
     * @return Trx充提
     */
    @Override
    public TronTransaction selectTronTransactionById(Long id)
    {
        return tronTransactionMapper.selectTronTransactionById(id);
    }

    /**
     * 查询Trx充提列表
     * 
     * @param tronTransaction Trx充提
     * @return Trx充提
     */
    @Override
    public List<TronTransaction> selectTronTransactionList(TronTransaction tronTransaction)
    {
        return tronTransactionMapper.selectTronTransactionList(tronTransaction);
    }

    /**
     * 新增Trx充提
     * 
     * @param tronTransaction Trx充提
     * @return 结果
     */
    @Override
    public int insertTronTransaction(TronTransaction tronTransaction)
    {
        return tronTransactionMapper.insertTronTransaction(tronTransaction);
    }

    /**
     * 修改Trx充提
     * 
     * @param tronTransaction Trx充提
     * @return 结果
     */
    @Override
    public int updateTronTransaction(TronTransaction tronTransaction)
    {
        return tronTransactionMapper.updateTronTransaction(tronTransaction);
    }

    /**
     * 批量删除Trx充提
     * 
     * @param ids 需要删除的Trx充提ID
     * @return 结果
     */
    @Override
    public int deleteTronTransactionByIds(Long[] ids)
    {
        return tronTransactionMapper.deleteTronTransactionByIds(ids);
    }

    /**
     * 删除Trx充提信息
     * 
     * @param id Trx充提ID
     * @return 结果
     */
    @Override
    public int deleteTronTransactionById(Long id)
    {
        return tronTransactionMapper.deleteTronTransactionById(id);
    }

    @Override
    public TronTransaction findFirstByTxid(String txId) {
        return tronTransactionMapper.findFirstByTxid(txId);
    }

    @Override
    public List<TronTransaction> listTronTransaction(TronTransaction tronTransaction) {
        return tronTransactionMapper.listTronTransaction(tronTransaction);
    }

    @Override
    public AjaxResult collect(TronTransaction params) {
        Long rowId = params.getId();
        TronTransaction transaction = tronTransactionMapper.selectTronTransactionById(rowId);
        if(transaction != null){
            TronTransaction updateTron = new TronTransaction();
            updateTron.setId(transaction.getId());
            updateTron.setStatus(2);//待归集
            tronTransactionMapper.updateTronTransaction(transaction);
        }
        return AjaxResult.success();
    }

    @Override
    public List<TronTransaction> subordinateRecordList(TronTransaction tronEntity) {
        return tronTransactionMapper.listTronTransaction(tronEntity);
    }

    @Override
    @Transactional
    public void manualScoring(WalletAddress address,String fromAddress, String hash, Long blockNum, BigDecimal rechargeNum) {
        Transaction firstByTxid = transactionRepository.findFirstByTxid(hash);
        if(firstByTxid != null) throw new CustomException("订单记录已存在");
        Long uid = address.getUid();
        TronTransaction transaction = new TronTransaction();
        transaction.setTxid(hash);
        transaction.setToAddress(address.getAddress());
        transaction.setSymbol(CurrencyEnum.USDT.getCode());
//        transaction.setScore(1);
        transaction.setStatus(3);
        transaction.setFromAddress(fromAddress);
        transaction.setAmount(rechargeNum);
        transaction.setBlockNum(blockNum);
        transaction.setCreatedAt(new Date());
        transaction.setType(1);
        transaction.setOriginAmount(rechargeNum.multiply(new BigDecimal(1000000)).longValue());
        tronTransactionMapper.insertTronTransaction(transaction);
        AssetChange assetChange = AssetChange.builder()
                .amount(rechargeNum)
                .uid(uid)
                .dim(0).type(AssetLogTypeEnum.CASH_IN)
                .subType(AssetLogSubTypeEnum.CASH_IN_OUTSIDE)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .currency(CurrencyEnum.USDT.getCode())
                .build();
        iWalletService.changeAsset(assetChange);
        Withdraw inLog = new Withdraw();
        inLog.setCoin("USDT");
        inLog.setUid(uid);
        //内转-入
        inLog.setType(4);
        inLog.setFrom(fromAddress);
        inLog.setToAddress(address.getAddress());
        inLog.setMoney(rechargeNum);
        inLog.setFee(BigDecimal.ZERO);
        inLog.setStatus(1);
        inLog.setCheckStatus(1);
        inLog.setRemark("TRC20充值");
        inLog.setVerifyTime(new Date());
        withdrawMapper.insert(inLog);
    }
}
