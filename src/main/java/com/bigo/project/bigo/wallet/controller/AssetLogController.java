package com.bigo.project.bigo.wallet.controller;

import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @description: 后台资产查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/assetlog")
public class AssetLogController extends BaseController {

    @Autowired
    private IAssetLogService assetLogService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private IWithdrawService withdrawService;

    @PreAuthorize("@ss.hasPermi('bigo:assetLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(AssetLogEntity log, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            log.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<AssetLogEntity> list = assetLogService.listAssetLogByEntity(log);
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('bigo:assetLog:totalForm')")
    @GetMapping("/getTotalForm")
    public AjaxResult getTotalForm(WithdrawEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }

        if(StringUtils.isNotEmpty(entity.getBeginTime())) {
            entity.setBeginTime(entity.getBeginTime()+" 00:00:00");
        }
        if(StringUtils.isNotEmpty(entity.getEndTime())) {
            entity.setEndTime(entity.getEndTime()+" 23:59:59");
        }
        Map<String, BigDecimal> map = withdrawService.getTotalForm(entity);
        return AjaxResult.success(map);
    }



    /**
     * 资金明细导出
     */
    @PreAuthorize("@ss.hasPermi('bigo:assetLog:export')")
    @Log(title = "导出", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(AssetLogEntity log)
    {
        startPage(1,99999);
        List<AssetLogEntity> list = assetLogService.listAssetLogByEntity(log);
        ExcelUtil<AssetLogEntity> util = new ExcelUtil<AssetLogEntity>(AssetLogEntity.class);
        return util.exportExcel(list, "AssetLog");
    }
}
