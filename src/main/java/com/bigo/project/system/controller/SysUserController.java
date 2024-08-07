package com.bigo.project.system.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.google.GoogleAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.bigo.common.constant.UserConstants;
import com.bigo.common.utils.SecurityUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysPostService;
import com.bigo.project.system.service.ISysRoleService;
import com.bigo.project.system.service.ISysUserService;

/**
 * 用户信息
 * 
 * @author bigo
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    public AjaxResult export(SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    public AjaxResult importTemplate()
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("roles", roleService.selectRoleAll());
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user).intValue());
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 得到用户谷歌验证二维码
     * @return
     */
    @Log(title = "绑定谷歌验证器", businessType = BusinessType.OTHER)
    @PreAuthorize("@ss.hasPermi('system:user:bindGoogle')")
    @RequestMapping("/google_qrcode/{userId}")
    public AjaxResult getGoogleAuthQrCode(@PathVariable(value = "userId", required = false) Long userId) {
        SysUser sysUser = userService.selectUserById(userId);
        if(sysUser == null) return AjaxResult.error("用户不存在");
        log.info("接收获取谷歌验证码请求, userId={}", userId);
        String mobile = sysUser.getPhonenumber();
        String googleAuthSecretKey = sysUser.getGoogleAuthSecretKey();
        log.info("查询到管理员googleAuthSecretKey={}", googleAuthSecretKey);
        if(StringUtils.isBlank(googleAuthSecretKey)){
            googleAuthSecretKey = GoogleAuthenticator.generateSecretKey();
            SysUser updateSysUser = new SysUser();
            updateSysUser.setUserId(sysUser.getUserId());
            updateSysUser.setGoogleAuthSecretKey(googleAuthSecretKey);
            int count = userService.updateUserProfile(updateSysUser);
            if(count != 1) return AjaxResult.error("生成谷歌二维码失败");
        }
        String qrcode = GoogleAuthenticator.getQRBarcode("RedCarpet("+mobile+")", googleAuthSecretKey);
        String qrcodeUrl =  "/google_auth/qrcode_img_get?url=" + qrcode + "&width=200&height=200";
        log.info("生成谷歌绑定二维码userId={}, Url={}",userId, qrcodeUrl);
        return AjaxResult.success("success", qrcodeUrl);
    }

    /**
     * 绑定谷歌验证器
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "绑定谷歌验证器", businessType = BusinessType.UPDATE)
    @PostMapping("/bindGoogleCaptcha")
    public AjaxResult bindGoogleCaptcha(@RequestBody JSONObject jsonObject) {
        Long userId = jsonObject.getLong("userId");
        Long code = jsonObject.getLong( "googleCaptcha");
        if(!checkGoogleCode(userId, code)) return AjaxResult.error("绑定谷歌验证码失败，请重试");
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setGoogleAuthStatus(1);
        int count = userService.updateGoogleAuthStatus(sysUser);
        if(count != 1) return AjaxResult.error("绑定谷歌验证码失败，请重试");
        return AjaxResult.success();
    }

    /**
     * 验证谷歌验证码
     * @param userId
     * @param code
     * @return
     */
    boolean checkGoogleCode(Long userId, Long code) {
        SysUser sysUser = userService.selectUserById(userId);
        String googleAuthSecretKey = sysUser.getGoogleAuthSecretKey();
        return GoogleAuthenticator.checkGoogleCode(googleAuthSecretKey, code);
    }
}