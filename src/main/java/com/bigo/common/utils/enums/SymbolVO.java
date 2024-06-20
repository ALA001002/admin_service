package com.bigo.common.utils.enums;

import lombok.Data;

@Data
public class SymbolVO {

    private String enumName;
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

}
