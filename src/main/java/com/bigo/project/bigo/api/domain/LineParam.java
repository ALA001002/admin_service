package com.bigo.project.bigo.api.domain;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 9:34
 */
@Getter
@Setter
public class LineParam {
    /**
     * 交易对
     */
    @NotBlank(message = "symbol_cannot_be_empty")
    private String symbol;
    /**
     * K线时间粒度
     */
    private String period;
}
