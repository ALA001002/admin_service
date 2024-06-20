package com.bigo.project.bigo.agent.vo;

import com.bigo.project.system.domain.SysDept;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/3/20 18:26
 */
@Data
public class UserSumVo {

    private Long uid;

    private Long parentUid;

    private String username;

    private BigDecimal usdt;

    private BigDecimal eth;

    private BigDecimal btc;

    private List<UserSumVo> children  = new ArrayList<UserSumVo>();

    private Date registerTime;


}
