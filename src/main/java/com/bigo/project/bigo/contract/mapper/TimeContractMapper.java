package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.entity.TimeContractEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 限时合约mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface TimeContractMapper {

    /**
     * 新增合约
     * @param contract
     * @return
     */
    Long insertContract(TimeContract contract);

    /**
     * 查询合约
     * @param param
     * @return
     */
    List<TimeContract> listContract(TimeContract param);

    /**
     * 根据用户id和合约id获取合约
     * @param param
     * @return
     */
    TimeContract getByUidAndContractId(ContractQueryParam param);

    /**
     * 平仓
     * @param contract
     * @return
     */
    int closeContract(TimeContract contract);

    /**
     * 查询合约
     * @param entity
     * @return
     */
    List<TimeContractEntity> listByEntity(TimeContractEntity entity);

    /**
     * 根据id获取
     * @param id
     * @return
     */
    TimeContract getById(Long id);

    /**
     * 获取用户购买合约的总手续费
     * @param uid
     * @param currency
     * @return
     */
    BigDecimal getTotalFeeByUid(@Param("uid") Long uid, @Param("currency") String currency);

    /**
     * 获取用户持仓中合约的总金额
     * @param uid
     * @param currency
     * @return
     */
    BigDecimal getTotalAmountByUid(@Param("uid") Long uid, @Param("currency") String currency);


    int updateSettType(TimeContract timeContract);
}
