package com.bigo.framework.security.service;

import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import org.apache.tomcat.util.http.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.bigo.common.enums.UserStatus;
import com.bigo.common.exception.BaseException;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.security.LoginUser;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.bigo.common.utils.ServletUtils.getRequest;

/**
 * 用户验证处理
 *
 * @author bigo
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String uri = request.getRequestURI();
        if(uri.contains("/api/user/login")){
            //币高用户登录
            BigoUser user = bigoUserService.getUserByPhone(username);
            if (StringUtils.isNull(user)) {
                log.info("登录用户：{} 不存在.", username);
                throw new UsernameNotFoundException("mobile_number_has_not_been_registered");
            } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
                log.info("登录用户：{} 已被禁用.", username);
                throw new BaseException("account is disabled");
            }
            LoginUser loginUser = new LoginUser();
            loginUser.setBigoUser(user);
            return loginUser;
        }else {
            //系统用户登录
            SysUser user = userService.selectUserByUserName(username);
            if (StringUtils.isNull(user)) {
                log.info("登录用户：{} 不存在.", username);
                throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
            } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
                log.info("登录用户：{} 已被删除.", username);
                throw new BaseException("对不起，您的账号：" + username + " 已被删除");
            } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
                log.info("登录用户：{} 已被停用.", username);
                throw new BaseException("对不起，您的账号：" + username + " 已停用");
            }
            return createLoginUser(user);
        }
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user, permissionService.getMenuPermission(user));
    }
}
