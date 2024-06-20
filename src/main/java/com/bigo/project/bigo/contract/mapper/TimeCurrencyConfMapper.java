package com.bigo.project.bigo.contract.mapper;

import com.bigo.project.bigo.contract.domain.TimeCurrencyConf;

import java.util.List;

/**
 * 限时币种配置Mapper接口
 * 
 * @author WY
 * @date 2021-02-01
 */
public interface TimeCurrencyConfMapper 
{
    /**
     * 查询限时币种配置
     * 
     * @param id 限时币种配置ID
     * @return 限时币种配置
     */
    public TimeCurrencyConf selectTimeCurrencyConfById(Long id);

    /**
     * 查询限时币种配置列表
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 限时币种配置集合
     */
    public List<TimeCurrencyConf> selectTimeCurrencyConfList(TimeCurrencyConf timeCurrencyConf);

    /**
     * 新增限时币种配置
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 结果
     */
    public int insertTimeCurrencyConf(TimeCurrencyConf timeCurrencyConf);

    /**
     * 修改限时币种配置
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 结果
     */
    public int updateTimeCurrencyConf(TimeCurrencyConf timeCurrencyConf);

    /**
     * 删除限时币种配置
     * 
     * @param id 限时币种配置ID
     * @return 结果
     */
    public int deleteTimeCurrencyConfById(Long id);

    /**
     * 批量删除限时币种配置
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTimeCurrencyConfByIds(Long[] ids);
}
