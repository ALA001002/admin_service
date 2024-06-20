package com.bigo.project.bigo.wallet.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;

/**
 * 收币地址对象 to_address_info
 * 
 * @author bigo
 * @date 2022-04-28
 */
public class ToAddressInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 收币地址 */
    @Excel(name = "收币地址")
    private String address;

    /** 0-关闭，1-开启 */
    @Excel(name = "0-关闭，1-开启")
    private Long status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCurrency(String currency) 
    {
        this.currency = currency;
    }

    public String getCurrency() 
    {
        return currency;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return "ToAddressInfo{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }

    public ToAddressInfo() {
    }

    public ToAddressInfo(String currency) {
        this.currency = currency;
    }
}
