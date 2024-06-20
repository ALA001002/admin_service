package com.bigo.project.bigo.chat.domain;

import com.bigo.framework.web.domain.BaseEntity;
import lombok.Data;

/**
 * @description: 对接信息
 * @author: wenxm
 * @date: 2020/7/14 16:16
 */
@Data
public class DockInfo extends BaseEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 对接用户的客服ID
     */
    private Long customerServiceId;
    /**
     * 客服负责的人数
     */
    private Integer userNum;

}
