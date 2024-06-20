package com.bigo.project.bigo.userinfo.service;

import com.bigo.project.bigo.userinfo.domain.LevelVip;

import java.util.List;

/**
 * vip等级返佣Service接口
 * 
 * @author bigo
 * @date 2023-11-25
 */
public interface ILevelVipService 
{
    /**
     * 查询vip等级返佣
     * 
     * @param id vip等级返佣ID
     * @return vip等级返佣
     */
    public LevelVip selectLevelVipById(Long id);

    /**
     * 查询vip等级返佣列表
     * 
     * @param levelVip vip等级返佣
     * @return vip等级返佣集合
     */
    public List<LevelVip> selectLevelVipList(LevelVip levelVip);

    /**
     * 新增vip等级返佣
     * 
     * @param levelVip vip等级返佣
     * @return 结果
     */
    public int insertLevelVip(LevelVip levelVip);

    /**
     * 修改vip等级返佣
     * 
     * @param levelVip vip等级返佣
     * @return 结果
     */
    public int updateLevelVip(LevelVip levelVip);

    /**
     * 批量删除vip等级返佣
     * 
     * @param ids 需要删除的vip等级返佣ID
     * @return 结果
     */
    public int deleteLevelVipByIds(Long[] ids);

    /**
     * 删除vip等级返佣信息
     * 
     * @param id vip等级返佣ID
     * @return 结果
     */
    public int deleteLevelVipById(Long id);
}
