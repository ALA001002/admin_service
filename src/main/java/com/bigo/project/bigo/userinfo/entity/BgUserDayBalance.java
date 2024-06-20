package com.bigo.project.bigo.userinfo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/4/16 11:38 PM
 */
@Data
public class BgUserDayBalance {
    private long id;
    private Long userId;
    private String balance;
    private Integer dayNo;
    private Timestamp createTime;
}
