package com.bigo.project.bigo.pay.mapper;

import java.util.List;
import com.bigo.project.bigo.pay.domain.TransOrder;

/**
 * 代付Mapper接口
 * 
 * @author bigo
 * @date 2022-05-22
 */
public interface TransOrderMapper 
{
    /**
     * 查询代付
     * 
     * @param id 代付ID
     * @return 代付
     */
    public TransOrder selectTransOrderById(Long id);

    /**
     * 查询代付列表
     * 
     * @param transOrder 代付
     * @return 代付集合
     */
    public List<TransOrder> selectTransOrderList(TransOrder transOrder);

    /**
     * 新增代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    public int insertTransOrder(TransOrder transOrder);

    /**
     * 修改代付
     * 
     * @param transOrder 代付
     * @return 结果
     */
    public int updateTransOrder(TransOrder transOrder);

    /**
     * 删除代付
     * 
     * @param id 代付ID
     * @return 结果
     */
    public int deleteTransOrderById(Long id);

    /**
     * 批量删除代付
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTransOrderByIds(Long[] ids);

    TransOrder selectOrderId(String transOrderId);

    int updateStatus(TransOrder updateTransOrder);
}
