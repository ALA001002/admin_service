package com.bigo.project.bigo.contract.service.impl;

import com.bigo.project.bigo.contract.domain.TimePeriod;
import com.bigo.project.bigo.contract.mapper.TimePeriodMapper;
import com.bigo.project.bigo.contract.service.ITimePeriodService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/9/1 16:14
 */
@Service
public class TimePeriodServiceImpl implements ITimePeriodService {

    @Resource
    private TimePeriodMapper timePeriodMapper;

    @Override
    public int insert(TimePeriod timePeriod) {
        return timePeriodMapper.insert(timePeriod);
    }

    @Override
    public int update(TimePeriod timePeriod) {
        return timePeriodMapper.update(timePeriod);
    }

    @Override
    public int deleteById(Long id) {
        return timePeriodMapper.deleteById(id);
    }

    @Override
    public TimePeriod getByPeriod(Integer period) {
        return timePeriodMapper.getByPeriod(period);
    }

    @Override
    public List<TimePeriod> listAllPeriod() {
        return timePeriodMapper.listAllPeriod();
    }

    @Override
    public List<TimePeriod> listByEntity(TimePeriod entity) {
        return timePeriodMapper.listByEntity(entity);
    }

    @Override
    public TimePeriod selectTimePeriodById(TimePeriod entity) {
        return timePeriodMapper.selectTimePeriodById(entity);
    }
}
