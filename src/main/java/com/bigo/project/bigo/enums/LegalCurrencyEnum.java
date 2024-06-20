package com.bigo.project.bigo.enums;

/**
 * @Description 法币枚举
 * @Author wenxm
 * @Date 2020/6/18 17:06
 */
public enum LegalCurrencyEnum {
    /**
     * 人民币
     */
    CNY("CNY"),
    /**
     * 美元
     */
    USD("USD"),
    /**
     * 欧元
     */
    EUR("EUR"),
    /**
     * 英镑
     */
    GBP("GBP"),
    /**
     * 韩元
     */
    KRW("KRW"),
    ;
    private String code;

    LegalCurrencyEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
