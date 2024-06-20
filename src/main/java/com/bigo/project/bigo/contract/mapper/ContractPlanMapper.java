package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.vo.ContractPlanVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;

import java.util.List;

/**
 * @Description 合约mapper
 * @Author wenxm
 * @Date 2020/6/16 10:58
 */
public interface ContractPlanMapper {

    /**
     * 新增计划委托
     * @param plan
     * @return
     */
    Long insertContractPlan(ContractPlan plan);

    /**
     * 查询计划委托
     * @param param
     * @return
     */
    List<ContractPlanVO> listContractPlan(ContractQueryParam param);

    /**
     * 根据用户id和计划委托id获取计划委托信息
     * @param uid
     * @param planId
     * @return
     */
    ContractPlan getByUidAndPlanId(Long uid, Long planId);

    /**
     * 撤销计划委托
     * @param planId
     * @return
     */
    int revokeContractPlan(Long planId);

    /**
     * 获取委托中的合约
     * @return
     */
    List<ContractPlan> getPlaningContract();

    /**
     * 批量更新委托状态
     * @param list
     * @return
     */
    int updatePlanStatusForDeal(List<Long> list, Integer status);

    /**
     * 更新计划委托信息
     * @param plan
     * @return
     */
    int updatePlan(ContractPlan plan);


}
