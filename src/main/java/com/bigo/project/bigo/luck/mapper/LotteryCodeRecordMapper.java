package com.bigo.project.bigo.luck.mapper;

import java.util.List;
import com.bigo.project.bigo.luck.domain.LotteryCodeRecord;

/**
 * 抽奖码使用记录Mapper接口
 * 
 * @author bigo
 * @date 2021-03-29
 */
public interface LotteryCodeRecordMapper 
{
    /**
     * 查询抽奖码使用记录
     * 
     * @param id 抽奖码使用记录ID
     * @return 抽奖码使用记录
     */
    public LotteryCodeRecord selectLotteryCodeRecordById(Long id);

    /**
     * 查询抽奖码使用记录列表
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 抽奖码使用记录集合
     */
    public List<LotteryCodeRecord> selectLotteryCodeRecordList(LotteryCodeRecord lotteryCodeRecord);

    /**
     * 新增抽奖码使用记录
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 结果
     */
    public int insertLotteryCodeRecord(LotteryCodeRecord lotteryCodeRecord);

    /**
     * 修改抽奖码使用记录
     * 
     * @param lotteryCodeRecord 抽奖码使用记录
     * @return 结果
     */
    public int updateLotteryCodeRecord(LotteryCodeRecord lotteryCodeRecord);

    /**
     * 删除抽奖码使用记录
     * 
     * @param id 抽奖码使用记录ID
     * @return 结果
     */
    public int deleteLotteryCodeRecordById(Long id);

    /**
     * 批量删除抽奖码使用记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLotteryCodeRecordByIds(Long[] ids);
}
