package com.bigo.project.bigo.notify.domain;

import lombok.Data;

@Data
public class QuickMessage {
    Long id;
    String message;
    /** del_flag */
    private Integer delFlag;

    /** seq */
    private Long seq;
}
