package com.bigo.project.bigo.contract.service;

import com.bigo.project.bigo.api.domain.TimeContractBuyParam;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.entity.TimeContractEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 合约service
 * @Author wenxm
 * @Date 2020/6/21 17:28
 */
public interface ITimeContractService {

    /**
     * 生成合约
     * @param param
     * @return
     */
    Boolean generateContract(TimeContractBuyParam param, HttpServletRequest request);

    /**
     * 查询合约
     * @param param
     * @return
     */
    List<TimeContract> listContract(TimeContract param);

    /**
     * 根据id获取
     * @param uid
     * @param contractId
     * @return
     */
    TimeContract getByUIdAndContractId(Long uid, Long contractId);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    TimeContract getById(Long id);

    /**
     * 查询合约
     * @param entity
     * @return
     */
    List<TimeContractEntity> listByEntity(TimeContractEntity entity);

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
    void closeContract(TimeContract contract);

    /**
     * 开仓
     * @param contract
     */
    void openContract(TimeContract contract);

    /**
     * 计算合约
     * @param contract
     * @param curPrice
     */
    void calContract(TimeContract contract, BigDecimal curPrice);

    /**
     * 一键止损/止盈
     * @param contract
     * @param type
     */
    int oneKeyClose(TimeContract contract, int type);

    BigDecimal getVariablePrice(TimeContract contract);
}
