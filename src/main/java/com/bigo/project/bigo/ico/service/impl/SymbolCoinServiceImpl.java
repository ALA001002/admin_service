package com.bigo.project.bigo.ico.service.impl;

import java.util.List;

import com.bigo.common.utils.RedisUtils;
import com.bigo.project.bigo.enums.SymbolEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bigo.project.bigo.ico.mapper.SymbolCoinMapper;
import com.bigo.project.bigo.ico.domain.SymbolCoin;
import com.bigo.project.bigo.ico.service.ISymbolCoinService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 发币Service业务层处理
 * 
 * @author bigo
 * @date 2023-01-06
 */
@Service
public class SymbolCoinServiceImpl implements ISymbolCoinService 
{
    @Autowired
    private SymbolCoinMapper symbolCoinMapper;

    /**
     * 查询发币
     * 
     * @param id 发币ID
     * @return 发币
     */
    @Override
    public SymbolCoin selectSymbolCoinById(Long id)
    {
        return symbolCoinMapper.selectSymbolCoinById(id);
    }

    /**
     * 查询发币列表
     * 
     * @param symbolCoin 发币
     * @return 发币
     */
    @Override
    public List<SymbolCoin> selectSymbolCoinList(SymbolCoin symbolCoin)
    {
        return symbolCoinMapper.selectSymbolCoinList(symbolCoin);
    }

    /**
     * 新增发币
     * 
     * @param symbolCoin 发币
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSymbolCoin(SymbolCoin symbolCoin) {
        String enumName = symbolCoin.getEnumName();
        String code = enumName+"usdt";
        String name = enumName + "/usdt";
        symbolCoin.setEnumName(enumName.toUpperCase());
        symbolCoin.setCode(code.toLowerCase());
        symbolCoin.setName(name.toUpperCase());
        int result = symbolCoinMapper.insertSymbolCoin(symbolCoin);
        List<SymbolCoin> coinList = RedisUtils.getCacheList("symbol_coin_list");

        if(coinList != null && coinList.size() > 0) {
            for (int i=0; i<coinList.size(); i++) {
                SymbolCoin coin = coinList.get(i);
                if(coin.getCode().equals(symbolCoin.getCode())) {
                    coinList.remove(i);
                    RedisUtils.deleteObject("symbol_coin_list");
                    break;
                }
            }
        }
        coinList.add(symbolCoin);
        RedisUtils.setCacheList("symbol_coin_list", coinList);
        return result;
    }

    /**
     * 修改发币
     * 
     * @param symbolCoin 发币
     * @return 结果
     */
    @Override
    public int updateSymbolCoin(SymbolCoin symbolCoin) {
        String enumName = symbolCoin.getEnumName();
        String code = enumName+"usdt";
        String name = enumName + "/usdt";
        symbolCoin.setEnumName(enumName.toUpperCase());
        symbolCoin.setCode(code.toLowerCase());
        symbolCoin.setName(name.toUpperCase());
        int status = symbolCoinMapper.updateSymbolCoin(symbolCoin);
//        List<SymbolCoin> coinList = RedisUtils.getCacheList("symbol_coin_list");
//        for (int i=0; i<coinList.size(); i++) {
//            SymbolCoin coin = coinList.get(i);
//            if(coin.getId() == symbolCoin.getId()) {
//                coinList.remove(i);
//                break;
//            }
//        }
        List<SymbolCoin> coinList = symbolCoinMapper.selectSymbolCoinList(new SymbolCoin());
        RedisUtils.deleteObject("symbol_coin_list");
//        coinList.add(symbolCoin);
        RedisUtils.setCacheList("symbol_coin_list", coinList);
        return status;
    }

    /**
     * 批量删除发币
     * 
     * @param ids 需要删除的发币ID
     * @return 结果
     */
    @Override
    public int deleteSymbolCoinByIds(Long[] ids) {
        int status =symbolCoinMapper.deleteSymbolCoinByIds(ids);
        List<SymbolCoin> coinList = symbolCoinMapper.selectSymbolCoinList(new SymbolCoin());
//        List<SymbolCoin> coinList = RedisUtils.getCacheList("symbol_coin_list");
//        for (Long id : ids) {
//            for (int i=0; i<coinList.size(); i++) {
//                SymbolCoin coin = coinList.get(i);
//                if(coin.getId() == id) {
//                    coinList.remove(i);
//                    break;
//                }
//            }
//        }
        RedisUtils.deleteObject("symbol_coin_list");
        RedisUtils.setCacheList("symbol_coin_list", coinList);
        return status;
    }

    public static void main(String[] args) {
        Long a = 22L;
        Long b = 22L;
        if(a==b) {
            System.out.println(112233);
        }else {
            System.out.println(334455);
        }
    }

    /**
     * 删除发币信息
     * 
     * @param id 发币ID
     * @return 结果
     */
    @Override
    public int deleteSymbolCoinById(Long id) {
        int status = symbolCoinMapper.deleteSymbolCoinById(id);
        List<SymbolCoin> coinList = RedisUtils.getCacheList("symbol_coin_list");
        for (int i=0; i<coinList.size(); i++) {
            SymbolCoin coin = coinList.get(i);
            if(coin.getId() == id) {
                coinList.remove(i);
                break;
            }
        }
        RedisUtils.deleteObject("symbol_coin_list");
        RedisUtils.setCacheList("symbol_coin_list", coinList);
        return status;
    }

}
