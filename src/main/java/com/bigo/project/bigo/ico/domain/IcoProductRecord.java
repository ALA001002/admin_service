package com.bigo.project.bigo.ico.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * ico产品购买记录对象 bg_ico_product_record
 * 
 * @author bigo
 * @date 2023-01-09
 */
@Data
public class IcoProductRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户id */
    @Excel(name = "用户id")
    private Long uid;

    /** 币种 */
    @Excel(name = "币种")
    private String currency;

    /** 购买数量 */
    @Excel(name = "购买数量")
    private Long amount;

    public IcoProductRecord(Long uid) {
        this.uid = uid;
    }

    public IcoProductRecord() {
    }
}
