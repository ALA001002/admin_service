package com.bigo.project.bigo.userinfo.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.Constants;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.luck.domain.Jackpot;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.entity.ExportBigoUserEntity;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 后台资产查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/bigouser")
public class BigoUserController extends BaseController {

    @Autowired
    private IBigoUserService userService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenService tokenService;



    @Autowired
    private IAgentService agentService;

    @PreAuthorize("@ss.hasPermi('bigo:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(BigoUserEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<BigoUserEntity> list = userService.listByEntity(entity);
        if(entity.getAuthFlag() != null) {
            for (BigoUserEntity user : list) {
                if(user.getAuthPhotos() != null) {
                    user.setImgList(Arrays.asList(user.getAuthPhotos().split(";")));
                }
            }
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bigo:user:list')")
    @GetMapping("/authList")
    public TableDataInfo authList(BigoUserEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<BigoUserEntity> list = userService.authListByEntity(entity);
        if(entity.getAuthFlag() != null) {
            for (BigoUserEntity user : list) {
                if(user.getAuthPhotos() != null) {
                    user.setImgList(Arrays.asList(user.getAuthPhotos().split(";")));
                }
            }
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bigo:user:lowerList')")
    @GetMapping("/lowerList")
    public TableDataInfo lowerList(BigoUserEntity entity) {
        startPage();
        List<BigoUserEntity> list = userService.lowerListEntity(entity);
        if(entity.getAuthFlag() != null) {
            for (BigoUserEntity user : list) {
                if(user.getAuthPhotos() != null) {
                    user.setImgList(Arrays.asList(user.getAuthPhotos().split(";")));
                }
            }
        }
        return getDataTable(list);
    }



    @PreAuthorize("@ss.hasPermi('bigo:user:batchEdit')")
    @PostMapping("/batchEditUserStatus")
    public AjaxResult batchEditUserStatus(@RequestBody BigoUserEntity entity) {
        for (Long uid : entity.getUids()) {
            BigoUser bigoUser = userService.getUserByUid(uid);
            if (bigoUser == null) {
                continue;
            }
            BigoUser user = new BigoUser();
            user.setUid(uid);
            user.setStatus(entity.getStatus());
            userService.updateUser(user);
            String token = redisCache.getCacheObject("bigouser_" + user.getUid());
            LoginUser loginUser = tokenService.getLoginUserByToken(token);
            if(loginUser != null && entity.getStatus() != 1) {
                BigoUser newUser = userService.getUserByUid(user.getUid());
                loginUser.setBigoUser(newUser);
                tokenService.refreshToken(loginUser);
            }else if(loginUser != null && entity.getStatus() == 1){
                redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + loginUser.getToken());
            }
        }
        return AjaxResult.success();
    }

    /**
     *
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:export')")
    @Log(title = "导出用户", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BigoUserEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentId());
        }
        startPage(1,9999);
        List<ExportBigoUserEntity> list = userService.exportListByEntity(entity);
        ExcelUtil<ExportBigoUserEntity> util = new ExcelUtil<ExportBigoUserEntity>(ExportBigoUserEntity.class);
        return util.exportExcel(list, "User");
    }

    /**
     * 禁用/启用角色
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.CHANGE_STATUS)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody BigoUserEntity user)
    {
        BigoUser bigoUser = userService.getUserByUid(user.getUid());
        if(bigoUser == null){
            return AjaxResult.error("用户不存在");
        }
        if(bigoUser.getStatus() > 1){
            return AjaxResult.error("内部账号，无法操作");
        }
        if (bigoUser.getStatus().equals(user.getStatus())){
            return AjaxResult.success();
        }
        if (user.getStatus() != 0 && user.getStatus() != 1 && user.getStatus() != 2){
            return AjaxResult.error("用户状态不合法");
        }
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(user.getUid());
        updateUser.setStatus(user.getStatus());
        //删除用户登录信息
        Object tokenObject = redisCache.getCacheObject("bigouser_"+bigoUser.getUid());
        if(tokenObject != null) {
            String userKey = tokenService.getUserKey(tokenObject.toString());
            tokenService.delLoginUser(userKey);
        }
        return toAjax(userService.updateUser(updateUser));
    }

    /**
     * 用户身份认证审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:checkAuthStatus')")
    @Log(title = "实名审核", businessType = BusinessType.AUTH_CHECK)
    @PutMapping("/checkAuthStatus")
    public AjaxResult checkAuthStatus(@RequestBody BigoUserEntity user)
    {
        BigoUser bigoUser = userService.getUserByUid(user.getUid());
        if(bigoUser == null){
            return AjaxResult.error("用户不存在");
        }
        if (bigoUser.getAuthStatus() != 1){
            return AjaxResult.error("用户认证状态不是待审核");
        }
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(user.getUid());
        updateUser.setAuthStatus(user.getAuthStatus());
        Boolean checkStatus = redisTemplate.opsForValue().setIfAbsent("redis_checkAuthStatus_"+user.getUid(),"", 10, TimeUnit.SECONDS);
        if(!checkStatus){
            return AjaxResult.error("请勿重复提交");
        }
        userService.CheckAuthStatus(updateUser);
        String token = redisCache.getCacheObject("bigouser_" + bigoUser.getUid());
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        if(loginUser != null) {
            BigoUser newUser = userService.getUserByUid(user.getUid());
            loginUser.setBigoUser(newUser);
            tokenService.refreshToken(loginUser);
        }
        return AjaxResult.success();
    }


    /**
     * 用户身份认证审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:batchCheckAuthStatus')")
    @Log(title = "实名审核", businessType = BusinessType.AUTH_CHECK)
    @PostMapping("/batchCheckAuthStatus")
    public AjaxResult batchCheckAuthStatus(@RequestBody BigoUserEntity entity) {
        if(entity.getUids() == null || entity.getUids().length <= 0){
            return AjaxResult.error("用户ID不能为空");
        }

        for (Long uid : entity.getUids()) {
            BigoUser bigoUser = userService.getUserByUid(uid);
            if(bigoUser == null){
                continue;
            }
            if (bigoUser.getAuthStatus() != 1 &&  bigoUser.getAuthStatus() != 3){
                continue;
            }
            BigoUser updateUser = new BigoUser();
            updateUser.setUid(uid);
            updateUser.setAuthStatus(entity.getAuthStatus());
            updateUser.setAuthRationale(entity.getAuthRationale());
            Boolean checkStatus = redisTemplate.opsForValue().setIfAbsent("redis_checkAuthStatus_"+uid,"", 10, TimeUnit.SECONDS);
            if(!checkStatus){
                continue;
            }
            userService.CheckAuthStatus(updateUser);
            String token = redisCache.getCacheObject("bigouser_" + bigoUser.getUid());
            LoginUser loginUser = tokenService.getLoginUserByToken(token);
            if(loginUser != null) {
                BigoUser newUser = userService.getUserByUid(bigoUser.getUid());
                loginUser.setBigoUser(newUser);
                tokenService.refreshToken(loginUser);
            }
        }
        return AjaxResult.success();
    }

    /**
     * 设置邀请人
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:setParent')")
    @Log(title = "设置邀请人", businessType = BusinessType.SET_PARENT)
    @PutMapping("/setParentUid")
    public AjaxResult setParentUid(@RequestBody BigoUserEntity user)
    {
        BigoUser bigoUser = userService.getUserByUid(user.getUid());
        if(bigoUser == null){
            return AjaxResult.error("用户不存在！");
        }
        if(user.getParentUid().equals(user.getUid())){
            return AjaxResult.error("邀请人不能设为用户自己！");
        }
        if (bigoUser.getParentUid() != null){
            return AjaxResult.error("该用户已有邀请人，无法设置！");
        }
        if(user.getParentUid() == null){
            return AjaxResult.error("邀请人ID不能为空！");
        }
        BigoUser parentUser = userService.getUserByUid(user.getParentUid());
        if(parentUser == null){
            return AjaxResult.error("邀请人不存在！");
        }
        if(!parentUser.getRegisterTime().before(bigoUser.getRegisterTime())){
            return AjaxResult.error("邀请人的注册时间不能晚于该用户的注册时间！");
        }
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(user.getUid());
        updateUser.setParentUid(parentUser.getUid());
        userService.updateUser(updateUser);
        String token = redisCache.getCacheObject("bigouser_" + bigoUser.getUid());
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        if(loginUser != null) {
            BigoUser newUser = userService.getUserByUid(user.getUid());
            loginUser.setBigoUser(newUser);
            tokenService.refreshToken(loginUser);
        }
        return AjaxResult.success();
    }


    /**
     * 修改邀请人
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:editParent')")
    @Log(title = "修改邀请人", businessType = BusinessType.SET_PARENT)
    @PutMapping("/editParent")
    public AjaxResult editParent(@RequestBody JSONObject jsonObject) {
        Long uid = jsonObject.getLong("uid");
        Long newParentUid = jsonObject.getLong("newParentUid");
        BigoUser bigoUser = userService.getUserByUid(uid);
        if(bigoUser == null){
            return AjaxResult.error("用户不存在！");
        }
        if(newParentUid.equals(uid)){
            return AjaxResult.error("邀请人不能设为用户自己！");
        }
        if(newParentUid == null){
            return AjaxResult.error("邀请人ID不能为空！");
        }
        BigoUser parentUser = userService.getUserByUid(newParentUid);
        if(parentUser == null){
            return AjaxResult.error("邀请人不存在！");
        }

        BigoUser updateUser = new BigoUser();
        updateUser.setUid(uid);
        updateUser.setParentUid(parentUser.getUid());
        userService.updateUserParent(updateUser, parentUser);
        String token = redisCache.getCacheObject("bigouser_" + bigoUser.getUid());
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        if(loginUser != null) {
            BigoUser newUser = userService.getUserByUid(uid);
            loginUser.setBigoUser(newUser);
            tokenService.refreshToken(loginUser);
        }
        return AjaxResult.success();
    }

    @GetMapping("/getUser/{uid}")
    public AjaxResult getUser(@PathVariable Long uid) {
        BigoUser bigoUser = userService.getUserByUid(uid);
        if(bigoUser == null){
            return AjaxResult.error("用户不存在");
        }
        bigoUser.setPassword(null);
        bigoUser.setPayPassword(null);
        return AjaxResult.success(bigoUser);
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:editUser')")
    @Log(title = "修改用户信息", businessType = BusinessType.INSERT)
    @PutMapping("/editUser")
    public AjaxResult editUser(@RequestBody BigoUser user, HttpServletRequest request) {
        BigoUser oldUser = userService.getUserByUid(user.getUid());
        if(oldUser == null) {
            return AjaxResult.error("用户不存在");
        }
        BigoUserEntity entity = null;
        userService.editUser(user);
        String token = redisCache.getCacheObject("bigouser_" + user.getUid());
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        if(loginUser != null) {
            BigoUser newUser = userService.getUserByUid(user.getUid());
            loginUser.setBigoUser(newUser);
            tokenService.refreshToken(loginUser);
        }
        return AjaxResult.success();
    }

    /**
     * 修改用户信息
     */
    @PreAuthorize("@ss.hasPermi('bigo:user:editUserStatus')")
    @Log(title = "修改用户信息", businessType = BusinessType.INSERT)
    @PutMapping("/editUserStatus")
    public AjaxResult editUserStatus(@RequestBody BigoUser user, HttpServletRequest request) {
        BigoUser oldUser = userService.getUserByUid(user.getUid());
        if(oldUser == null) {
            return AjaxResult.error("用户不存在");
        }
        BigoUserEntity entity = null;
        userService.editUser(user);
        String token = redisCache.getCacheObject("bigouser_" + user.getUid());
        LoginUser loginUser = tokenService.getLoginUserByToken(token);
        if(loginUser != null) {
            BigoUser newUser = userService.getUserByUid(user.getUid());
            loginUser.setBigoUser(newUser);
            tokenService.refreshToken(loginUser);
        }
        return AjaxResult.success();
    }

}
