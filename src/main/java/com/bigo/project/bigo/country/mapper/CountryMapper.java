package com.bigo.project.bigo.country.mapper;

import com.bigo.project.bigo.api.vo.CountryInfoVO;

import java.util.List;

/**
 * @Description 国家区号mapper
 * @Author wenxm
 * @Date 2020/6/23 15:41
 */
public interface CountryMapper {

    /**
     * 获取可用的国家区号列表
     * @return
     */
    List<CountryInfoVO> listCountryInfo();

    CountryInfoVO getId(Long areaId);
}
