package com.bigo.project.bigo.pay.service;

import java.util.List;
import com.bigo.project.bigo.pay.domain.BankInfo;

/**
 * 银行信息Service接口
 * 
 * @author bigo
 * @date 2022-05-23
 */
public interface IBankInfoService 
{
    /**
     * 查询银行信息
     * 
     * @param id 银行信息ID
     * @return 银行信息
     */
    public BankInfo selectBankInfoById(Long id);

    /**
     * 查询银行信息列表
     * 
     * @param bankInfo 银行信息
     * @return 银行信息集合
     */
    public List<BankInfo> selectBankInfoList(BankInfo bankInfo);

    /**
     * 新增银行信息
     * 
     * @param bankInfo 银行信息
     * @return 结果
     */
    public int insertBankInfo(BankInfo bankInfo);

    /**
     * 修改银行信息
     * 
     * @param bankInfo 银行信息
     * @return 结果
     */
    public int updateBankInfo(BankInfo bankInfo);

    /**
     * 批量删除银行信息
     * 
     * @param ids 需要删除的银行信息ID
     * @return 结果
     */
    public int deleteBankInfoByIds(Long[] ids);

    /**
     * 删除银行信息信息
     * 
     * @param id 银行信息ID
     * @return 结果
     */
    public int deleteBankInfoById(Long id);
}
