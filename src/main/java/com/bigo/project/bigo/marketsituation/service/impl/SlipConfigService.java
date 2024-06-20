package com.bigo.project.bigo.marketsituation.service.impl;

import com.bigo.project.bigo.marketsituation.domain.Kline;
import com.bigo.project.bigo.marketsituation.domain.KlineQuery;
import com.bigo.project.bigo.marketsituation.mapper.KlineMapper;
import com.bigo.project.bigo.marketsituation.mapper.SlipConfigMapper;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import com.bigo.project.bigo.marketsituation.service.ISlipConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/20 17:32
 */
@Service
public class SlipConfigService implements ISlipConfigService {

    @Resource
    private SlipConfigMapper slipConfigMapper;


    public int deleteSlipConfigById(Long id) {
        return slipConfigMapper.deleteSlipConfig(id);
    }
}
