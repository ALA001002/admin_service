package com.bigo.project.bigo.wallet.jpaEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "view_tron_transaction_recharge")
public class ViewTronTransactionRecharge {
    private Integer id;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String txid;
    private String fromAddress;
    private String toAddress;
    private Byte status;
    private BigDecimal amount;
    private String symbol;
    private Byte type;
    private Long blockNum;
    private Timestamp confirmTime;
    private String feeTxId;
    private BigDecimal originAmount;
    private Timestamp collectTime;
    private String collectTxId;
    private String contractAddress;
    private String errors;
    private String collectErrors;
    private String parentTxId;
    private Long confirmBlock;
    private String email;
    private int uid;
    private Integer topUid;
    private Boolean score;
    @Id
    @Basic
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "created_at")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Basic
    @Column(name = "updated_at")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Basic
    @Column(name = "txid")
    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    @Basic
    @Column(name = "from_address")
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Basic
    @Column(name = "to_address")
    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    @Basic
    @Column(name = "status")
    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Basic
    @Column(name = "amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Basic
    @Column(name = "symbol")
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Basic
    @Column(name = "type")
    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Basic
    @Column(name = "block_num")
    public Long getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(Long blockNum) {
        this.blockNum = blockNum;
    }

    @Basic
    @Column(name = "confirm_time")
    public Timestamp getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Timestamp confirmTime) {
        this.confirmTime = confirmTime;
    }

    @Basic
    @Column(name = "fee_tx_id")
    public String getFeeTxId() {
        return feeTxId;
    }

    public void setFeeTxId(String feeTxId) {
        this.feeTxId = feeTxId;
    }

    @Basic
    @Column(name = "origin_amount")
    public BigDecimal getOriginAmount() {
        return originAmount;
    }

    public void setOriginAmount(BigDecimal originAmount) {
        this.originAmount = originAmount;
    }

    @Basic
    @Column(name = "collect_time")
    public Timestamp getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Timestamp collectTime) {
        this.collectTime = collectTime;
    }

    @Basic
    @Column(name = "collect_tx_id")
    public String getCollectTxId() {
        return collectTxId;
    }

    public void setCollectTxId(String collectTxId) {
        this.collectTxId = collectTxId;
    }

    @Basic
    @Column(name = "contract_address")
    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @Basic
    @Column(name = "errors")
    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    @Basic
    @Column(name = "collect_errors")
    public String getCollectErrors() {
        return collectErrors;
    }

    public void setCollectErrors(String collectErrors) {
        this.collectErrors = collectErrors;
    }

    @Basic
    @Column(name = "parent_tx_id")
    public String getParentTxId() {
        return parentTxId;
    }

    public void setParentTxId(String parentTxId) {
        this.parentTxId = parentTxId;
    }

    @Basic
    @Column(name = "confirm_block")
    public Long getConfirmBlock() {
        return confirmBlock;
    }

    public void setConfirmBlock(Long confirmBlock) {
        this.confirmBlock = confirmBlock;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "uid")
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "top_uid")
    public Integer getTopUid() {
        return topUid;
    }

    public void setTopUid(Integer topUid) {
        this.topUid = topUid;
    }

    @Basic
    @Column(name = "score")
    public Boolean getScore() {
        return score;
    }

    public void setScore(Boolean score) {
        this.score = score;
    }
}
