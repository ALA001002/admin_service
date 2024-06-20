package com.bigo.project.bigo.config.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.config.entity.ConfigSetting;
import com.bigo.project.bigo.config.mapper.ConfigSettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/9 15:22
 */
@Service
public class ConfigSettingServiceImpl implements IConfigSettingService {

    @Resource
    private ConfigSettingMapper configSettingMapper;

    @Autowired
    private RedisCache redisCache;

//    @Resource
//    ConfigService configService;

    @Override
    public ConfigSetting getConfigSetting() {
        ConfigSetting configSetting = configSettingMapper.getConfigSetting();
        return configSetting;
    }

    @Override
    @Transactional
    public int updateConfigSetting(ConfigSetting config) {
        //对象转成JSON保存到redis
        JSONObject object = (JSONObject) JSONObject.toJSON(config);
//        Boolean hideFlag = Optional.ofNullable(config.getHideFlag()).orElse(false);
//        Boolean licai = Optional.ofNullable(config.getLicaiFlag()).orElse(false);
//        Boolean shequFlag = Optional.ofNullable(config.getShequFlag()).orElse(false);
//        Boolean xiadanFlag = Optional.ofNullable(config.getXiadanFlag()).orElse(false);
//        Boolean gerenFlag = Optional.ofNullable(config.getGerenFlag()).orElse(false);
//        configService.updateConfig(hideFlag,licai,xiadanFlag,shequFlag,gerenFlag);
        redisCache.deleteObject("config_setting");
        redisCache.setCacheObject("config_setting", object.toJSONString());
        return configSettingMapper.update(config);
    }
}
