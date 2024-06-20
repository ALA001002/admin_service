package com.bigo.project.bigo.agent.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/3/21 3:31
 */
@Data
public class UserChildrenVo {

    private Long uid;

    private Long parentUid;

    private String username;

    private BigDecimal totalRecharge;

    private BigDecimal totalWithdraw;

    private BigDecimal userBalance;

    private Boolean hasChildren;

    private List<UserSumVo> children = new ArrayList<>();
}
