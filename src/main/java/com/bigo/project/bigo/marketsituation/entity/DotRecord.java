package com.bigo.project.bigo.marketsituation.entity;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description: 滑点操作记录实体
 * @author: wenxm
 * @date: 2020/7/9 14:40
 */
@Data
public class DotRecord extends BaseEntity {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 关联的滑点id
     */
    private Long dotId;
    /**
     * 操作类型 0-开始滑点 1-结束滑点
     */
    private Integer type;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 操作人id
     */
    private Long operatorId;
    /**
     * 操作人姓名
     */
    private String operatorName;
}
