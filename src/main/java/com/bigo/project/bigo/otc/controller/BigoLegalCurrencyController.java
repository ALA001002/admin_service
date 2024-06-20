package com.bigo.project.bigo.otc.controller;

import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.api.domain.OrderParam;
import com.bigo.project.bigo.consts.BigoConsts;
import com.bigo.project.bigo.enums.OrderStatusEnum;
import com.bigo.project.bigo.otc.domain.LegalCurrency;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import com.bigo.project.bigo.otc.entity.OrderEntity;
import com.bigo.project.bigo.otc.service.IAppealService;
import com.bigo.project.bigo.otc.service.ILegalCurrencyService;
import com.bigo.project.bigo.otc.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @description: OTC法币 controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/legalCurrency")
public class BigoLegalCurrencyController extends BaseController {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private ILegalCurrencyService legalCurrencyService;



    @PreAuthorize("@ss.hasPermi('otc:legal:list')")
    @GetMapping("/list")
    public TableDataInfo orderList(LegalCurrency entity)
    {
        startPage();
        List<LegalCurrency> list = legalCurrencyService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 新增法币
     */
    @PreAuthorize("@ss.hasPermi('otc:legal:add')")
    @Log(title = "新增法币", businessType = BusinessType.CHANGE_STATUS)
    @PostMapping("/addLegal")
    public AjaxResult addLegal(@Validated @RequestBody LegalCurrency entity, HttpServletRequest request)
    {
        LoginUser user = tokenService.getLoginUser(request);
        if(entity.getStatus() > 1){
            return AjaxResult.error("参数错误!");
        }
        entity.setCreatorId(user.getUser().getUserId());
        int num = legalCurrencyService.insert(entity);
        if(num == 1){
            return AjaxResult.success();
        }else{
            return AjaxResult.error("保存法币信息失败！");
        }
    }

    /**
     * 删除法币
     */
    @PreAuthorize("@ss.hasPermi('otc:legal:del')")
    @Log(title = "删除法币", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(legalCurrencyService.deleteById(id));
    }

    /**
     * 更改法币信息
     */
    @PreAuthorize("@ss.hasPermi('otc:legal:update')")
    @Log(title = "修改法币信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateLegal")
    public AjaxResult updateLegal(@RequestBody LegalCurrency entity, HttpServletRequest request)
    {
        LoginUser user = tokenService.getLoginUser(request);
        if(entity.getId() == null || entity.getStatus() == null || entity.getStatus() > 1){
            return AjaxResult.error("参数错误!");
        }
        entity.setOperatorId(user.getUser().getUserId());
        entity.setUpdateTime(new Date());
        int num = legalCurrencyService.update(entity);
        if(num == 1){
            return AjaxResult.success();
        }else{
            return AjaxResult.error("保存法币信息失败！");
        }

    }




}
