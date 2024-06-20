package com.bigo.project.bigo.config.service.impl;

import com.bigo.project.bigo.config.entity.ConfigSetting;

public interface IConfigSettingService {
    ConfigSetting getConfigSetting();
    int updateConfigSetting(ConfigSetting config);
}
