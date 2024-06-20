package com.bigo.project.bigo.userinfo.service;

import com.bigo.project.bigo.userinfo.domain.LevelConfig;

import java.util.List;

/**
 * 用户等级Service接口
 * 
 * @author bigo
 * @date 2022-07-22
 */
public interface ILevelConfigService 
{
    /**
     * 查询用户等级
     * 
     * @param id 用户等级ID
     * @return 用户等级
     */
    public LevelConfig selectLevelConfigById(Long id);

    /**
     * 查询用户等级列表
     * 
     * @param levelConfig 用户等级
     * @return 用户等级集合
     */
    public List<LevelConfig> selectLevelConfigList(LevelConfig levelConfig);

    /**
     * 新增用户等级
     * 
     * @param levelConfig 用户等级
     * @return 结果
     */
    public int insertLevelConfig(LevelConfig levelConfig);

    /**
     * 修改用户等级
     * 
     * @param levelConfig 用户等级
     * @return 结果
     */
    public int updateLevelConfig(LevelConfig levelConfig);

    /**
     * 批量删除用户等级
     * 
     * @param ids 需要删除的用户等级ID
     * @return 结果
     */
    public int deleteLevelConfigByIds(Long[] ids);

    /**
     * 删除用户等级信息
     * 
     * @param id 用户等级ID
     * @return 结果
     */
    public int deleteLevelConfigById(Long id);
}
