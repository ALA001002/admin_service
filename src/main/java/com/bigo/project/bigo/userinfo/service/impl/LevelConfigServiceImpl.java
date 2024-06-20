package com.bigo.project.bigo.userinfo.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.userinfo.domain.LevelConfig;
import com.bigo.project.bigo.userinfo.mapper.LevelConfigMapper;
import com.bigo.project.bigo.userinfo.service.ILevelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户等级Service业务层处理
 * 
 * @author bigo
 * @date 2022-07-22
 */
@Service
public class LevelConfigServiceImpl implements ILevelConfigService 
{
    @Autowired
    private LevelConfigMapper levelConfigMapper;

    /**
     * 查询用户等级
     * 
     * @param id 用户等级ID
     * @return 用户等级
     */
    @Override
    public LevelConfig selectLevelConfigById(Long id)
    {
        return levelConfigMapper.selectLevelConfigById(id);
    }

    /**
     * 查询用户等级列表
     * 
     * @param levelConfig 用户等级
     * @return 用户等级
     */
    @Override
    public List<LevelConfig> selectLevelConfigList(LevelConfig levelConfig)
    {
        return levelConfigMapper.selectLevelConfigList(levelConfig);
    }

    /**
     * 新增用户等级
     * 
     * @param levelConfig 用户等级
     * @return 结果
     */
    @Override
    public int insertLevelConfig(LevelConfig levelConfig)
    {
        levelConfig.setCreateTime(DateUtils.getNowDate());
        return levelConfigMapper.insertLevelConfig(levelConfig);
    }

    /**
     * 修改用户等级
     * 
     * @param levelConfig 用户等级
     * @return 结果
     */
    @Override
    public int updateLevelConfig(LevelConfig levelConfig)
    {
        levelConfig.setUpdateTime(DateUtils.getNowDate());
        return levelConfigMapper.updateLevelConfig(levelConfig);
    }

    /**
     * 批量删除用户等级
     * 
     * @param ids 需要删除的用户等级ID
     * @return 结果
     */
    @Override
    public int deleteLevelConfigByIds(Long[] ids)
    {
        return levelConfigMapper.deleteLevelConfigByIds(ids);
    }

    /**
     * 删除用户等级信息
     * 
     * @param id 用户等级ID
     * @return 结果
     */
    @Override
    public int deleteLevelConfigById(Long id)
    {
        return levelConfigMapper.deleteLevelConfigById(id);
    }
}
