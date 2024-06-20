package com.bigo.project.bigo.loans.service;

import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.loans.domain.LoansThreshold;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

import java.util.List;

/**
 * 借款门槛Service接口
 * 
 * @author bigo
 * @date 2022-01-12
 */
public interface ILoansThresholdService 
{
    /**
     * 查询借款门槛
     * 
     * @param id 借款门槛ID
     * @return 借款门槛
     */
    public LoansThreshold selectLoansThresholdById(Long id);

    /**
     * 查询借款门槛列表
     * 
     * @param loansThreshold 借款门槛
     * @return 借款门槛集合
     */
    public List<LoansThreshold> selectLoansThresholdList(LoansThreshold loansThreshold);

    /**
     * 新增借款门槛
     * 
     * @param loansThreshold 借款门槛
     * @return 结果
     */
    public int insertLoansThreshold(LoansThreshold loansThreshold);

    /**
     * 修改借款门槛
     * 
     * @param loansThreshold 借款门槛
     * @return 结果
     */
    public int updateLoansThreshold(LoansThreshold loansThreshold);

    /**
     * 批量删除借款门槛
     * 
     * @param ids 需要删除的借款门槛ID
     * @return 结果
     */
    public int deleteLoansThresholdByIds(Long[] ids);

    /**
     * 删除借款门槛信息
     * 
     * @param id 借款门槛ID
     * @return 结果
     */
    public int deleteLoansThresholdById(Long id);

    AjaxResult getUserLoansInfo(BigoUser user);
}
