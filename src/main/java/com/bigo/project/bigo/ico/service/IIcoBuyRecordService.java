package com.bigo.project.bigo.ico.service;

import java.util.List;
import com.bigo.project.bigo.ico.domain.IcoBuyRecord;

/**
 * 现货预售购买记录Service接口
 * 
 * @author bigo
 * @date 2023-05-11
 */
public interface IIcoBuyRecordService 
{
    /**
     * 查询现货预售购买记录
     * 
     * @param id 现货预售购买记录ID
     * @return 现货预售购买记录
     */
    public IcoBuyRecord selectIcoBuyRecordById(Long id);

    /**
     * 查询现货预售购买记录列表
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 现货预售购买记录集合
     */
    public List<IcoBuyRecord> selectIcoBuyRecordList(IcoBuyRecord icoBuyRecord);

    /**
     * 新增现货预售购买记录
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 结果
     */
    public int insertIcoBuyRecord(IcoBuyRecord icoBuyRecord);

    /**
     * 修改现货预售购买记录
     * 
     * @param icoBuyRecord 现货预售购买记录
     * @return 结果
     */
    public int updateIcoBuyRecord(IcoBuyRecord icoBuyRecord);

    /**
     * 批量删除现货预售购买记录
     * 
     * @param ids 需要删除的现货预售购买记录ID
     * @return 结果
     */
    public int deleteIcoBuyRecordByIds(Long[] ids);

    /**
     * 删除现货预售购买记录信息
     * 
     * @param id 现货预售购买记录ID
     * @return 结果
     */
    public int deleteIcoBuyRecordById(Long id);
}
