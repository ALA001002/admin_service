package com.bigo.project.bigo.luck.service;

import java.util.List;
import com.bigo.project.bigo.luck.domain.WinningRecord;

/**
 * 中奖记录Service接口
 * 
 * @author bigo
 * @date 2020-12-31
 */
public interface IWinningRecordService 
{
    /**
     * 查询中奖记录
     * 
     * @param id 中奖记录ID
     * @return 中奖记录
     */
    public WinningRecord selectWinningRecordById(Long id);

    /**
     * 查询中奖记录列表
     * 
     * @param winningRecord 中奖记录
     * @return 中奖记录集合
     */
    public List<WinningRecord> selectWinningRecordList(WinningRecord winningRecord);

    /**
     * 新增中奖记录
     * 
     * @param winningRecord 中奖记录
     * @return 结果
     */
    public int insertWinningRecord(WinningRecord winningRecord);

    /**
     * 修改中奖记录
     * 
     * @param winningRecord 中奖记录
     * @return 结果
     */
    public int updateWinningRecord(WinningRecord winningRecord);

    /**
     * 批量删除中奖记录
     * 
     * @param ids 需要删除的中奖记录ID
     * @return 结果
     */
    public int deleteWinningRecordByIds(Long[] ids);

    /**
     * 删除中奖记录信息
     * 
     * @param id 中奖记录ID
     * @return 结果
     */
    public int deleteWinningRecordById(Long id);
}
