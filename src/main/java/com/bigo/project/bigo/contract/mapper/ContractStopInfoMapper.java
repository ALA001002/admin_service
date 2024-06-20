package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.api.domain.ContractStopParam;

/**
 * @description: 合约止盈止损设置信息mapper
 * @author: wenxm
 * @date: 2020/7/28 14:38
 */
public interface ContractStopInfoMapper {
    /**
     * 新增
     * @param param
     * @return
     */
    int insert(ContractStopParam param);

    /**
     * 修改
     * @param param
     * @return
     */
    int update(ContractStopParam param);

    /**
     * 根据合约ID删除止盈止损参数
     * @param contractId
     * @return
     */
    int deleteByContractId(Long contractId);

    /**
     * 根据合约ID获取止盈止损设置信息
     * @param contractId
     * @return
     */
    ContractStopParam getByContractId(Long contractId);

    /**
     * 根据计划委托ID获取止盈止损设置信息
     * @param contractPlanId
     * @return
     */
    ContractStopParam getByContractPlanId(Long contractPlanId);

}
