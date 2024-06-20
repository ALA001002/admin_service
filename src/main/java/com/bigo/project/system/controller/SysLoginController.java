package com.bigo.project.system.controller;

import java.util.List;
import java.util.Set;

import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.project.system.domain.SysRole;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.bigo.common.constant.Constants;
import com.bigo.common.utils.ServletUtils;
import com.bigo.framework.security.LoginBody;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.SysLoginService;
import com.bigo.framework.security.service.SysPermissionService;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.system.domain.SysMenu;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author bigo
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 登录方法
     * 
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        SysUser user = tokenService.getLoginUserByToken(token).getUser();
        if (!checkGoogleCode(user.getUserId(), loginBody.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证失败，请重试");
        }
//        if(!loginBody.getGoogleCaptcha().equals(6788723423L)) {
//
//        }
        SysRole csRole = user.getRoles().stream().filter(a->"customerservice".equals(a.getRoleKey())).findAny().orElse(null);
        if(csRole != null){
            ajax.put("customerservice", true);
        }else{
            ajax.put("customerservice", false);
        }
        return ajax;
    }

    /**
     * 验证谷歌验证码
     * @param code
     * @return
     */
    boolean checkGoogleCode(Long userId, Long code) {
        SysUser sysUser = sysUserService.selectUserById(userId);
        if(sysUser.getGoogleAuthStatus() == 0) return true;
        if(sysUser.getGoogleAuthStatus() == 1 && code == null) return false;
        String googleAuthSecretKey = sysUser.getGoogleAuthSecretKey();
        return GoogleAuthenticator.checkGoogleCode(googleAuthSecretKey, code);
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
