package com.bigo.project.bigo.loans.service;

import com.bigo.project.bigo.api.dto.LoansDTO;
import com.bigo.project.bigo.loans.domain.LoansInfo;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

import java.util.List;

/**
 * 借款信息Service接口
 * 
 * @author bigo
 * @date 2022-01-12
 */
public interface ILoansInfoService 
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
     * 批量删除借款信息
     * 
     * @param ids 需要删除的借款信息ID
     * @return 结果
     */
    public int deleteLoansInfoByIds(Long[] ids);

    /**
     * 删除借款信息信息
     * 
     * @param id 借款信息ID
     * @return 结果
     */
    public int deleteLoansInfoById(Long id);

    /**
     * 用户借款申请
     * @param dto
     * @param user
     */
    void userLoans(LoansDTO dto, BigoUser user);

    /**
     * //查询当前用户当前是否有借款
     * @param uid
     * @return
     */
    LoansInfo selectLoansInfoByUid(Long uid);

    /**
     * 审核
     * @param info
     * @param status
     */
    void checkLoansInfo(LoansInfo info, Integer status);

    LoansInfo getCurrentLoans(LoansInfo paramsInfo);

    List<LoansInfo> selectLoansInfoByWithdrawList(LoansInfo loansInfo);


    int cumulativeBalance(LoansInfo updateInfo);

    int cumulativeRechargeAmount(LoansInfo updateInfo);
}
