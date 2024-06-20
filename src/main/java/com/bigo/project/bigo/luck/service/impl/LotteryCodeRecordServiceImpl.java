package com.bigo.project.bigo.luck.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.luck.mapper.LotteryCodeRecordMapper;
import com.bigo.project.bigo.luck.domain.LotteryCodeRecord;
import com.bigo.project.bigo.luck.service.ILotteryCodeRecordService;

/**
 * 抽奖码使用记录Service业务层处理
 * 
 * @author bigo
 * @date 2021-03-29
 */
@Service
public class LotteryCodeRecordServiceImpl implements ILotteryCodeRecordService 
{
    @Autowired
    private LotteryCodeRecordMapper lotteryCodeRecordMapper;

    /**
     * 查询抽奖码使用记录
     * 
     * @param id 抽奖码使用记录ID
     * @return 抽奖码使用记录
     */
    @Override
    public LotteryCodeRecord selectLotteryCodeRecordById(Long id)
    {
        return lotteryCodeRecordMapper.selectLotteryCodeRecordById(id);
    }

    /**
     * 查询抽奖码使用记录列表
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 抽奖码使用记录
     */
    @Override
    public List<LotteryCodeRecord> selectLotteryCodeRecordList(LotteryCodeRecord lotteryCodeRecord)
    {
        return lotteryCodeRecordMapper.selectLotteryCodeRecordList(lotteryCodeRecord);
    }

    /**
     * 新增抽奖码使用记录
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 结果
     */
    @Override
    public int insertLotteryCodeRecord(LotteryCodeRecord lotteryCodeRecord)
    {
        lotteryCodeRecord.setCreateTime(DateUtils.getNowDate());
        return lotteryCodeRecordMapper.insertLotteryCodeRecord(lotteryCodeRecord);
    }

    /**
     * 修改抽奖码使用记录
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 结果
     */
    @Override
    public int updateLotteryCodeRecord(LotteryCodeRecord lotteryCodeRecord)
    {
        return lotteryCodeRecordMapper.updateLotteryCodeRecord(lotteryCodeRecord);
    }

    /**
     * 批量删除抽奖码使用记录
     * 
     * @param ids 需要删除的抽奖码使用记录ID
     * @return 结果
     */
    @Override
    public int deleteLotteryCodeRecordByIds(Long[] ids)
    {
        return lotteryCodeRecordMapper.deleteLotteryCodeRecordByIds(ids);
    }

    /**
     * 删除抽奖码使用记录信息
     * 
     * @param id 抽奖码使用记录ID
     * @return 结果
     */
    @Override
    public int deleteLotteryCodeRecordById(Long id)
    {
        return lotteryCodeRecordMapper.deleteLotteryCodeRecordById(id);
    }
}
