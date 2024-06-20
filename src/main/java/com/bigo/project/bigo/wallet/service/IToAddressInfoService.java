package com.bigo.project.bigo.wallet.service;

import java.util.List;
import com.bigo.project.bigo.wallet.domain.ToAddressInfo;

/**
 * 收币地址Service接口
 * 
 * @author bigo
 * @date 2022-04-28
 */
public interface IToAddressInfoService 
{
    /**
     * 查询收币地址
     * 
     * @param id 收币地址ID
     * @return 收币地址
     */
    public ToAddressInfo selectToAddressInfoById(Long id);

    /**
     * 查询收币地址列表
     * 
     * @param toAddressInfo 收币地址
     * @return 收币地址集合
     */
    public List<ToAddressInfo> selectToAddressInfoList(ToAddressInfo toAddressInfo);

    /**
     * 新增收币地址
     * 
     * @param toAddressInfo 收币地址
     * @return 结果
     */
    public int insertToAddressInfo(ToAddressInfo toAddressInfo);

    /**
     * 修改收币地址
     * 
     * @param toAddressInfo 收币地址
     * @return 结果
     */
    public int updateToAddressInfo(ToAddressInfo toAddressInfo);

    /**
     * 批量删除收币地址
     * 
     * @param ids 需要删除的收币地址ID
     * @return 结果
     */
    public int deleteToAddressInfoByIds(Long[] ids);

    /**
     * 删除收币地址信息
     * 
     * @param id 收币地址ID
     * @return 结果
     */
    public int deleteToAddressInfoById(Long id);
}
