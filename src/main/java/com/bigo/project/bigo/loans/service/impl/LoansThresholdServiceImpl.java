package com.bigo.project.bigo.loans.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.vo.LoansThresholdVO;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.loans.domain.LoansThreshold;
import com.bigo.project.bigo.loans.mapper.LoansThresholdMapper;
import com.bigo.project.bigo.loans.service.ILoansThresholdService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.system.domain.SysDictData;
import com.bigo.project.system.service.ISysDictTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 借款门槛Service业务层处理
 * 
 * @author bigo
 * @date 2022-01-12
 */
@Slf4j
@Service
public class LoansThresholdServiceImpl implements ILoansThresholdService {

    @Autowired
    private IWithdrawService withdrawService;

    @Resource
    private LoansThresholdMapper loansThresholdMapper;

    @Autowired
    private ISysDictTypeService dictTypeService;



    /**
     * 查询借款门槛
     * 
     * @param id 借款门槛ID
     * @return 借款门槛
     */
    @Override
    public LoansThreshold selectLoansThresholdById(Long id)
    {
        return loansThresholdMapper.selectLoansThresholdById(id);
    }

    /**
     * 查询借款门槛列表
     * 
     * @param loansThreshold 借款门槛
     * @return 借款门槛
     */
    @Override
    public List<LoansThreshold> selectLoansThresholdList(LoansThreshold loansThreshold)
    {
        return loansThresholdMapper.selectLoansThresholdList(loansThreshold);
    }

    /**
     * 新增借款门槛
     * 
     * @param loansThreshold 借款门槛
     * @return 结果
     */
    @Override
    public int insertLoansThreshold(LoansThreshold loansThreshold)
    {
        loansThreshold.setCreateTime(DateUtils.getNowDate());
        return loansThresholdMapper.insertLoansThreshold(loansThreshold);
    }

    /**
     * 修改借款门槛
     * 
     * @param loansThreshold 借款门槛
     * @return 结果
     */
    @Override
    public int updateLoansThreshold(LoansThreshold loansThreshold)
    {
        loansThreshold.setUpdateTime(DateUtils.getNowDate());
        return loansThresholdMapper.updateLoansThreshold(loansThreshold);
    }

    /**
     * 批量删除借款门槛
     * 
     * @param ids 需要删除的借款门槛ID
     * @return 结果
     */
    @Override
    public int deleteLoansThresholdByIds(Long[] ids)
    {
        return loansThresholdMapper.deleteLoansThresholdByIds(ids);
    }

    /**
     * 删除借款门槛信息
     * 
     * @param id 借款门槛ID
     * @return 结果
     */
    @Override
    public int deleteLoansThresholdById(Long id)
    {
        return loansThresholdMapper.deleteLoansThresholdById(id);
    }

    /**
     * 获取用户借贷门槛
     * @param user
     * @return
     */
    @Override
    public AjaxResult getUserLoansInfo(BigoUser user) {
        List<LoansThreshold> loansThresholdList = selectLoansThresholdList(new LoansThreshold());
        if(loansThresholdList == null || loansThresholdList.size() <= 0) {
            return AjaxResult.error("not_eligible_to_borrow");
        }
//        loansThresholdList.sort((x, y) -> Long.compare(x.getRechargeAmount(), y.getRechargeAmount()));
        Collections.sort(loansThresholdList, new Comparator<LoansThreshold>() {
            @Override
            public int compare(LoansThreshold o1, LoansThreshold o2) {
                return o2.getRechargeAmount().compareTo(o1.getRechargeAmount());
            }
        });
        // 查询自己的累计充值金额
        BigDecimal rechargeAmount = withdrawService.getWithdraAmount(user.getUid(), CurrencyEnum.USDT.getCode(), 4, 1 , 1);
        LoansThreshold threshold = null;
        for (LoansThreshold param : loansThresholdList) {
            if(rechargeAmount.compareTo(new BigDecimal(param.getRechargeAmount())) >= 0) {
                threshold = param;
                break;
            }
        }
        if(threshold == null) {
            // 没有获取到门槛提示
            log.info("========暂无获取到充值门槛========");
            return AjaxResult.error("not_eligible_to_borrow");
        }
//        threshold = loansThresholdList.get(0);
        //获取借款期限
        List<SysDictData> dataList= dictTypeService.selectDictDataByType("bigo_loans_number");
        List<Integer> numberList = new ArrayList<>();
        for (SysDictData sysDictData : dataList) {
            numberList.add(Integer.valueOf(sysDictData.getDictValue()));
        }
        Collections.sort(numberList);
        LoansThresholdVO vo = new LoansThresholdVO();
        vo.setUid(user.getUid());
        vo.setThresholdId(threshold.getId());
        vo.setMinAmount(threshold.getMinAmount());
        vo.setMaxAmount(rechargeAmount.multiply(threshold.getQuotaMultiplier()));
        vo.setNumberList(numberList);
        vo.setMinRepaymentRate(threshold.getMinRepaymentRate());
        vo.setInterest(threshold.getInterest());
        return AjaxResult.success(vo);
    }
}
