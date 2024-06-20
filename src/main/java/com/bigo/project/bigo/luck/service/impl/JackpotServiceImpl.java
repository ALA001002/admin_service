package com.bigo.project.bigo.luck.service.impl;

import java.util.Date;
import java.util.List;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.LotteryUtil;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.domain.LotteryCodeRecord;
import com.bigo.project.bigo.luck.domain.WinningRecord;
import com.bigo.project.bigo.luck.service.ILotteryCodeRecordService;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;
import com.bigo.project.bigo.luck.service.IWinningRecordService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.luck.mapper.JackpotMapper;
import com.bigo.project.bigo.luck.domain.Jackpot;
import com.bigo.project.bigo.luck.service.IJackpotService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 奖池Service业务层处理
 * 
 * @author bigo
 * @date 2020-12-31
 */
@Service
public class JackpotServiceImpl implements IJackpotService {

    @Resource
    private JackpotMapper jackpotMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWinningRecordService winningRecordService;

    @Autowired
    private ILotteryCodeService lotteryCodeService;

    @Autowired
    private ILotteryCodeRecordService lotteryCodeRecordService;

    /**
     * 查询奖池
     * 
     * @param id 奖池ID
     * @return 奖池
     */
    @Override
    public Jackpot selectJackpotById(Long id)
    {
        return jackpotMapper.selectJackpotById(id);
    }

    /**
     * 查询奖池列表
     * 
     * @param jackpot 奖池
     * @return 奖池
     */
    @Override
    public List<Jackpot> selectJackpotList(Jackpot jackpot)
    {
        return jackpotMapper.selectJackpotList(jackpot);
    }

    /**
     * 新增奖池
     * 
     * @param jackpot 奖池
     * @return 结果
     */
    @Override
    public int insertJackpot(Jackpot jackpot)
    {
        jackpot.setCreateTime(DateUtils.getNowDate());
        return jackpotMapper.insertJackpot(jackpot);
    }

    /**
     * 修改奖池
     * 
     * @param jackpot 奖池
     * @return 结果
     */
    @Override
    public int updateJackpot(Jackpot jackpot)
    {
        return jackpotMapper.updateJackpot(jackpot);
    }

    /**
     * 批量删除奖池
     * 
     * @param ids 需要删除的奖池ID
     * @return 结果
     */
    @Override
    public int deleteJackpotByIds(Long[] ids)
    {
        return jackpotMapper.deleteJackpotByIds(ids);
    }

    /**
     * 删除奖池信息
     * 
     * @param id 奖池ID
     * @return 结果
     */
    @Override
    public int deleteJackpotById(Long id)
    {
        return jackpotMapper.deleteJackpotById(id);
    }

    /**
     * 抽奖
     * @param lotteryCode
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Jackpot luckDraw(LotteryCode lotteryCode, BigoUser user) {

        // 获取中奖对象信息
        List<Jackpot> list = this.selectJackpotList(new Jackpot());
        LotteryUtil ll = new LotteryUtil(list);
        int index = ll.randomColunmIndex();
        Jackpot jackpot = list.get(index);
        // 增加冻结金额
        AssetChange change = AssetChange.builder().uid(user.getUid())
                .currency(jackpot.getCoin().toUpperCase())
                .dim(0)
                .type(AssetLogTypeEnum.LUCKY_DRAW)
                .subType(AssetLogSubTypeEnum.FROZEN)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(jackpot.getNum())
                .amountType(AmountTypeEnum.FROZEN.getType())
                .build();
        walletService.changeAsset(change);
        // 增加中奖记录
        WinningRecord record = new WinningRecord();
        record.setUid(user.getUid());
        record.setCoin(jackpot.getCoin());
        record.setNum(jackpot.getNum());
        record.setCreateTime(new Date());
        record.setLotteryCode(lotteryCode.getLotteryCode());
        winningRecordService.insertWinningRecord(record);
        // 修改抽奖码状态
        lotteryCode.setStatus(2);
        lotteryCode.setUpdateTime(new Date());
        lotteryCodeService.updateLotteryCode(lotteryCode);
        // 增加抽奖码使用记录
        LotteryCodeRecord codeRecord = new LotteryCodeRecord();
        codeRecord.setLotteryCode(lotteryCode.getLotteryCode());
        codeRecord.setUid(user.getUid());
        codeRecord.setCreateTime(new Date());
        lotteryCodeRecordService.insertLotteryCodeRecord(codeRecord);
        return jackpot;
    }
}
