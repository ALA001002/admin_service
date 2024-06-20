package com.bigo.project.bigo.ico.mapper;

import java.util.List;
import com.bigo.project.bigo.ico.domain.IcoProductRecord;

/**
 * ico产品购买记录Mapper接口
 * 
 * @author bigo
 * @date 2023-01-09
 */
public interface IcoProductRecordMapper 
{
    /**
     * 查询ico产品购买记录
     * 
     * @param id ico产品购买记录ID
     * @return ico产品购买记录
     */
    public IcoProductRecord selectIcoProductRecordById(Long id);

    /**
     * 查询ico产品购买记录列表
     * 
     * @param icoProductRecord ico产品购买记录
     * @return ico产品购买记录集合
     */
    public List<IcoProductRecord> selectIcoProductRecordList(IcoProductRecord icoProductRecord);

    /**
     * 新增ico产品购买记录
     * 
     * @param icoProductRecord ico产品购买记录
     * @return 结果
     */
    public int insertIcoProductRecord(IcoProductRecord icoProductRecord);

    /**
     * 修改ico产品购买记录
     * 
     * @param icoProductRecord ico产品购买记录
     * @return 结果
     */
    public int updateIcoProductRecord(IcoProductRecord icoProductRecord);

    /**
     * 删除ico产品购买记录
     * 
     * @param id ico产品购买记录ID
     * @return 结果
     */
    public int deleteIcoProductRecordById(Long id);

    /**
     * 批量删除ico产品购买记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteIcoProductRecordByIds(Long[] ids);
}
