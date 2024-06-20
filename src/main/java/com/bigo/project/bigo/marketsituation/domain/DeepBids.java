package com.bigo.project.bigo.marketsituation.domain;

import java.math.BigDecimal;

public class DeepBids {
    /**
     * 价位
     */
    private BigDecimal price;

    /**
     * 挂单量
     */
    private BigDecimal pendOrderVolume;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPendOrderVolume() {
        return pendOrderVolume;
    }

    public void setPendOrderVolume(BigDecimal pendOrderVolume) {
        this.pendOrderVolume = pendOrderVolume;
    }
}
