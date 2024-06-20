package com.bigo.project.bigo.userinfo.domain;

import java.math.BigDecimal;
import com.bigo.framework.aspectj.lang.annotation.Excel;
import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * 等级赠送奖励对象 bg_level_award
 * 
 * @author bigo
 * @date 2023-11-25
 */
@Data
public class LevelAward extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long uid;

    /** VIP等级 */
    @Excel(name = "VIP等级")
    private Long level;

    /** 奖励金额 */
    @Excel(name = "奖励金额")
    private BigDecimal awardAmount;

    /** 达标用户 */
    @Excel(name = "达标用户")
    private Long requireUser;

    /** 团队资产 */
    @Excel(name = "团队资产")
    private BigDecimal teamAsset;

    /** 领取状态：0-未领取，1-已领取 */
    @Excel(name = "领取状态：0-未领取，1-已领取")
    private Long getStatus;


}
