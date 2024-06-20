package com.bigo.project.bigo.ico.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.bigo.project.bigo.api.dto.IcoSpotDTO;
import com.bigo.project.bigo.api.vo.ico.IcoSpotVO;
import com.bigo.project.bigo.ico.domain.IcoSpot;
import com.bigo.project.bigo.userinfo.domain.BigoUser;

/**
 * 现货交易记录Service接口
 * 
 * @author bigo
 * @date 2023-03-14
 */
public interface IIcoSpotService 
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
     * 批量删除现货交易记录
     * 
     * @param ids 需要删除的现货交易记录ID
     * @return 结果
     */
    public int deleteIcoSpotByIds(Long[] ids);

    /**
     * 删除现货交易记录信息
     * 
     * @param id 现货交易记录ID
     * @return 结果
     */
    public int deleteIcoSpotById(Long id);

    void place(IcoSpotDTO dto, BigoUser user);

    void commissioningBuy(IcoSpot spot);

    void commissioningSell(IcoSpot spot);

    IcoSpot selectIcoSpot(IcoSpot icoSpot);

    void revokeOrder(IcoSpot spot);

    List<IcoSpotVO> selectIcoSpotVOList(IcoSpotVO param);

    Long sumTradeUserNum(Map params);

    List<IcoSpot> selectTradeUser();

    BigDecimal getTradeAmount(IcoSpot param);
}
