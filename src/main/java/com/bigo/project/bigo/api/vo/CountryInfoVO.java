package com.bigo.project.bigo.api.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description 国家区号VO
 * @Author wenxm
 * @Date 2020/6/23 15:41
 */
@Getter
@Setter
public class CountryInfoVO {

    /**
     * 区域id
     */
    private Long areaId;

    /**
     * 国家名称
     */
    private String country;
    /**
     * 区号
     */
    private String areaCode;

}
