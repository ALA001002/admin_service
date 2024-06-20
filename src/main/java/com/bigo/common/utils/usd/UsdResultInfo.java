package com.bigo.common.utils.usd;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/12 13:02
 */
@Data
public class UsdResultInfo {

    private List<DataInfo> data;

    public BigDecimal getPrice() {
        return data.get(0).getQuote().USD.price;
    }

    @Data
    class DataInfo{
        String symbol;
        quote quote;
    }

    @Data
    class quote {
        USD USD;
    }

    @Data
    class USD {
        BigDecimal price;   // 价格
        Date last_updated;  // 上次更新时间
    }



}
