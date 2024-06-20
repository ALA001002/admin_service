package com.bigo.project.bigo.wallet.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.wallet.mapper.RechargeRewardMapper;
import com.bigo.project.bigo.wallet.domain.RechargeReward;
import com.bigo.project.bigo.wallet.service.IRechargeRewardService;

/**
 * 充值奖励Service业务层处理
 * 
 * @author xx
 * @date 2023-11-08
 */
@Service
public class RechargeRewardServiceImpl implements IRechargeRewardService 
{
    @Autowired
    private RechargeRewardMapper rechargeRewardMapper;

    /**
     * 查询充值奖励
     * 
     * @param id 充值奖励ID
     * @return 充值奖励
     */
    @Override
    public RechargeReward selectRechargeRewardById(Long id)
    {
        return rechargeRewardMapper.selectRechargeRewardById(id);
    }

    /**
     * 查询充值奖励列表
     * 
     * @param rechargeReward 充值奖励
     * @return 充值奖励
     */
    @Override
    public List<RechargeReward> selectRechargeRewardList(RechargeReward rechargeReward)
    {
        return rechargeRewardMapper.selectRechargeRewardList(rechargeReward);
    }

    /**
     * 新增充值奖励
     * 
     * @param rechargeReward 充值奖励
     * @return 结果
     */
    @Override
    public int insertRechargeReward(RechargeReward rechargeReward)
    {
        rechargeReward.setCreateTime(DateUtils.getNowDate());
        return rechargeRewardMapper.insertRechargeReward(rechargeReward);
    }

    /**
     * 修改充值奖励
     * 
     * @param rechargeReward 充值奖励
     * @return 结果
     */
    @Override
    public int updateRechargeReward(RechargeReward rechargeReward)
    {
        return rechargeRewardMapper.updateRechargeReward(rechargeReward);
    }

    /**
     * 批量删除充值奖励
     * 
     * @param ids 需要删除的充值奖励ID
     * @return 结果
     */
    @Override
    public int deleteRechargeRewardByIds(Long[] ids)
    {
        return rechargeRewardMapper.deleteRechargeRewardByIds(ids);
    }

    /**
     * 删除充值奖励信息
     * 
     * @param id 充值奖励ID
     * @return 结果
     */
    @Override
    public int deleteRechargeRewardById(Long id)
    {
        return rechargeRewardMapper.deleteRechargeRewardById(id);
    }
}
