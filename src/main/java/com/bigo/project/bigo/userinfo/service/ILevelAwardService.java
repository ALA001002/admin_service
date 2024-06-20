package com.bigo.project.bigo.userinfo.service;

import com.bigo.project.bigo.userinfo.domain.LevelAward;

import java.util.List;

/**
 * 等级赠送奖励Service接口
 * 
 * @author bigo
 * @date 2023-11-25
 */
public interface ILevelAwardService 
{
    /**
     * 查询等级赠送奖励
     * 
     * @param id 等级赠送奖励ID
     * @return 等级赠送奖励
     */
    public LevelAward selectLevelAwardById(Long id);

    /**
     * 查询等级赠送奖励列表
     * 
     * @param levelAward 等级赠送奖励
     * @return 等级赠送奖励集合
     */
    public List<LevelAward> selectLevelAwardList(LevelAward levelAward);

    /**
     * 新增等级赠送奖励
     * 
     * @param levelAward 等级赠送奖励
     * @return 结果
     */
    public int insertLevelAward(LevelAward levelAward);

    /**
     * 修改等级赠送奖励
     * 
     * @param levelAward 等级赠送奖励
     * @return 结果
     */
    public int updateLevelAward(LevelAward levelAward);

    /**
     * 批量删除等级赠送奖励
     * 
     * @param ids 需要删除的等级赠送奖励ID
     * @return 结果
     */
    public int deleteLevelAwardByIds(Long[] ids);

    /**
     * 删除等级赠送奖励信息
     * 
     * @param id 等级赠送奖励ID
     * @return 结果
     */
    public int deleteLevelAwardById(Long id);
}
