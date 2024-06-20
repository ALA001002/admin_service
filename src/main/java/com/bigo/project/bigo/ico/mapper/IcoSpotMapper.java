package com.bigo.project.bigo.ico.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.bigo.project.bigo.api.vo.ico.IcoSpotVO;
import com.bigo.project.bigo.ico.domain.IcoSpot;

/**
 * 现货交易记录Mapper接口
 * 
 * @author bigo
 * @date 2023-03-14
 */
public interface IcoSpotMapper 
{
    /**
     * 查询现货交易记录
     * 
     * @param id 现货交易记录ID
     * @return 现货交易记录
     */
    public IcoSpot selectIcoSpotById(Long id);

    /**
     * 查询现货交易记录列表
     * 
     * @param icoSpot 现货交易记录
     * @return 现货交易记录集合
     */
    public List<IcoSpot> selectIcoSpotList(IcoSpot icoSpot);

    /**
     * 新增现货交易记录
     * 
     * @param icoSpot 现货交易记录
     * @return 结果
     */
    public int insertIcoSpot(IcoSpot icoSpot);

    /**
     * 修改现货交易记录
     * 
     * @param icoSpot 现货交易记录
     * @return 结果
     */
    public int updateIcoSpot(IcoSpot icoSpot);

    /**
     * 删除现货交易记录
     * 
     * @param id 现货交易记录ID
     * @return 结果
     */
    public int deleteIcoSpotById(Long id);

    /**
     * 批量删除现货交易记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteIcoSpotByIds(Long[] ids);

    IcoSpot selectIcoSpot(IcoSpot icoSpot);

    List<IcoSpotVO> selectIcoSpotVOList(IcoSpotVO param);

    void revokeOrder(IcoSpot updateSpot);

    Long sumTradeUserNum(Map params);

    List<IcoSpot> selectTradeUser();

    BigDecimal getTradeAmount(IcoSpot param);
}
