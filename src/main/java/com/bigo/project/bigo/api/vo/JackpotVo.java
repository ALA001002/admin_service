package com.bigo.project.bigo.api.vo;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/1 2:14
 */
@Data
public class JackpotVo {

    /** id */
    private Long id;

    /** 币种 */
    private String coin;

    /** 数量 */
    private BigDecimal num;

}
