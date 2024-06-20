package com.bigo.project.bigo.wallet.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.wallet.mapper.ToAddressInfoMapper;
import com.bigo.project.bigo.wallet.domain.ToAddressInfo;
import com.bigo.project.bigo.wallet.service.IToAddressInfoService;

/**
 * 收币地址Service业务层处理
 * 
 * @author bigo
 * @date 2022-04-28
 */
@Service
public class ToAddressInfoServiceImpl implements IToAddressInfoService 
{
    @Autowired
    private ToAddressInfoMapper toAddressInfoMapper;

    /**
     * 查询收币地址
     * 
     * @param id 收币地址ID
     * @return 收币地址
     */
    @Override
    public ToAddressInfo selectToAddressInfoById(Long id)
    {
        return toAddressInfoMapper.selectToAddressInfoById(id);
    }

    /**
     * 查询收币地址列表
     * 
     * @param toAddressInfo 收币地址
     * @return 收币地址
     */
    @Override
    public List<ToAddressInfo> selectToAddressInfoList(ToAddressInfo toAddressInfo)
    {
        return toAddressInfoMapper.selectToAddressInfoList(toAddressInfo);
    }

    /**
     * 新增收币地址
     * 
     * @param toAddressInfo 收币地址
     * @return 结果
     */
    @Override
    public int insertToAddressInfo(ToAddressInfo toAddressInfo)
    {
        toAddressInfo.setCreateTime(DateUtils.getNowDate());
        return toAddressInfoMapper.insertToAddressInfo(toAddressInfo);
    }

    /**
     * 修改收币地址
     * 
     * @param toAddressInfo 收币地址
     * @return 结果
     */
    @Override
    public int updateToAddressInfo(ToAddressInfo toAddressInfo)
    {
        return toAddressInfoMapper.updateToAddressInfo(toAddressInfo);
    }

    /**
     * 批量删除收币地址
     * 
     * @param ids 需要删除的收币地址ID
     * @return 结果
     */
    @Override
    public int deleteToAddressInfoByIds(Long[] ids)
    {
        return toAddressInfoMapper.deleteToAddressInfoByIds(ids);
    }

    /**
     * 删除收币地址信息
     * 
     * @param id 收币地址ID
     * @return 结果
     */
    @Override
    public int deleteToAddressInfoById(Long id)
    {
        return toAddressInfoMapper.deleteToAddressInfoById(id);
    }
}
