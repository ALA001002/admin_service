package com.bigo.project.bigo.enums;

import com.bigo.common.utils.RedisUtils;
import com.bigo.common.utils.enums.DynamicEnumUtil;
import com.bigo.project.bigo.ico.domain.SymbolCoin;

import java.util.*;

/**
 * @Description 交易对枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum SymbolCoinEnum {
    BTCUSDT("btcusdt","BTC/USDT", 1, "btc"),
    /**
     * 以太坊/USDT
     */
    ETHUSDT("ethusdt","ETH/USDT",1, "eth"),
    /**
     * BNB/USDT
     */
    BNBUSDT("bnbusdt","BNB/USDT",0, "bnb"),

    /**
     * DOT/USDT
     */
    DOTUSDT("dotusdt","DOT/USDT",0, "dot"),

    /**
     * HT/USDT
     */
    HTUSDT("htusdt","HT/USDT",0, "ht"),
    /**
     * DOGE/USDT
     */
    DOGEUSDT("dogeusdt","DOGE/USDT",0, "doge"),
    /**
     * XRP/USDT
     */
    XRPUSDT("xrpusdt","XRP/USDT",0, "xrp"),
    /**
     * LINK/USDT
     */
    LINKUSDT("linkusdt","LINK/USDT",0, "link"),
    /**
     * 比特币现金/USDT
     */
    BCHUSDT("bchusdt","BCH/USDT",0, "bch"),
    /**
     * LTC/USDT
     */
    LTCUSDT("ltcusdt","LTC/USDT",0, "ltc"),
    /**
     * BSV/USDT
     */
    BSVUSDT("bsvusdt","BSV/USDT",0, "bsv"),
    /**
     * ADA/USDT
     */
    ADAUSDT("adausdt","ADA/USDT",0, "ada"),
    /**
     * EOS/USDT
     */
    EOSUSDT("eosusdt","EOS/USDT",0, "eos"),
    /**
     * TRX/USDT
     */
    TRXUSDT("trxusdt","TRX/USDT",0, "trx"),

    /**
     * IOTA/USDT
     */
    IOTAUSDT("iotausdt","IOTA/USDT",0, "iota"),
    /**
     * USDJ/USDT
     */
   /* USDJUSDT("usdjusdt","USDJ/USDT",0, "usdj"),
    *//**
     * MAGIC/USDT
     *//*
    MAGICUSDT("magicusdt","MAGIC/USDT",0, "magic"),
    *//**
     * MATCH/USDT
     *//*
    MATCHUSDT("matchusdt","MATCH/USDT",0, "match"),
    *//**
     * TON/USDT
     *//*
    TONUSDT("tonusdt","TON/USDT",0, "ton"),
    *//**
     * FITFI/USDT
     *//*
    FITFIUSDT("fitfiusdt","FITFI/USDT",0, "fitfi"),
    *//**
     * APT/USDT
     *//*
    APTUSDT("aptusdt","APT/USDT",0, "apt"),
    *//**
     * WWY/USDT
     *//*
    WWYUSDT("wwyusdt","WWY/USDT",0, "wwy"),
    *//**
     *
     * MINE/USDT
     *//*
    MINEUSDT("mineusdt","MINE/USDT",0, "mine"),
    *//**
     * WLKN/USDT
     *//*
    WLKNUSDT("wlknusdt","WLKN/USDT",0, "wlkn"),
    *//**
     * BLD/USDT
     *//*
    BLDUSDT("bldusdt","BLD/USDT",0, "bld"),
    *//**
     * EVMOS/USDT
     *//*
    EVMOSUSDT("evmosusdt","EVMOS/USDT",0, "evmos"),
    *//**
     * ING/USDT
     *//*
    INGUSDT("ingusdt","ING/USDT",0, "ing"),
    *//**
     * APE/USDT
     *//*
    APEUSDT("apeusdt","APE/USDT",0, "ape"),*/
    ;
    /**
     * 交易对代码
     */
    private String code;
    /**
     * 交易对名称
     */
    private String name;
    /**
     * 是否支持限合约 0-否 1-是
     */
    private Integer supTimeContract;

    private String coin;

    SymbolCoinEnum(String code, String name, Integer supTimeContract, String coin){
        this.code = code;
        this.name = name;
        this.supTimeContract = supTimeContract;
        this.coin = coin;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getSupTimeContract() {
        return supTimeContract;
    }

    public String getCoin() {
        return coin;
    }

    private static Map<String, SymbolCoinEnum> enumMap = new HashMap<>();


    static{
        //  可以在这里加载枚举的配置文件 比如从 properties  数据库中加载
        //  加载完后 使用DynamicEnumUtil.addEnum 动态增加枚举值
        //  然后正常使用枚举即可
        List<SymbolCoin> voList = RedisUtils.getCacheList("symbol_coin_list") == null ? new ArrayList<>() : RedisUtils.getCacheList("symbol_coin_list");
        for (SymbolCoin vo : voList) {
            addTestEnum(vo.getCode().toUpperCase(), vo.getCode(), vo.getName(), 0, vo.getEnumName().toLowerCase());
        }

        EnumSet<SymbolCoinEnum> set = EnumSet.allOf(SymbolCoinEnum.class);
        for (SymbolCoinEnum each: set ) {
            // 增加一个缓存 减少对枚举的修改
            enumMap.put(each.code, each);
        }
    }

    // 根据关键字段获取枚举值  可以在这里做一些修改 来达到动态添加的效果
    public SymbolCoinEnum getEnum(String value){
        // 这里可以做一些修改  比如若从 enumMap 中没有取得 则加载配置动态添加
        return enumMap.get(value);
    }


    /**
     *
     * @param enumName 枚举名
     * @param code 枚举项1
     * @param name 枚举项2
     */
    public static void addTestEnum(String enumName, String code, String name, Integer supTimeContract, String coin){
        DynamicEnumUtil.addEnum(SymbolCoinEnum.class, enumName, new Class[]{String.class, String.class, Integer.class, String.class}, new Object[]{code, name,supTimeContract,coin});
    }


    /**
     * 根据编码获取交易对名称
     * @param code
     * @return
     */
    public static String getNameByCode(String code){
        for(SymbolCoinEnum symbol : SymbolCoinEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.name;
            }
        }
        return null;
    }

    public static String getCoinByCode(String code){
        for(SymbolCoinEnum symbol : SymbolCoinEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.coin;
            }
        }
        return null;
    }

    /**
     * 判断交易对是否支持限时合约
     * @param code
     * @return
     */
    public static Boolean isSupTimeContract(String code){
        for(SymbolCoinEnum symbol : SymbolCoinEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.supTimeContract == 1;
            }
        }
        return false;
    }

}
