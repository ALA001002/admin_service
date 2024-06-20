package com.bigo.project.bigo.pay.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.pay.mapper.BankInfoMapper;
import com.bigo.project.bigo.pay.domain.BankInfo;
import com.bigo.project.bigo.pay.service.IBankInfoService;

/**
 * 银行信息Service业务层处理
 * 
 * @author bigo
 * @date 2022-05-23
 */
@Service
public class BankInfoServiceImpl implements IBankInfoService 
{
    @Autowired
    private BankInfoMapper bankInfoMapper;

    /**
     * 查询银行信息
     * 
     * @param id 银行信息ID
     * @return 银行信息
     */
    @Override
    public BankInfo selectBankInfoById(Long id)
    {
        return bankInfoMapper.selectBankInfoById(id);
    }

    /**
     * 查询银行信息列表
     * 
     * @param bankInfo 银行信息
     * @return 银行信息
     */
    @Override
    public List<BankInfo> selectBankInfoList(BankInfo bankInfo)
    {
        return bankInfoMapper.selectBankInfoList(bankInfo);
    }

    /**
     * 新增银行信息
     * 
     * @param bankInfo 银行信息
     * @return 结果
     */
    @Override
    public int insertBankInfo(BankInfo bankInfo)
    {
        return bankInfoMapper.insertBankInfo(bankInfo);
    }

    /**
     * 修改银行信息
     * 
     * @param bankInfo 银行信息
     * @return 结果
     */
    @Override
    public int updateBankInfo(BankInfo bankInfo)
    {
        return bankInfoMapper.updateBankInfo(bankInfo);
    }

    /**
     * 批量删除银行信息
     * 
     * @param ids 需要删除的银行信息ID
     * @return 结果
     */
    @Override
    public int deleteBankInfoByIds(Long[] ids)
    {
        return bankInfoMapper.deleteBankInfoByIds(ids);
    }

    /**
     * 删除银行信息信息
     * 
     * @param id 银行信息ID
     * @return 结果
     */
    @Override
    public int deleteBankInfoById(Long id)
    {
        return bankInfoMapper.deleteBankInfoById(id);
    }
}
