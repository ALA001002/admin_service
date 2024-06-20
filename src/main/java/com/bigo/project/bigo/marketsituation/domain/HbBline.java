package com.bigo.project.bigo.marketsituation.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/19 17:36
 */
@Getter
@Setter
public class HbBline {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 新加坡时间的时间戳，单位毫秒
     */
    private Long ts;
    /**
     * b线数据
     */
    private List<Bline> data;
}
