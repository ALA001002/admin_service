package com.bigo.project.bigo.luck.service;

import java.util.List;
import com.bigo.project.bigo.luck.domain.Jackpot;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

/**
 * 奖池Service接口
 * 
 * @author bigo
 * @date 2020-12-31
 */
public interface IJackpotService 
{
    /**
     * 查询奖池
     * 
     * @param id 奖池ID
     * @return 奖池
     */
    public Jackpot selectJackpotById(Long id);

    /**
     * 查询奖池列表
     * 
     * @param jackpot 奖池
     * @return 奖池集合
     */
    public List<Jackpot> selectJackpotList(Jackpot jackpot);

    /**
     * 新增奖池
     * 
     * @param jackpot 奖池
     * @return 结果
     */
    public int insertJackpot(Jackpot jackpot);

    /**
     * 修改奖池
     * 
     * @param jackpot 奖池
     * @return 结果
     */
    public int updateJackpot(Jackpot jackpot);

    /**
     * 批量删除奖池
     * 
     * @param ids 需要删除的奖池ID
     * @return 结果
     */
    public int deleteJackpotByIds(Long[] ids);

    /**
     * 删除奖池信息
     * 
     * @param id 奖池ID
     * @return 结果
     */
    public int deleteJackpotById(Long id);

    /**
     * 抽奖
     * @param lotteryCode
     * @param user
     * @return
     */
    Jackpot luckDraw(LotteryCode lotteryCode, BigoUser user);
}
