package com.bigo.project.bigo.luck.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.luck.mapper.WinningRecordMapper;
import com.bigo.project.bigo.luck.domain.WinningRecord;
import com.bigo.project.bigo.luck.service.IWinningRecordService;

/**
 * 中奖记录Service业务层处理
 * 
 * @author bigo
 * @date 2020-12-31
 */
@Service
public class WinningRecordServiceImpl implements IWinningRecordService 
{
    @Autowired
    private WinningRecordMapper winningRecordMapper;

    /**
     * 查询中奖记录
     * 
     * @param id 中奖记录ID
     * @return 中奖记录
     */
    @Override
    public WinningRecord selectWinningRecordById(Long id)
    {
        return winningRecordMapper.selectWinningRecordById(id);
    }

    /**
     * 查询中奖记录列表
     * 
     * @param winningRecord 中奖记录
     * @return 中奖记录
     */
    @Override
    public List<WinningRecord> selectWinningRecordList(WinningRecord winningRecord)
    {
        return winningRecordMapper.selectWinningRecordList(winningRecord);
    }

    /**
     * 新增中奖记录
     * 
     * @param winningRecord 中奖记录
     * @return 结果
     */
    @Override
    public int insertWinningRecord(WinningRecord winningRecord)
    {
        winningRecord.setCreateTime(DateUtils.getNowDate());
        return winningRecordMapper.insertWinningRecord(winningRecord);
    }

    /**
     * 修改中奖记录
     * 
     * @param winningRecord 中奖记录
     * @return 结果
     */
    @Override
    public int updateWinningRecord(WinningRecord winningRecord)
    {
        return winningRecordMapper.updateWinningRecord(winningRecord);
    }

    /**
     * 批量删除中奖记录
     * 
     * @param ids 需要删除的中奖记录ID
     * @return 结果
     */
    @Override
    public int deleteWinningRecordByIds(Long[] ids)
    {
        return winningRecordMapper.deleteWinningRecordByIds(ids);
    }

    /**
     * 删除中奖记录信息
     * 
     * @param id 中奖记录ID
     * @return 结果
     */
    @Override
    public int deleteWinningRecordById(Long id)
    {
        return winningRecordMapper.deleteWinningRecordById(id);
    }
}
