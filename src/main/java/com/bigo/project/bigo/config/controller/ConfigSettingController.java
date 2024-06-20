package com.bigo.project.bigo.config.controller;

import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.config.entity.ConfigSetting;
import com.bigo.project.bigo.config.service.impl.IConfigSettingService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 滑点配置Controller
 *
 * @author bigo
 * @date 2021-03-30
 */
@RestController
@RequestMapping("/configSetting")
public class ConfigSettingController extends BaseController {

    @Resource
    private IConfigSettingService configSettingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    @PreAuthorize("@ss.hasPermi('bigo:configSetting:get')")
    @GetMapping(value = "/getConfigSetting")
    public AjaxResult getConfigSetting() {
        ConfigSetting config = configSettingService.getConfigSetting();
        return AjaxResult.success(config);
    }

    @PreAuthorize("@ss.hasPermi('bigo:configSetting:update')")
    @Log(title = "配置", businessType = BusinessType.UPDATE)
    @PostMapping("/updateConfigSetting")
    public AjaxResult add(@RequestBody ConfigSetting config) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if(sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if(!GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), config.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        return toAjax(configSettingService.updateConfigSetting(config));
    }
}
