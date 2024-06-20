package com.bigo.project.bigo.marketsituation.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.http.HttpClientUtil;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.marketsituation.entity.DotRecord;
import com.bigo.project.bigo.marketsituation.entity.SlipDot;
import com.bigo.project.bigo.marketsituation.mapper.SlipDotMapper;
import com.bigo.project.bigo.marketsituation.service.ISlipDotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/9 15:22
 */
@Service
public class SlipDotServiceImpl implements ISlipDotService {

    @Autowired
    private SlipDotMapper slipDotMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Boolean addSlipDot(SlipDot dot) {
        dot.setStatus(0);
        return slipDotMapper.insert(dot) > 0;
    }

    @Override
    public Boolean deleteLogical(Long id) {
        return slipDotMapper.deleteLogical(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startSlipDot(SlipDot dot) {
        SlipDot runningDot = slipDotMapper.getRunningDotBySymbol(dot.getSymbol());
        if(runningDot != null){
            throw new RuntimeException("开始滑点失败，该交易对有正在运行的滑点");
        }
        dot.setStatus(1);
        dot.setStartDotTime(new Date());
        dot.setStopDotTime(null);
        slipDotMapper.update(dot);
        insertRecord(dot.getId(),0,dot.getOperateId());
        redisCache.setCacheObject(dot.getSymbol().toLowerCase()+"_slipdot",dot.getAdjustPrice());
        return Boolean.TRUE;
    }

    @Override
    public Boolean stopSlipDot(SlipDot dot) {
        dot.setStatus(2);
        dot.setStartDotTime(null);
        dot.setStopDotTime(new Date());
        slipDotMapper.update(dot);
        insertRecord(dot.getId(),1,dot.getOperateId());
        redisCache.deleteObject(dot.getSymbol().toLowerCase()+"_slipdot");
        return Boolean.TRUE;
    }

    @Override
    public List<SlipDot> listByEntity(SlipDot dot) {
        return slipDotMapper.listByEntity(dot);
    }

    @Override
    public SlipDot getById(Long id) {
        return slipDotMapper.getById(id);
    }

    @Override
    public List<SlipDot> listSlipDotByDate(Map<String, Object> params) {
        return slipDotMapper.listSlipDotByDate(params);
    }

    /**
     * 插入滑点操作记录
     * @param dotId
     * @param type
     * @param operatorId
     */
    private void insertRecord(Long dotId, Integer type, Long operatorId){
        DotRecord record = new DotRecord();
        record.setDotId(dotId);
        record.setOperatorId(operatorId);
        record.setType(type);
        slipDotMapper.insertDotRecord(record);
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
