package com.bigo.project.bigo.userinfo.domain;

import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户等级对象 bg_level_config
 * 
 * @author bigo
 * @date 2022-07-22
 */
@Data
public class LevelConfig extends BaseEntity implements Comparable<LevelConfig>
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 用户等级 */
    @Excel(name = "用户等级")
    private Long level;

    /** 等级名称 */
    @Excel(name = "等级名称")
    private String name;

    /** 达成此等级需要的有效用户 */
    @Excel(name = "达成此等级需要的有效用户")
    private Long requireUser;

    @Excel(name = "团队资产")
    private BigDecimal teamAsset;

    @Excel(name = "返佣比例")
    private BigDecimal awardAmount;

    @Excel(name = "团队佣金")
    private BigDecimal teamCommission;



    @Override
    public int compareTo(LevelConfig o)
    {
        if(this.getLevel()<o.getLevel())
        {
            return 1;
        }
        else if(this.getLevel()>o.getLevel())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

}
