package com.bigo.project.bigo.wallet.service;

import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.domain.WalletAddress;

import java.math.BigDecimal;
import java.util.List;

/**
 * Trx充提Service接口
 * 
 * @author bigo
 * @date 2021-11-23
 */
public interface ITronTransactionService 
{
    /**
     * 查询Trx充提
     * 
     * @param id Trx充提ID
     * @return Trx充提
     */
    public TronTransaction selectTronTransactionById(Long id);

    /**
     * 查询Trx充提列表
     * 
     * @param tronTransaction Trx充提
     * @return Trx充提集合
     */
    public List<TronTransaction> selectTronTransactionList(TronTransaction tronTransaction);

    /**
     * 新增Trx充提
     * 
     * @param tronTransaction Trx充提
     * @return 结果
     */
    public int insertTronTransaction(TronTransaction tronTransaction);

    /**
     * 修改Trx充提
     * 
     * @param tronTransaction Trx充提
     * @return 结果
     */
    public int updateTronTransaction(TronTransaction tronTransaction);

    /**
     * 批量删除Trx充提
     * 
     * @param ids 需要删除的Trx充提ID
     * @return 结果
     */
    public int deleteTronTransactionByIds(Long[] ids);

    /**
     * 删除Trx充提信息
     * 
     * @param id Trx充提ID
     * @return 结果
     */
    public int deleteTronTransactionById(Long id);

    TronTransaction findFirstByTxid(String txId);

    List<TronTransaction> listTronTransaction(TronTransaction tronTransaction);

    AjaxResult collect(TronTransaction tronTransaction);

    List<TronTransaction> subordinateRecordList(TronTransaction tronEntity);

    void manualScoring(WalletAddress address,String fromAddress, String hash, Long blockNum, BigDecimal rechargeNum);
}
