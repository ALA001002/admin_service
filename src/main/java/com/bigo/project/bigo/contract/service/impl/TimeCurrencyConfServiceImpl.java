package com.bigo.project.bigo.contract.service.impl;

import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.RedisUtils;
import com.bigo.project.bigo.contract.domain.TimeCurrencyConf;
import com.bigo.project.bigo.contract.mapper.TimeCurrencyConfMapper;
import com.bigo.project.bigo.contract.service.ITimeCurrencyConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 限时币种配置Service业务层处理
 * 
 * @author WY
 * @date 2021-02-01
 */
@Service
public class TimeCurrencyConfServiceImpl implements ITimeCurrencyConfService 
{
    @Autowired
    private TimeCurrencyConfMapper timeCurrencyConfMapper;


    /**
     * 查询限时币种配置
     * 
     * @param id 限时币种配置ID
     * @return 限时币种配置
     */
    @Override
    public TimeCurrencyConf selectTimeCurrencyConfById(Long id)
    {
        return timeCurrencyConfMapper.selectTimeCurrencyConfById(id);
    }

    /**
     * 查询限时币种配置列表
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 限时币种配置
     */
    @Override
    public List<TimeCurrencyConf> selectTimeCurrencyConfList(TimeCurrencyConf timeCurrencyConf)
    {
        return timeCurrencyConfMapper.selectTimeCurrencyConfList(timeCurrencyConf);
    }

    /**
     * 新增限时币种配置
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 结果
     */
    @Override
    public int insertTimeCurrencyConf(TimeCurrencyConf timeCurrencyConf)
    {
        timeCurrencyConf.setCreateTime(DateUtils.getNowDate());
        int status = timeCurrencyConfMapper.insertTimeCurrencyConf(timeCurrencyConf);
        return status;
    }

    /**
     * 修改限时币种配置
     * 
     * @param timeCurrencyConf 限时币种配置
     * @return 结果
     */
    @Override
    public int updateTimeCurrencyConf(TimeCurrencyConf timeCurrencyConf)
    {
        timeCurrencyConf.setUpdateTime(DateUtils.getNowDate());
        int status = timeCurrencyConfMapper.updateTimeCurrencyConf(timeCurrencyConf);
        //写入redis
        List<TimeCurrencyConf> currencyConfs = selectTimeCurrencyConfList(new TimeCurrencyConf());
        RedisUtils.deleteObject("time_currency_conf");
        RedisUtils.setCacheList("time_currency_conf", currencyConfs);
        return status;
    }

    /**
     * 批量删除限时币种配置
     * 
     * @param ids 需要删除的限时币种配置ID
     * @return 结果
     */
    @Override
    public int deleteTimeCurrencyConfByIds(Long[] ids)
    {
        return timeCurrencyConfMapper.deleteTimeCurrencyConfByIds(ids);
    }

    /**
     * 删除限时币种配置信息
     * 
     * @param id 限时币种配置ID
     * @return 结果
     */
    @Override
    public int deleteTimeCurrencyConfById(Long id)
    {
        return timeCurrencyConfMapper.deleteTimeCurrencyConfById(id);
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal(7.3e-7).toPlainString());
    }
}
