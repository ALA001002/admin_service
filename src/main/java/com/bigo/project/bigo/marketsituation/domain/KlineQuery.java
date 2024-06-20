package com.bigo.project.bigo.marketsituation.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 17:27
 */
@Getter
@Setter
public class KlineQuery {
    /**
     * 交易对
     */
    private String symbol;
    /**
     * 时间粒度
     */
    private String period;
    /**
     * 时间戳,查询这个时间之前的数据
     */
    private Long timestamp;
    /**
     * 查询条数限制
     */
    private Integer size;

    private Long timeOfIssue;
}
