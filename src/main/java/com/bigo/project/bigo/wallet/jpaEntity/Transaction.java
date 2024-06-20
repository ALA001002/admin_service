package com.bigo.project.bigo.wallet.jpaEntity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * null
 *
 * <p>Date: Mon Oct 11 22:09:09 CST 2021</p>
 */

@Table(name ="tron_transaction")
@Entity
@Data
public class Transaction implements Serializable {


    private static final long serialVersionUID =  3398954683467389267L;

    /**
     * null
     */
    @Column(name = "id" )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * null
     */
    @Column(name = "created_at" )
    private java.time.LocalDateTime createdAt;

    /**
     * null
     */
    @Column(name = "updated_at" )
    private java.time.LocalDateTime updatedAt;

    /**
     * null
     */
    @Column(name = "txid" )
    private String txid;

    /**
     * null
     */
    @Column(name = "from_address" )
    private String fromAddress;

    /**
     * null
     */
    @Column(name = "to_address" )
    private String toAddress;

    /**
     * status 0 待转手续费 1表示转账手续费中 2表示手续费成功 3表示手续费失败 4表示归集成功 5表示归集失败 6 手续费转账交易
     */
    @Column(name = "status" )
    private Integer status;

    /**
     * null
     */
    @Column(name = "amount" )
    private Double amount;

    /**
     * null
     */
    @Column(name = "symbol" )
    private String symbol;

    /**
     * null
     */
    @Column(name = "type" )
    private Integer type;

    /**
     * null
     */
    @Column(name = "block_num" )
    private Integer blockNum;

    /**
     * null
     */
    @Column(name = "confirm_time" )
    private java.time.LocalDateTime confirmTime;

    /**
     * null
     */
    @Column(name = "fee_tx_id" )
    private String feeTxId;

    /**
     * null
     */
    @Column(name = "origin_amount" )
    private Double originAmount;

    /**
     * null
     */
    @Column(name = "collect_time" )
    private java.time.LocalDateTime collectTime;

    /**
     * null
     */
    @Column(name = "collect_tx_id" )
    private String collectTxId;

    /**
     * null
     */
    @Column(name = "contract_address" )
    private String contractAddress;

    /**
     * null
     */
    @Column(name = "errors" )
    private String errors;

    /**
     * null
     */
    @Column(name = "collect_errors" )
    private String collectErrors;

    /**
     * null
     */
    @Column(name = "parent_tx_id" )
    private String parentTxId;

    /**
     * null
     */
    @Column(name = "confirm_block" )
    private Integer confirmBlock;

    @Column(name="score")
    private Boolean score;


}
