package com.bigo.project.bigo.api.vo;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

@Data
public class BankInfoVO {

    /** $column.columnComment */
    private Long id;

    /** 区号 */
    private String mobilePrefix;

    /** 银行编码 */
//    private String bankCode;

    /** 银行名称 */
    private String bankName;
}
