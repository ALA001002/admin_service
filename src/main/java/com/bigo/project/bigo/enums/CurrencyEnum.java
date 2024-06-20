package com.bigo.project.bigo.enums;

/**
 * @Description 币种枚举
 * @Author wenxm
 * @Date 2020/6/18 17:06
 */
public enum CurrencyEnum {
    /**
     * 比特币
     */
    //BTC("BTC"),
    /**
     * USDT
     */
    USDT("USDT"),
    /**
     * 以太坊
     */
    ETH("ETH"),
    /**
     * VST（体验币）
     */
//    VST("VST"),
    /**
     * DIEM（天秤座币）
     */
//    DIEM("DIEM"),
    /**
     * BTC（比特币）
     */
    BTC("BTC"),
    ;

    private String code;

    CurrencyEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
