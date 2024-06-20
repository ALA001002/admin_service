package com.bigo.project.bigo.marketsituation.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description 火币B线请求结果
 * @Author wenxm
 * @Date 2020/6/19 17:12
 */
@Getter
@Setter
public class HbBlineResult {
    /**
     * K线主题
     */
    private String ch;
    /**
     * 请求结果
     */
    private String status;
    /**
     * 请求时间戳
     */
    private Long ts;
    /**
     * K线数据
     */
    private List<HbBline> data;
}
