package com.bigo.project.bigo.luck.mapper;

import java.util.List;
import com.bigo.project.bigo.luck.domain.LotteryCode;

/**
 * 抽奖码Mapper接口
 * 
 * @author bigo
 * @date 2020-12-30
 */
public interface LotteryCodeMapper 
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
     * 删除抽奖码
     * 
     * @param id 抽奖码ID
     * @return 结果
     */
    public int deleteLotteryCodeById(Long id);

    /**
     * 批量删除抽奖码
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLotteryCodeByIds(Long[] ids);

    int countByLotteryCode(String code);

    LotteryCode getCode(LotteryCode lotteryCode);
}
