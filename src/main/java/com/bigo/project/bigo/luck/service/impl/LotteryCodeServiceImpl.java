package com.bigo.project.bigo.luck.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.InviteCodeUtils;
import com.bigo.common.utils.LotteryCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.luck.mapper.LotteryCodeMapper;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;

/**
 * 抽奖码Service业务层处理
 * 
 * @author bigo
 * @date 2020-12-30
 */
@Service
public class LotteryCodeServiceImpl implements ILotteryCodeService 
{
    @Autowired
    private LotteryCodeMapper lotteryCodeMapper;

    /**
     * 查询抽奖码
     * 
     * @param id 抽奖码ID
     * @return 抽奖码
     */
    @Override
    public LotteryCode selectLotteryCodeById(Long id)
    {
        return lotteryCodeMapper.selectLotteryCodeById(id);
    }

    /**
     * 查询抽奖码列表
     * 
     * @param lotteryCode 抽奖码
     * @return 抽奖码
     */
    @Override
    public List<LotteryCode> selectLotteryCodeList(LotteryCode lotteryCode)
    {
        return lotteryCodeMapper.selectLotteryCodeList(lotteryCode);
    }

    /**
     * 新增抽奖码
     * 
     * @param lotteryCode 抽奖码
     * @return 结果
     */
    @Override
    public int insertLotteryCode(LotteryCode lotteryCode)
    {
        String code = getCode();
        lotteryCode.setLotteryCode(code);   // 随机生成抽奖码
        lotteryCode.setCreateTime(DateUtils.getNowDate());
        return lotteryCodeMapper.insertLotteryCode(lotteryCode);
    }

    private String getCode() {
        String code = LotteryCodeUtils.getLetteryCode();
        int size = this.countByLotteryCode(code);
        if(size > 0){
            return getCode();
        }
        return code;
    }

    public int countByLotteryCode(String code) {
        return lotteryCodeMapper.countByLotteryCode(code);
    }

    /**
     * 修改抽奖码
     * 
     * @param lotteryCode 抽奖码
     * @return 结果
     */
    @Override
    public int updateLotteryCode(LotteryCode lotteryCode)
    {
        return lotteryCodeMapper.updateLotteryCode(lotteryCode);
    }

    /**
     * 批量删除抽奖码
     * 
     * @param ids 需要删除的抽奖码ID
     * @return 结果
     */
    @Override
    public int deleteLotteryCodeByIds(Long[] ids)
    {
        return lotteryCodeMapper.deleteLotteryCodeByIds(ids);
    }

    /**
     * 删除抽奖码信息
     * 
     * @param id 抽奖码ID
     * @return 结果
     */
    @Override
    public int deleteLotteryCodeById(Long id)
    {
        return lotteryCodeMapper.deleteLotteryCodeById(id);
    }


    @Override
    public LotteryCode getCode(LotteryCode lotteryCode) {
        return lotteryCodeMapper.getCode(lotteryCode);
    }
}
