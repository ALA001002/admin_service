package com.bigo.project.bigo.ico.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.ico.mapper.IcoProductRecordMapper;
import com.bigo.project.bigo.ico.domain.IcoProductRecord;
import com.bigo.project.bigo.ico.service.IIcoProductRecordService;

/**
 * ico产品购买记录Service业务层处理
 * 
 * @author bigo
 * @date 2023-01-09
 */
@Service
public class IcoProductRecordServiceImpl implements IIcoProductRecordService 
{
    @Autowired
    private IcoProductRecordMapper icoProductRecordMapper;

    /**
     * 查询ico产品购买记录
     * 
     * @param id ico产品购买记录ID
     * @return ico产品购买记录
     */
    @Override
    public IcoProductRecord selectIcoProductRecordById(Long id)
    {
        return icoProductRecordMapper.selectIcoProductRecordById(id);
    }

    /**
     * 查询ico产品购买记录列表
     * 
     * @param icoProductRecord ico产品购买记录
     * @return ico产品购买记录
     */
    @Override
    public List<IcoProductRecord> selectIcoProductRecordList(IcoProductRecord icoProductRecord)
    {
        return icoProductRecordMapper.selectIcoProductRecordList(icoProductRecord);
    }

    /**
     * 新增ico产品购买记录
     * 
     * @param icoProductRecord ico产品购买记录
     * @return 结果
     */
    @Override
    public int insertIcoProductRecord(IcoProductRecord icoProductRecord)
    {
        icoProductRecord.setCreateTime(DateUtils.getNowDate());
        return icoProductRecordMapper.insertIcoProductRecord(icoProductRecord);
    }

    /**
     * 修改ico产品购买记录
     * 
     * @param icoProductRecord ico产品购买记录
     * @return 结果
     */
    @Override
    public int updateIcoProductRecord(IcoProductRecord icoProductRecord)
    {
        return icoProductRecordMapper.updateIcoProductRecord(icoProductRecord);
    }

    /**
     * 批量删除ico产品购买记录
     * 
     * @param ids 需要删除的ico产品购买记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoProductRecordByIds(Long[] ids)
    {
        return icoProductRecordMapper.deleteIcoProductRecordByIds(ids);
    }

    /**
     * 删除ico产品购买记录信息
     * 
     * @param id ico产品购买记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoProductRecordById(Long id)
    {
        return icoProductRecordMapper.deleteIcoProductRecordById(id);
    }
}
