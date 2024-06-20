package com.bigo.project.bigo.wallet.service;

import java.util.List;
import com.bigo.project.bigo.wallet.domain.RechargeReward;

/**
 * 充值奖励Service接口
 * 
 * @author xx
 * @date 2023-11-08
 */
public interface IRechargeRewardService 
{
    /**
     * 查询充值奖励
     * 
     * @param id 充值奖励ID
     * @return 充值奖励
     */
    public RechargeReward selectRechargeRewardById(Long id);

    /**
     * 查询充值奖励列表
     * 
     * @param rechargeReward 充值奖励
     * @return 充值奖励集合
     */
    public List<RechargeReward> selectRechargeRewardList(RechargeReward rechargeReward);

    /**
     * 新增充值奖励
     * 
     * @param rechargeReward 充值奖励
     * @return 结果
     */
    public int insertRechargeReward(RechargeReward rechargeReward);

    /**
     * 修改充值奖励
     * 
     * @param rechargeReward 充值奖励
     * @return 结果
     */
    public int updateRechargeReward(RechargeReward rechargeReward);

    /**
     * 批量删除充值奖励
     * 
     * @param ids 需要删除的充值奖励ID
     * @return 结果
     */
    public int deleteRechargeRewardByIds(Long[] ids);

    /**
     * 删除充值奖励信息
     * 
     * @param id 充值奖励ID
     * @return 结果
     */
    public int deleteRechargeRewardById(Long id);
}
