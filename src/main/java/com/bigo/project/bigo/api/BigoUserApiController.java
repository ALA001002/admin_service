package com.bigo.project.bigo.api;

import com.bigo.common.enums.UserStatus;
import com.bigo.common.exception.file.FileSizeLimitExceededException;
import com.bigo.common.utils.*;
import com.bigo.common.utils.file.FileUploadUtils;
import com.bigo.framework.config.RuoYiConfig;
import com.bigo.framework.redis.RedisCache;
import com.bigo.project.bigo.api.vo.CountryInfoVO;
import com.bigo.project.bigo.captcha.service.CaptchaService;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginBody;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.BigoLoginService;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.RegisterInfo;
import com.bigo.project.bigo.api.domain.UserUpdateInfo;
import com.bigo.project.bigo.api.vo.BigoUserVO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.country.service.ICountryService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class BigoUserApiController {


    @Autowired
    private BigoLoginService loginService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ICountryService countryService;

    /**
     * 用户注册
     */
    @Log(title = "用户注册", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    public AjaxResult register(@Validated @RequestBody RegisterInfo regInfo){
        String registerPhone = regInfo.getPhone();
        String registerEmail = regInfo.getEmail();
        //判断是否开启注册
        String openRegister = DictUtils.getDictValue("sys_normal_disable", "is_open_register");
        if(!"1".equals(openRegister)) {
            return AjaxResult.error("Registration_function_is_not_open");
        }

        if((StringUtils.isEmpty(registerPhone) && StringUtils.isEmpty(registerEmail))) {
            log.error("注册失败，用户账号是空值！！");
            return AjaxResult.error("to_account_does_not_exist");
        }
        if(StringUtils.isNotEmpty(registerEmail)) registerEmail = registerEmail.trim();
        BigoUser bigoUser = new BigoUser();
        String lockUserNo = "";

        if(StringUtils.isNotEmpty(registerPhone)) {
            registerPhone = regInfo.getPhone().trim();
            if (bigoUserService.isPhoneExist(registerPhone)) {
                return AjaxResult.error("this_phone_number_has_been_registered");
            }
            if(ConfigSettingUtil.getSmsRegisterStatus() == 0) {   //是否开启手机号注册
                return AjaxResult.error("feature_service_is_closed");
            }
            lockUserNo = registerPhone;
        }else{
            if (bigoUserService.isEmailExist(registerEmail)) {
                return AjaxResult.error("this_email_has_been_registered");
            }
            if(ConfigSettingUtil.getEmailRegisterStatus() == 0) {   //是否开启邮箱注册
                return AjaxResult.error("feature_service_is_closed");
            }
            lockUserNo = registerEmail;
        }

        // 邀请码必填
        if(StringUtils.isEmpty(regInfo.getInvitationCode())) {
            return AjaxResult.error("invitation_code_not_exist");
        }
        //判断邀请码是否存在
        String invitationCode = regInfo.getInvitationCode().replace(" ","");
        BigoUser invitationUser = bigoUserService.getUserByUid(Long.valueOf(invitationCode));
        if (!validateInviteCode(invitationCode) || invitationUser == null) {
            return AjaxResult.error("invitation_code_not_exist");
        }

        bigoUser.setParentUid(Long.valueOf(regInfo.getInvitationCode()));
        bigoUser.setTopUid(invitationUser.getTopUid() == null ? invitationUser.getUid() : invitationUser.getTopUid());


        if(!captchaService.verifyCaptcha(registerPhone, registerEmail, regInfo.getCaptcha())){
            return AjaxResult.error("captcha_has_expired");
        }

        Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent("register_" + lockUserNo, "", 10, TimeUnit.SECONDS);
        if(!locked) {
            return AjaxResult.error("please_try_again_later");
        }
        bigoUser.setPhone(regInfo.getPhone());
        bigoUser.setEmail(registerEmail);
        bigoUser.setPassword(SecurityUtils.encryptPassword(regInfo.getPassword()));
        bigoUser.setAreaId(regInfo.getAreaId());
        bigoUserService.register(bigoUser);
        stringRedisTemplate.delete("register_" + registerEmail);
        return AjaxResult.success("register_success");
    }

    @Log(title = "BIGO用户登录", businessType = BusinessType.LOGIN)
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody){
        // 生成令牌
        String token = loginService.login(loginBody.getPhone(), loginBody.getEmail(),loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        return AjaxResult.success("success", token);
    }

    @GetMapping("/getUserInfo")
    public AjaxResult getUserInfo(){
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        //刷新用户信息
        refreshUserInfo(ServletUtils.getRequest(),loginUser.getBigoUser().getUid());
        BigoUser user = bigoUserService.getUserByUid(loginUser.getBigoUser().getUid());
        BigoUserVO userVO = new BigoUserVO();
        BeanUtils.copyProperties(user, userVO);
        if(StringUtils.isEmpty(user.getPayPassword())){
            userVO.setPayPwdStatus(0);
        }else{
            userVO.setPayPwdStatus(1);
        }
//        userVO.setPercent(bgUserDayBalanceService.getUserBalance(user.getUid()));
        return AjaxResult.success(userVO);
    }

    /**
     * 修改个人资料
     * @param updateInfo 修改内容
     * @return
     */
    @PostMapping("/modifyPersonalInfo")
    public AjaxResult modifyPersonalInfo(@RequestBody UserUpdateInfo updateInfo, HttpServletRequest request){
        // 生成令牌
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        BigoUser oldUser = loginUser.getBigoUser();
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(oldUser.getUid());
        Boolean hasChanged = Boolean.FALSE;
        //修改昵称
        if(updateInfo.getNickName() != null && !updateInfo.getNickName().equals(oldUser.getNickName())){
            updateUser.setNickName(updateInfo.getNickName());
            hasChanged = Boolean.TRUE;
        }
        //修改性别
        if(updateInfo.getSex() != null && !updateInfo.getSex().equals(oldUser.getSex())){
            updateUser.setSex(updateInfo.getSex());
            hasChanged = Boolean.TRUE;
        }
        //修改个人简介
        if(updateInfo.getProfile() != null && !updateInfo.getProfile().equals(oldUser.getProfile())){
            updateUser.setProfile(updateInfo.getProfile());
            hasChanged = Boolean.TRUE;
        }
        //修改联系方式
        if(updateInfo.getContractInfo() != null && !updateInfo.getContractInfo().equals(oldUser.getContractInfo())){
            updateUser.setContractInfo(updateInfo.getContractInfo());
            hasChanged = Boolean.TRUE;
        }
        //修改所在地区
        if(updateInfo.getAreaId() != null && !updateInfo.getAreaId().equals(oldUser.getAreaId())){
            updateUser.setAreaId(updateInfo.getAreaId());
            hasChanged = Boolean.TRUE;
        }
        //补充邀请人
        if(StringUtils.isNotEmpty(updateInfo.getInvitationCode())){
            if(oldUser.getParentUid() != null){
                return AjaxResult.error("invitation_code_cannot_be_modified");
            }
            //判断邀请码是否存在
            if (!validateInviteCode(updateInfo.getInvitationCode())
                    || !bigoUserService.isInviteCodeExist(Long.valueOf(updateInfo.getInvitationCode()))) {
                return AjaxResult.error("invitation_code_not_exist");
            }
            if(updateInfo.getInvitationCode().equals(oldUser.getUid().toString())){
                return AjaxResult.error("cannot_invite_yourself");
            }
            BigoUser parentUser = bigoUserService.getUserByUid(Long.valueOf(updateInfo.getInvitationCode()));
            if(!parentUser.getRegisterTime().before(oldUser.getRegisterTime())){
                return AjaxResult.error("the_inviting_person_is_illegal");
            }
            updateUser.setParentUid(Long.valueOf(updateInfo.getInvitationCode()));
            hasChanged = Boolean.TRUE;
        }
        if(hasChanged){
            bigoUserService.updateUser(updateUser);
            //刷新缓存
            refreshUserInfo(request, oldUser.getUid());
        }
        return AjaxResult.success();
    }

    /**
     * 发送验证码
     */
    @PostMapping("/sendCaptcha")
    public AjaxResult sendCaptcha(@RequestParam(value = "type",defaultValue = "1")int type) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        String sendTo = null;
        String code = RandomNumberUtils.genCaptcha();
        if(StringUtils.isNotEmpty(user.getPhone())){
            captchaService.sendCaptcha(user.getPhone(), code);
            sendTo = PrivacyUtils.maskMobile(user.getPhone());
        }else if(StringUtils.isNotEmpty(user.getEmail())){
            captchaService.gmailSender(user.getEmail(), code, type);
            sendTo = PrivacyUtils.maskEmail(user.getEmail());
        }
        return AjaxResult.success("success", sendTo);
    }

    /**
     * 修改密码
     */
    @PostMapping("/modifyPwd")
    public AjaxResult modifyPayPwd(@RequestBody RegisterInfo info, HttpServletRequest request) {
        BigoUser user = tokenService.getBigoUser(request);
        if(StringUtils.isEmpty(info.getCaptcha())){
            return AjaxResult.error("captcha_cannot_be_null");
        }
//        if(StringUtils.isNotEmpty(info.getPassword()) && !captchaService.verifyCaptcha(user.getPhone(), user.getEmail(), info.getCaptcha())){
//            return AjaxResult.error("captcha_has_expired");
//        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus().toString())) {
            log.info("登录用户：{} 已被禁用.", user.getUid());
            return AjaxResult.error("account_is_disabled");
        }
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(user.getUid());
        Boolean changeFlag = false;
        if(StringUtils.isNotEmpty(info.getPassword())){
            updateUser.setPassword(SecurityUtils.encryptPassword(info.getPassword()));
            changeFlag = true;
        }
        if(StringUtils.isNotEmpty(info.getPayPassword())){
            updateUser.setPayPassword(SecurityUtils.encryptPassword(info.getPayPassword()));
            changeFlag = true;
        }
        if(changeFlag){
            bigoUserService.updateUser(updateUser);
            //刷新缓存
            refreshUserInfo(request, user.getUid());
        }
        return AjaxResult.success();
    }

    /**
     * 上传头像
     */
    @PostMapping("/uploadAvatar")
    public AjaxResult uploadAvatar(MultipartFile file, HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(request);
        if(file == null){
            return AjaxResult.error("file_cannot_be_null");
        }
        if(!FileUploadUtils.isImage(file)){
            return AjaxResult.error("only_picture_can_be_uploaded");
        }
        try{
            if(file.getSize() > FileUploadUtils.AUTH_IMG_MAX_SIZE){
                return AjaxResult.error("single_image_size_cannot_be_greater_than_10MB");
            }
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = RuoYiConfig.getFileUrl() + fileName;
            BigoUser updateUser = new BigoUser();
            updateUser.setUid(user.getUid());
            updateUser.setAvatar(url);
            bigoUserService.updateUser(updateUser);
            refreshUserInfo(request, user.getUid());
            return AjaxResult.success();
        }catch (Exception e){
            log.error("用户：{} 上传头像失败",user.getUid(),e);
            return AjaxResult.error("image_upload_failed");
        }
    }

    /**
     * 上传身份认证图片
     */
    @PostMapping("/uploadAuth")
    public AjaxResult uploadPhoto(MultipartFile[] files,
                                  @RequestParam String realName,
                                  @RequestParam(required = false) String country,
                                  @RequestParam(required = false) String driverLicense,
                                  @RequestParam(required = false) Integer authType,
                                  @RequestParam(required = false) String idNum,
                                  @RequestParam(required = false) String passport,
                                  HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(request);
        user = bigoUserService.getUserByUid(user.getUid());
        if(user.getAuthStatus() != 0 && user.getAuthStatus() != 3){
            return AjaxResult.error("authentication_is_not_possible_at_this_time");
        }
        if(files.length < 1){
            return AjaxResult.error("files_cannot_be_null");
        }
        List<String> authImgList = new ArrayList<>();
        try{
            for(int i=0; i<files.length; i++){
                if(files[i].getSize() > FileUploadUtils.AUTH_IMG_MAX_SIZE){
                    return AjaxResult.error("single_image_size_cannot_be_greater_than_10MB");
                }
                // 上传文件路径
                String filePath = RuoYiConfig.getUploadPath();
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, files[i]);
                String url = RuoYiConfig.getFileUrl() + fileName;
                authImgList.add(url);
            }
            BigoUser updateUser = new BigoUser();
            updateUser.setUid(user.getUid());
            updateUser.setAuthStatus(1);
            updateUser.setAuthPhotos(StringUtils.join(authImgList, ";"));
            updateUser.setRealName(realName);
            updateUser.setIdNum(idNum);
            updateUser.setPassport(passport);
            updateUser.setCountry(country);
            updateUser.setDriverLicense(driverLicense);
            updateUser.setAuthType(authType);
            bigoUserService.updateUser(updateUser);
            refreshUserInfo(request, user.getUid());
            return AjaxResult.success();
        }catch (Exception e){
            log.error("用户：{} 上传身份认证图片失败",user.getUid(),e);
            return AjaxResult.error("image_upload_failed");
        }
    }

    @PostMapping("/logout")
    public AjaxResult logout(HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(request);
        //删除用户登录信息
        Object tokenObject = redisCache.getCacheObject("bigouser_"+user.getUid());
        if(tokenObject != null) {
            String userKey = tokenService.getUserKey(tokenObject.toString());
            tokenService.delLoginUser(userKey);
        }
        return AjaxResult.success();
    }

    /**
     * 验证邀请码
     * @param code
     * @return
     */
    private Boolean validateInviteCode(String code){
        Pattern pattern = Pattern.compile("^\\d{6}$");
        return pattern.matcher(code).matches();
    }

    private void refreshUserInfo(HttpServletRequest request, Long uid){
        //刷新缓存
        BigoUser newUser = bigoUserService.getUserByUid(uid);
        LoginUser loginUser = tokenService.getLoginUser(request);
        loginUser.setBigoUser(newUser);
        tokenService.refreshToken(loginUser);
    }

}
