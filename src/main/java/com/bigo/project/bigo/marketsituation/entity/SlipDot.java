package com.bigo.project.bigo.marketsituation.entity;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 滑点entity
 * @author: wenxm
 * @date: 2020/7/9 14:29
 */
@Data
public class SlipDot extends BaseEntity {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 交易对
     */
    @NotBlank(message = "交易对不能为空")
    private String symbol;
    /**
     * 调整价格
     */
    @NotNull(message = "滑点不能为空")
    private BigDecimal adjustPrice;
    /**
     * 状态 0-就绪 1-正在运行 2-已结束
     */
    private Integer status;
    /**
     * 开始时间
     */
    private Date startDotTime;
    /**
     * 结束时间
     */
    private Date stopDotTime;
    /**
     * 创建人id
     */
    private Long creatorId;
    /**
     * 是否已删除 0-否 1-是
     */
    private Integer deleted;
    /**
     * 创建人姓名
     */
    private String creatorName;
    /**
     * 操作人id
     */
    private Long operateId;
}
