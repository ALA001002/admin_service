package com.bigo.project.bigo.contract.service;

import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.ContractStopParam;
import com.bigo.project.bigo.api.vo.ContractVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;
import com.bigo.project.bigo.contract.entity.ContractEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description 合约service
 * @Author wenxm
 * @Date 2020/6/21 17:28
 */
public interface IContractService {

    /**
     * 生成合约
     * @param param
     * @return
     */
    Boolean generateContract(ContractBuyParam param);

    /**
     * 查询合约列表
     * @param contract
     * @return
     */
    List<ContractVO> listContractVO(ContractQueryParam contract);

    /**
     * 手动平仓
     * @param uid
     * @param contractId
     * @return
     */
    Boolean closeContract(Long uid, Long contractId);

    /**
     * 根据计划委托购买合约
     * @param plan
     * @return
     */
    Boolean geneContractByPlan(ContractPlan plan, BigDecimal price);

    /**
     * 扫描和处理合约
     */
    void scanAndDealContract(Contract contract);

    /**
     * 更新止盈止损信息
     * @param contract
     * @return
     */
    Boolean updateStopInfo(Contract contract, ContractStopParam stopInfo);

    /**
     * 查询指定日期的用户订单
     * @param uidList
     * @param startTime
     * @param endTime
     * @return
     */
    List<Contract> listContract(List<Long> uidList, Date startTime, Date endTime);

    /**
     * 补仓
     * @param param
     * @return
     */
    Boolean replenishContract(ContractBuyParam param);

    /**
     * 查询合约
     * @param param
     * @return
     */
    List<Contract> listContract(Contract param);

    /**
     * 计算留仓费
     * @param contract
     * @param rate
     */
    void capitalContract(Contract contract, BigDecimal rate);

    /**
     * 根据id获取
     * @param uid
     * @param contractId
     * @return
     */
    Contract getByUIdAndContractId(Long uid, Long contractId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    Contract getById(Long id);

    /**
     * 查询合约
     * @param entity
     * @return
     */
    List<ContractEntity> listByEntity(ContractEntity entity);

    /**
     * 一键平仓
     * @param contract
     * @param operatorId 操作人id
     * @return
     */
    Boolean oneKeyClose(Contract contract, BigDecimal closePrice, Integer type, Long operatorId);

    /**
     * 获取用户购买合约的总手续费
     * @param uid
     * @param currency
     * @return
     */
    BigDecimal getTotalFeeByUid(Long uid, String currency);

    /**
     * 平仓
     * @param contract
     */
    void closeContract(Contract contract);

}
