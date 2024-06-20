package com.bigo.project.bigo.wallet.mapper;

import com.bigo.project.bigo.wallet.domain.TronTransaction;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Trx充提Mapper接口
 * 
 * @author bigo
 * @date 2021-11-23
 */
public interface TronTransactionMapper 
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
     * 删除Trx充提
     * 
     * @param id Trx充提ID
     * @return 结果
     */
    public int deleteTronTransactionById(Long id);

    /**
     * 批量删除Trx充提
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTronTransactionByIds(Long[] ids);

    TronTransaction findFirstByTxid(@Param("txId") String txId);

    List<TronTransaction> listTronTransaction(TronTransaction tronTransaction);

    void updateScore(TronTransaction tronTransaction);
}
