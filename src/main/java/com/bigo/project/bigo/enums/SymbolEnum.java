package com.bigo.project.bigo.enums;

import com.bigo.common.utils.RedisUtils;
import com.bigo.common.utils.enums.DynamicEnumUtil;
import com.bigo.project.bigo.ico.domain.SymbolCoin;
import com.bigo.project.bigo.ico.enums.SpotTypeEnum;

import java.util.*;

/**
 * @Description 交易对枚举
 * @Author wenxm
 * @Date 2020/6/20 13:28
 */
public enum SymbolEnum {
    BTCUSDT("btcusdt","BTC/USDT", 1,"btc"),
    /**
     * 以太坊/USDT
     */
    ETHUSDT("ethusdt","ETH/USDT",1,"eth"),

    /**
     * HT/USDT
     */
    HTUSDT("htusdt","HT/USDT",0,"ht"),

    /**
     * BNB/USDT
     */
    BNBUSDT("bnbusdt","BNB/USDT",0,"bnb"),


    /**
     * XRP/USDT
     */
    XRPUSDT("xrpusdt","XRP/USDT",0,"xrp"),

    /**
     * LINK/USDT
     */
    LINKUSDT("linkusdt","LINK/USDT",0,"link"),

    /**
     * 比特币现金/USDT
     */
    BCHUSDT("bchusdt","BCH/USDT",0,"bch"),


    /**
     * LTC/USDT
     */
    LTCUSDT("ltcusdt","LTC/USDT",0,"ltc"),


    /**
     * BSV/USDT
     */
    BSVUSDT("bsvusdt","BSV/USDT",0,"bsv"),


    /**
     * ADA/USDT
     */
    ADAUSDT("adausdt","ADA/USDT",0,"ada"),


    /**
     * EOS/USDT
     */
    EOSUSDT("eosusdt","EOS/USDT",0,"eos"),


    /**
     * TRX/USDT
     */
    TRXUSDT("trxusdt","TRX/USDT",0,"trx"),


    /**
     * DOT/USDT
     */
    DOTUSDT("dotusdt","DOT/USDT",0,"dot"),



    /**
     * DOGE/USDT
     */
    DOGEUSDT("dogeusdt","DOGE/USDT",0,"doge"),

    /**
     * IOTA/USDT
     */
    IOTAUSDT("iotausdt","IOTA/USDT",0,"iota"),


    /**
     * XMR/USDT
     */
    XMRUSDT("xmrusdt","XMR/USDT",0,"xmr"),

    /**
     * NEXT/USDT
     */
    NEXTUSDT("nextusdt","NEXT/USDT",0,"next"),

    /**
     * TON/USDT
     */
    TONUSDT("tonusdt","TON/USDT",0,"ton"),
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

    SymbolEnum(String code, String name, Integer supTimeContract, String coin){
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

    private static Map<String, SymbolEnum> enumMap = new HashMap<>();


    static{
        //  可以在这里加载枚举的配置文件 比如从 properties  数据库中加载
        //  加载完后 使用DynamicEnumUtil.addEnum 动态增加枚举值
        //  然后正常使用枚举即可
        List<SymbolCoin> voList = RedisUtils.getCacheList("symbol_coin_list") == null ? new ArrayList<>() : RedisUtils.getCacheList("symbol_coin_list");
        for (SymbolCoin vo : voList) {
            addTestEnum(vo.getCode().toUpperCase(), vo.getCode(), vo.getName(), 0, vo.getEnumName().toLowerCase());
        }

        EnumSet<SymbolEnum> set = EnumSet.allOf(SymbolEnum.class);
        for (SymbolEnum each: set ) {
            // 增加一个缓存 减少对枚举的修改
            enumMap.put(each.code, each);
        }
    }

    // 根据关键字段获取枚举值  可以在这里做一些修改 来达到动态添加的效果
    public SymbolEnum getEnum(String value){
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
        DynamicEnumUtil.addEnum(SymbolEnum.class, enumName, new Class[]{String.class, String.class, Integer.class, String.class}, new Object[]{code, name,supTimeContract,coin});
    }

    public static String getCoinByCode(String code){
        for(SymbolEnum symbol : SymbolEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.coin;
            }
        }
        return null;
    }


    public static String getCodeByCoin(String coin){
        for(SymbolEnum symbol : SymbolEnum.values()){
            if(symbol.coin.equals(coin)){
                return symbol.code;
            }
        }
        return null;
    }


    /**
     * 根据编码获取交易对名称
     * @param code
     * @return
     */
    public static String getNameByCode(String code){
        for(SymbolEnum symbol : SymbolEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.name;
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
        for(SymbolEnum symbol : SymbolEnum.values()){
            if(symbol.code.equals(code)){
                return symbol.supTimeContract == 1;
            }
        }
        return false;
    }

    public static SymbolEnum getCodeByEnum(String code){
        for(SymbolEnum symbol : SymbolEnum.values()){
            if(symbol.code.equals(code)){
                return symbol;
            }
        }
        return null;
    }

}
