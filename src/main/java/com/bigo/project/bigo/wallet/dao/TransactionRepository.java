package com.bigo.project.bigo.wallet.dao;


import com.bigo.project.bigo.wallet.jpaEntity.Transaction;

/**
 * null
 *
 * <p>Date: Mon Oct 11 22:09:09 CST 2021</p>
 */
public interface TransactionRepository extends BaseRepository<Transaction> {

    Transaction findFirstByTxid(String txId);

}
