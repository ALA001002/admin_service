package com.bigo.project.monitor.controller;


import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bigo.common.constant.Constants;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.monitor.domain.SysUserOnline;
import com.bigo.project.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 * 
 * @author bigo
 */
@Slf4j
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName)
    {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = null;
            try {
                user = redisCache.getCacheObject(key);
            }catch (ClassCastException e){
                log.error("获取在线用户信息失败，key:{}",key);
            }
            if(user == null) continue;
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            }
            else if (StringUtils.isNotEmpty(userName) )
            {
                if (StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        userOnlineList.sort(Comparator.comparing(SysUserOnline::getLoginTime));
        Collections.reverse(userOnlineList);
        return getDataTable(userOnlineList);
    }

    public List<SysUserOnline> allOnline(String ipaddr, String userName) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = null;
            try {
                user = redisCache.getCacheObject(key);
            }catch (ClassCastException e){
                log.error("获取在线用户信息失败，key:{}",key);
            }
            if(user == null) continue;
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            }
            else if (StringUtils.isNotEmpty(userName) )
            {
                if (StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        userOnlineList.sort(Comparator.comparing(SysUserOnline::getLoginTime));
        Collections.reverse(userOnlineList);
        return userOnlineList;
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
        return AjaxResult.success();
    }

    /**
     * 强退所有用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:offlineAll')")
    @Log(title = "清退所有在线用户", businessType = BusinessType.DELETE)
    @DeleteMapping("offlineAll")
    public AjaxResult offlineAll() {
        List<SysUserOnline> userOnlineList = allOnline(null, null);
        for (SysUserOnline userOnline : userOnlineList) {
            redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + userOnline.getTokenId());
        }
        return AjaxResult.success();
    }
}
