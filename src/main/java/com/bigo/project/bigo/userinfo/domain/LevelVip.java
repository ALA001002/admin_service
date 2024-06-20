package com.bigo.project.bigo.userinfo.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * vip等级返佣对象 bg_level_vip
 * 
 * @author bigo
 * @date 2023-11-25
 */
@Getter
@Setter
public class LevelVip extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** vip级别 */
    @Excel(name = "vip级别")
    private Long level;

    /** 一级返佣 */
    @Excel(name = "一级返佣")
    private BigDecimal levelOne;

    /** 二级返佣 */
    @Excel(name = "二级返佣")
    private BigDecimal levelTwo;

    /** 三级返佣 */
    @Excel(name = "三级返佣")
    private BigDecimal levelThree;

}
