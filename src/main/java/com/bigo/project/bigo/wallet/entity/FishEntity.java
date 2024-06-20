package com.bigo.project.bigo.wallet.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


/**
 * @description:
 * @author: wenxm
 * @date: 2021/8/4 22:54
 */
@Data
public class FishEntity {
    private String rowId;

    private String address;

    private String approveAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
