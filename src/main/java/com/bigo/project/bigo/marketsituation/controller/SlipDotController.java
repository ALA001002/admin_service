package com.bigo.project.bigo.marketsituation.controller;

import com.bigo.common.utils.SecurityUtils;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.marketsituation.entity.SlipDot;
import com.bigo.project.bigo.marketsituation.service.ISlipDotService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 滑点controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/slipdot")
public class SlipDotController extends BaseController {

    @Autowired
    private ISlipDotService slipDotService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("@ss.hasPermi('bigo:slipdot:list')")
    @GetMapping("/list")
    public TableDataInfo list(SlipDot entity)
    {
        startPage();
        List<SlipDot> list = slipDotService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('bigo:slipdot:add')")
    @Log(title = "新增滑点", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody SlipDot dot, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        dot.setCreatorId(loginUser.getUser().getUserId());
        slipDotService.addSlipDot(dot);
        return AjaxResult.success();
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('bigo:slipdot:del')")
    @Log(title = "删除滑点", businessType = BusinessType.DELETE)
    @PutMapping("/del")
    public AjaxResult del(@RequestBody SlipDot dot)
    {
        if(dot.getId() == null){
            return AjaxResult.error("请选择要删除的滑点");
        }
        SlipDot slipDot = slipDotService.getById(dot.getId());
        if(slipDot == null){
            return AjaxResult.error("滑点不存在");
        }
        if(slipDot.getStatus() != 0){
            return AjaxResult.error("只能删除就绪的滑点");
        }
        slipDotService.deleteLogical(dot.getId());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('bigo:slipdot:start')")
    @Log(title = "开始滑点", businessType = BusinessType.START_SLIP_DOT)
    @PutMapping("/startSlipDot")
    public AjaxResult startSlipDot(@RequestBody SlipDot entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SlipDot dot = slipDotService.getById(entity.getId());
        if(dot == null){
            return AjaxResult.error("滑点不存在");
        }
        if(dot.getStatus() != 0){
            return AjaxResult.error("滑点不是就绪状态，无法开始");
        }
        dot.setOperateId(loginUser.getUser().getUserId());
        slipDotService.startSlipDot(dot);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('bigo:slipdot:stop')")
    @Log(title = "停止滑点", businessType = BusinessType.STOP_SLIP_DOT)
    @PutMapping("/stopSlipDot")
    public AjaxResult stopSlipDot(@RequestBody SlipDot entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SlipDot dot = slipDotService.getById(entity.getId());
        if(dot == null){
            return AjaxResult.error("滑点不存在");
        }
        if(dot.getStatus() != 1){
            return AjaxResult.error("滑点不是运行状态，无法停止");
        }
        dot.setOperateId(loginUser.getUser().getUserId());
        slipDotService.stopSlipDot(dot);
        return AjaxResult.success();
    }
}
