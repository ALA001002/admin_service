package com.bigo.project.bigo.userinfo.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.userinfo.domain.LevelAward;
import com.bigo.project.bigo.userinfo.mapper.LevelAwardMapper;
import com.bigo.project.bigo.userinfo.service.ILevelAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 等级赠送奖励Service业务层处理
 * 
 * @author bigo
 * @date 2023-11-25
 */
@Service
public class LevelAwardServiceImpl implements ILevelAwardService
{
    @Autowired
    private LevelAwardMapper levelAwardMapper;

    /**
     * 查询等级赠送奖励
     * 
     * @param id 等级赠送奖励ID
     * @return 等级赠送奖励
     */
    @Override
    public LevelAward selectLevelAwardById(Long id)
    {
        return levelAwardMapper.selectLevelAwardById(id);
    }

    /**
     * 查询等级赠送奖励列表
     * 
     * @param levelAward 等级赠送奖励
     * @return 等级赠送奖励
     */
    @Override
    public List<LevelAward> selectLevelAwardList(LevelAward levelAward)
    {
        return levelAwardMapper.selectLevelAwardList(levelAward);
    }

    /**
     * 新增等级赠送奖励
     * 
     * @param levelAward 等级赠送奖励
     * @return 结果
     */
    @Override
    public int insertLevelAward(LevelAward levelAward)
    {
        levelAward.setCreateTime(DateUtils.getNowDate());
        return levelAwardMapper.insertLevelAward(levelAward);
    }

    /**
     * 修改等级赠送奖励
     * 
     * @param levelAward 等级赠送奖励
     * @return 结果
     */
    @Override
    public int updateLevelAward(LevelAward levelAward)
    {
        return levelAwardMapper.updateLevelAward(levelAward);
    }

    /**
     * 批量删除等级赠送奖励
     * 
     * @param ids 需要删除的等级赠送奖励ID
     * @return 结果
     */
    @Override
    public int deleteLevelAwardByIds(Long[] ids)
    {
        return levelAwardMapper.deleteLevelAwardByIds(ids);
    }

    /**
     * 删除等级赠送奖励信息
     * 
     * @param id 等级赠送奖励ID
     * @return 结果
     */
    @Override
    public int deleteLevelAwardById(Long id)
    {
        return levelAwardMapper.deleteLevelAwardById(id);
    }
}
