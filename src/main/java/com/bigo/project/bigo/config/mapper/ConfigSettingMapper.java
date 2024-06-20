package com.bigo.project.bigo.config.mapper;

import com.bigo.project.bigo.config.entity.ConfigSetting;

public interface ConfigSettingMapper {
    ConfigSetting getConfigSetting();

    int update(ConfigSetting config);
}
