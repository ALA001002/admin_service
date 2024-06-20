package com.bigo.project.bigo.loans.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.api.dto.LoansDTO;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.loans.domain.LoansInfo;
import com.bigo.project.bigo.loans.domain.LoansThreshold;
import com.bigo.project.bigo.loans.mapper.LoansInfoMapper;
import com.bigo.project.bigo.loans.service.ILoansInfoService;
import com.bigo.project.bigo.loans.service.ILoansThresholdService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 借款信息Service业务层处理
 * 
 * @author bigo
 * @date 2022-01-12
 */
@Slf4j
@Service
public class LoansInfoServiceImpl implements ILoansInfoService 
{
    @Resource
    private LoansInfoMapper loansInfoMapper;

    @Autowired
    private ILoansThresholdService loansThresholdService;

    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private IWithdrawService withdrawService;

    /**
     * 查询借款信息
     * 
     * @param id 借款信息ID
     * @return 借款信息
     */
    @Override
    public LoansInfo selectLoansInfoById(Long id)
    {
        return loansInfoMapper.selectLoansInfoById(id);
    }



    /**
     * 查询借款信息列表
     * 
     * @param loansInfo 借款信息
     * @return 借款信息
     */
    @Override
    public List<LoansInfo> selectLoansInfoList(LoansInfo loansInfo)
    {
        return loansInfoMapper.selectLoansInfoList(loansInfo);
    }


    @Override
    public List<LoansInfo> selectLoansInfoByWithdrawList(LoansInfo loansInfo) {
        return loansInfoMapper.selectLoansInfoByWithdrawList(loansInfo);
    }

    @Override
    @Transactional
    public int cumulativeBalance(LoansInfo updateInfo) {
        return  loansInfoMapper.cumulativeBalance(updateInfo);
    }

    @Override
    public int cumulativeRechargeAmount(LoansInfo updateInfo) {
        return  loansInfoMapper.cumulativeRechargeAmount(updateInfo);
    }

    /**
     * 新增借款信息
     * 
     * @param loansInfo 借款信息
     * @return 结果
     */
    @Override
    public int insertLoansInfo(LoansInfo loansInfo)
    {
        loansInfo.setCreateTime(DateUtils.getNowDate());
        return loansInfoMapper.insertLoansInfo(loansInfo);
    }

    /**
     * 修改借款信息
     * 
     * @param loansInfo 借款信息
     * @return 结果
     */
    @Override
    public int updateLoansInfo(LoansInfo loansInfo)
    {
        return loansInfoMapper.updateLoansInfo(loansInfo);
    }

    /**
     * 批量删除借款信息
     * 
     * @param ids 需要删除的借款信息ID
     * @return 结果
     */
    @Override
    public int deleteLoansInfoByIds(Long[] ids)
    {
        return loansInfoMapper.deleteLoansInfoByIds(ids);
    }

    /**
     * 删除借款信息信息
     * 
     * @param id 借款信息ID
     * @return 结果
     */
    @Override
    public int deleteLoansInfoById(Long id)
    {
        return loansInfoMapper.deleteLoansInfoById(id);
    }

    /**
     * 用户借款申请
     * @param dto
     * @param user
     */
    @Override
    public void userLoans(LoansDTO dto, BigoUser user) {
        LoansThreshold threshold = loansThresholdService.selectLoansThresholdById(dto.getThresholdId());
        if(threshold == null) {
            // 没有获取到门槛提示
            log.info("========暂无获取到充值门槛========");
            throw new CustomException("operation_failed_please_try_again");
        }
        //判断借款金额是否达到最小金额
        if(dto.getLoansAmount().compareTo(new BigDecimal(threshold.getMinAmount())) < 0){
            log.info("========暂无获取到充值门槛========");
            throw new CustomException("illegal_loan_number");
        }

        BigDecimal interestAmount = BigDecimal.ZERO; //利息金额
        if(threshold.getInterest().compareTo(BigDecimal.ZERO) > 0) {
            interestAmount = dto.getLoansAmount().multiply(threshold.getInterest().divide(new BigDecimal(100))).multiply(new BigDecimal(dto.getLoansNumber()));
        }
        // 查询自己的累计充值金额
        BigDecimal rechargeAmount = withdrawService.getWithdraAmount(user.getUid(), CurrencyEnum.USDT.getCode(), 4, 1 , 1);
        if(dto.getLoansAmount().compareTo(rechargeAmount.multiply(threshold.getQuotaMultiplier())) > 0) {
            //如果借款数量超过限定额度,提示借款数量不合法
            throw new CustomException("illegal_loan_number");
        }
        //总金额
        BigDecimal totalAmount = interestAmount.add(dto.getLoansAmount());
        LoansInfo info = new LoansInfo();
        info.setUid(user.getUid());
        info.setAmount(dto.getLoansAmount());
        info.setInterestAmount(interestAmount);
        info.setTotalAmount(totalAmount);
        info.setLoansNumber(dto.getLoansNumber());
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, dto.getLoansNumber());// num为增加的天数，可以改变的
        info.setPaybackTime(ca.getTime());
        info.setStatus(1);
        info.setMinRepaymentRate(threshold.getMinRepaymentRate());
        info.setCreateTime(new Date());
        loansInfoMapper.insertLoansInfo(info);
    }

    @Override
    public LoansInfo selectLoansInfoByUid(Long uid) {
        return loansInfoMapper.selectLoansInfoByUid(uid);
    }

    @Override
    @Transactional
    public void checkLoansInfo(LoansInfo info, Integer status) {
        LoansInfo updateInfo = new LoansInfo();
        updateInfo.setId(info.getId());
        if(status == 2) {//审核不通过
            updateInfo.setStatus(2);
        }else if(status == 3) {//审核通过
            updateInfo.setStatus(3);
            //放款
            iWalletService.lockChange(info.getAmount(), CurrencyEnum.USDT.getCode(), info.getUid(), WalletTypeEnum.CAPITAL_ACCOUNT.getType(),
                    0, AssetLogTypeEnum.LOANS);
        }
        updateInfo.setCheckTime(new Date());
        updateInfo.setOperatorId(info.getOperatorId());
        loansInfoMapper.updateLoansInfo(updateInfo);
    }

    @Override
    public LoansInfo getCurrentLoans(LoansInfo paramsInfo) {
        return loansInfoMapper.getCurrentLoans(paramsInfo);
    }


}
