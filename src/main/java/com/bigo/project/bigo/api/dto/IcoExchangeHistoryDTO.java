package com.bigo.project.bigo.api.dto;

import lombok.Data;

@Data
public class IcoExchangeHistoryDTO {



    /** 用户UID */
    private Long uid;

    /** 币种 */
    private String currency;


    /** 交易类型：0-买，1-卖 */
    private Integer type;

}
