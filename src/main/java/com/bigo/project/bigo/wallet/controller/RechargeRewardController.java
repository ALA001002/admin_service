package com.bigo.project.bigo.wallet.controller;

import java.util.List;

import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.project.bigo.wallet.domain.RechargeReward;
import com.bigo.project.bigo.wallet.service.IRechargeRewardService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 充值奖励Controller
 * 
 * @author xx
 * @date 2023-11-08
 */
@RestController
@RequestMapping("/reward/reward")
public class RechargeRewardController extends BaseController
{
    @Autowired
    private IRechargeRewardService rechargeRewardService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询充值奖励列表
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:list')")
    @GetMapping("/list")
    public TableDataInfo list(RechargeReward rechargeReward)
    {
        startPage();
        List<RechargeReward> list = rechargeRewardService.selectRechargeRewardList(rechargeReward);
        return getDataTable(list);
    }

    /**
     * 导出充值奖励列表
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:export')")
    @Log(title = "充值奖励", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(RechargeReward rechargeReward)
    {
        List<RechargeReward> list = rechargeRewardService.selectRechargeRewardList(rechargeReward);
        ExcelUtil<RechargeReward> util = new ExcelUtil<RechargeReward>(RechargeReward.class);
        return util.exportExcel(list, "reward");
    }

    /**
     * 获取充值奖励详细信息
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(rechargeRewardService.selectRechargeRewardById(id));
    }

    /**
     * 新增充值奖励
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:add')")
    @Log(title = "充值奖励", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody RechargeReward rechargeReward)
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (rechargeReward.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), rechargeReward.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        return toAjax(rechargeRewardService.insertRechargeReward(rechargeReward));
    }

    /**
     * 修改充值奖励
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:edit')")
    @Log(title = "充值奖励", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody RechargeReward rechargeReward)
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (rechargeReward.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), rechargeReward.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        return toAjax(rechargeRewardService.updateRechargeReward(rechargeReward));
    }

    /**
     * 删除充值奖励
     */
    @PreAuthorize("@ss.hasPermi('reward:reward:remove')")
    @Log(title = "充值奖励", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(rechargeRewardService.deleteRechargeRewardByIds(ids));
    }
}
