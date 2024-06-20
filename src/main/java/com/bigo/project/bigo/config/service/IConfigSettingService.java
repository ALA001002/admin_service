package com.bigo.project.bigo.config.service;

import com.bigo.project.bigo.config.entity.ConfigSetting;

public interface IConfigSettingService {
    ConfigSetting getConfigSetting();

    int updateConfigSetting(ConfigSetting config);
}
