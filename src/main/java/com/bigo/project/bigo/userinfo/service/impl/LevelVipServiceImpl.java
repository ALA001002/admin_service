package com.bigo.project.bigo.userinfo.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.userinfo.domain.LevelVip;
import com.bigo.project.bigo.userinfo.mapper.LevelVipMapper;
import com.bigo.project.bigo.userinfo.service.ILevelVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * vip等级返佣Service业务层处理
 * 
 * @author bigo
 * @date 2023-11-25
 */
@Service
public class LevelVipServiceImpl implements ILevelVipService
{
    @Autowired
    private LevelVipMapper levelVipMapper;

    /**
     * 查询vip等级返佣
     * 
     * @param id vip等级返佣ID
     * @return vip等级返佣
     */
    @Override
    public LevelVip selectLevelVipById(Long id)
    {
        return levelVipMapper.selectLevelVipById(id);
    }

    /**
     * 查询vip等级返佣列表
     * 
     * @param levelVip vip等级返佣
     * @return vip等级返佣
     */
    @Override
    public List<LevelVip> selectLevelVipList(LevelVip levelVip)
    {
        return levelVipMapper.selectLevelVipList(levelVip);
    }

    /**
     * 新增vip等级返佣
     * 
     * @param levelVip vip等级返佣
     * @return 结果
     */
    @Override
    public int insertLevelVip(LevelVip levelVip)
    {
        levelVip.setCreateTime(DateUtils.getNowDate());
        return levelVipMapper.insertLevelVip(levelVip);
    }

    /**
     * 修改vip等级返佣
     * 
     * @param levelVip vip等级返佣
     * @return 结果
     */
    @Override
    public int updateLevelVip(LevelVip levelVip)
    {
        return levelVipMapper.updateLevelVip(levelVip);
    }

    /**
     * 批量删除vip等级返佣
     * 
     * @param ids 需要删除的vip等级返佣ID
     * @return 结果
     */
    @Override
    public int deleteLevelVipByIds(Long[] ids)
    {
        return levelVipMapper.deleteLevelVipByIds(ids);
    }

    /**
     * 删除vip等级返佣信息
     * 
     * @param id vip等级返佣ID
     * @return 结果
     */
    @Override
    public int deleteLevelVipById(Long id)
    {
        return levelVipMapper.deleteLevelVipById(id);
    }
}
