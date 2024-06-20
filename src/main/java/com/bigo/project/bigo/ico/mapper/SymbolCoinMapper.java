package com.bigo.project.bigo.ico.mapper;

import java.util.List;
import com.bigo.project.bigo.ico.domain.SymbolCoin;

/**
 * 发币Mapper接口
 * 
 * @author bigo
 * @date 2023-01-06
 */
public interface SymbolCoinMapper 
{
    /**
     * 查询发币
     * 
     * @param id 发币ID
     * @return 发币
     */
    public SymbolCoin selectSymbolCoinById(Long id);

    /**
     * 查询发币列表
     * 
     * @param symbolCoin 发币
     * @return 发币集合
     */
    public List<SymbolCoin> selectSymbolCoinList(SymbolCoin symbolCoin);

    /**
     * 新增发币
     * 
     * @param symbolCoin 发币
     * @return 结果
     */
    public int insertSymbolCoin(SymbolCoin symbolCoin);

    /**
     * 修改发币
     * 
     * @param symbolCoin 发币
     * @return 结果
     */
    public int updateSymbolCoin(SymbolCoin symbolCoin);

    /**
     * 删除发币
     * 
     * @param id 发币ID
     * @return 结果
     */
    public int deleteSymbolCoinById(Long id);

    /**
     * 批量删除发币
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSymbolCoinByIds(Long[] ids);
}
