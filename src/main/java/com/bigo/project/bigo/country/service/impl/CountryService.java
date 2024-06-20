package com.bigo.project.bigo.country.service.impl;

import com.bigo.project.bigo.api.vo.CountryInfoVO;
import com.bigo.project.bigo.country.mapper.CountryMapper;
import com.bigo.project.bigo.country.service.ICountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description 国家区号service
 * @Author wenxm
 * @Date 2020/6/23 15:41
 */
@Service
public class CountryService implements ICountryService {

    @Autowired
    private CountryMapper countryMapper;

    @Override
    public List<CountryInfoVO> listCountryInfo() {
        return countryMapper.listCountryInfo();
    }

    @Override
    public CountryInfoVO getId(Long areaId) {
        return countryMapper.getId(areaId);
    }
}
