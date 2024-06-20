package com.bigo.project.bigo.contract.service;

import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.vo.ContractPlanVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;

import java.util.List;

/**
 * @Description 计划委托service
 * @Author wenxm
 * @Date 2020/6/21 17:28
 */
public interface IContractPlanService {

    /**
     * 计划委托
     * @param param
     * @return
     */
    Boolean generateContractPlan(ContractBuyParam param);

    /**
     * 查询计划委托列表
     * @param param
     * @return
     */
    List<ContractPlanVO> listContractPlan(ContractQueryParam param);

    /**
     * 撤销委托
     * @param uid 用户id
     * @param planId 委托id
     * @return
     */
    Boolean revokeContractPlan(Long uid, Long planId);

    /**
     * 处理正在委托中的任务
     * @param plan
     */
    void dealPlaningContract(ContractPlan plan);

    /**
     * 获取委托中的合约
     * @return
     */
    List<ContractPlan> getPlaningContract();
}
