package com.bigo.project.bigo.loans.mapper;

import com.bigo.project.bigo.loans.domain.LoansInfo;

import java.util.List;

/**
 * 借款信息Mapper接口
 * 
 * @author bigo
 * @date 2022-01-12
 */
public interface LoansInfoMapper 
{
    /**
     * 查询借款信息
     * 
     * @param id 借款信息ID
     * @return 借款信息
     */
    public LoansInfo selectLoansInfoById(Long id);

    /**
     * 查询借款信息列表
     * 
     * @param loansInfo 借款信息
     * @return 借款信息集合
     */
    public List<LoansInfo> selectLoansInfoList(LoansInfo loansInfo);

    /**
     * 新增借款信息
     * 
     * @param loansInfo 借款信息
     * @return 结果
     */
    public int insertLoansInfo(LoansInfo loansInfo);

    /**
     * 修改借款信息
     * 
     * @param loansInfo 借款信息
     * @return 结果
     */
    public int updateLoansInfo(LoansInfo loansInfo);

    /**
     * 删除借款信息
     * 
     * @param id 借款信息ID
     * @return 结果
     */
    public int deleteLoansInfoById(Long id);

    /**
     * 批量删除借款信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLoansInfoByIds(Long[] ids);

    LoansInfo selectLoansInfoByUid(Long uid);

    LoansInfo getCurrentLoans(LoansInfo paramsInfo);

    List<LoansInfo> selectLoansInfoByWithdrawList(LoansInfo loansInfo);

    int cumulativeBalance(LoansInfo updateInfo);

    int cumulativeRechargeAmount(LoansInfo updateInfo);
}
