package com.bigo.project.bigo.ico.service.impl;

import java.util.List;
import com.bigo.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.ico.mapper.IcoBuyRecordMapper;
import com.bigo.project.bigo.ico.domain.IcoBuyRecord;
import com.bigo.project.bigo.ico.service.IIcoBuyRecordService;

/**
 * 现货预售购买记录Service业务层处理
 * 
 * @author bigo
 * @date 2023-05-11
 */
@Service
public class IcoBuyRecordServiceImpl implements IIcoBuyRecordService 
{
    @Autowired
    private IcoBuyRecordMapper icoBuyRecordMapper;

    /**
     * 查询现货预售购买记录
     * 
     * @param id 现货预售购买记录ID
     * @return 现货预售购买记录
     */
    @Override
    public IcoBuyRecord selectIcoBuyRecordById(Long id)
    {
        return icoBuyRecordMapper.selectIcoBuyRecordById(id);
    }

    /**
     * 查询现货预售购买记录列表
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 现货预售购买记录
     */
    @Override
    public List<IcoBuyRecord> selectIcoBuyRecordList(IcoBuyRecord icoBuyRecord)
    {
        return icoBuyRecordMapper.selectIcoBuyRecordList(icoBuyRecord);
    }

    /**
     * 新增现货预售购买记录
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 结果
     */
    @Override
    public int insertIcoBuyRecord(IcoBuyRecord icoBuyRecord)
    {
        icoBuyRecord.setCreateTime(DateUtils.getNowDate());
        return icoBuyRecordMapper.insertIcoBuyRecord(icoBuyRecord);
    }

    /**
     * 修改现货预售购买记录
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 结果
     */
    @Override
    public int updateIcoBuyRecord(IcoBuyRecord icoBuyRecord)
    {
        return icoBuyRecordMapper.updateIcoBuyRecord(icoBuyRecord);
    }

    /**
     * 批量删除现货预售购买记录
     * 
     * @param ids 需要删除的现货预售购买记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoBuyRecordByIds(Long[] ids)
    {
        return icoBuyRecordMapper.deleteIcoBuyRecordByIds(ids);
    }

    /**
     * 删除现货预售购买记录信息
     * 
     * @param id 现货预售购买记录ID
     * @return 结果
     */
    @Override
    public int deleteIcoBuyRecordById(Long id)
    {
        return icoBuyRecordMapper.deleteIcoBuyRecordById(id);
    }
}
