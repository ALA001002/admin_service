package com.bigo.project.bigo.luck.service;

import java.util.List;
import com.bigo.project.bigo.luck.domain.LotteryCode;

/**
 * 抽奖码Service接口
 * 
 * @author bigo
 * @date 2020-12-30
 */
public interface ILotteryCodeService 
{
    /**
     * 查询抽奖码
     * 
     * @param id 抽奖码ID
     * @return 抽奖码
     */
    public LotteryCode selectLotteryCodeById(Long id);

    /**
     * 查询抽奖码列表
     * 
     * @param lotteryCode 抽奖码
     * @return 抽奖码集合
     */
    public List<LotteryCode> selectLotteryCodeList(LotteryCode lotteryCode);

    /**
     * 新增抽奖码
     * 
     * @param lotteryCode 抽奖码
     * @return 结果
     */
    public int insertLotteryCode(LotteryCode lotteryCode);

    /**
     * 修改抽奖码
     * 
     * @param lotteryCode 抽奖码
     * @return 结果
     */
    public int updateLotteryCode(LotteryCode lotteryCode);

    /**
     * 批量删除抽奖码
     * 
     * @param ids 需要删除的抽奖码ID
     * @return 结果
     */
    public int deleteLotteryCodeByIds(Long[] ids);

    /**
     * 删除抽奖码信息
     * 
     * @param id 抽奖码ID
     * @return 结果
     */
    public int deleteLotteryCodeById(Long id);

    LotteryCode getCode(LotteryCode lotteryCode);
}
