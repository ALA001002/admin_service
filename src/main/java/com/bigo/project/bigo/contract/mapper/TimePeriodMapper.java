package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.contract.domain.TimePeriod;

import java.util.List;

/**
 * @description: 限时合约周期mapper
 * @author: wenxm
 * @date: 2020/7/20 15:28
 */
public interface TimePeriodMapper {

    /**
     * 插入
     * @param timePeriod
     * @return
     */
    int insert(TimePeriod timePeriod);

    /**
     * 变更状态
     * @param timePeriod
     * @return
     */
    int update(TimePeriod timePeriod);

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 根据时间获取周期信息
     * @param period
     * @return
     */
    TimePeriod getByPeriod(Integer period);

    /**
     * 获取所有可用法币
     * @return
     */
    List<TimePeriod> listAllPeriod();

    /**
     * 根据参数查询
     * @param entity
     * @return
     */
    List<TimePeriod> listByEntity(TimePeriod entity);

    TimePeriod selectTimePeriodById(TimePeriod entity);
}
