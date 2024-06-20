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
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import com.bigo.project.bigo.otc.entity.OrderEntity;
import com.bigo.project.bigo.otc.service.IAppealService;
import com.bigo.project.bigo.otc.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @description: OTC订单 controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/otc")
public class BigoOtcController extends BaseController {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IAppealService appealService;

    @PreAuthorize("@ss.hasPermi('otc:order:list')")
    @GetMapping("/order/list")
    public TableDataInfo orderList(OrderEntity entity)
    {
        startPage();
        List<OrderEntity> list = orderService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 更改订单状态
     */
    @PreAuthorize("@ss.hasPermi('otc:order:change')")
    @Log(title = "标记已付款/确认已收款", businessType = BusinessType.CHANGE_STATUS)
    @PutMapping("/order/changeStatus")
    public AjaxResult changeOrderStatus(@RequestBody OrderEntity entity, HttpServletRequest request)
    {
        Order order = orderService.getById(entity.getId());
        if(order == null){
            return AjaxResult.error("订单不存在!");
        }
        OrderParam param = new OrderParam();
        param.setUid(BigoConsts.SYS_USER_ID);
        param.setOrderId(order.getId());
        if(order.getExpireTime().before(new Date())){
            return AjaxResult.error("订单已超时!");
        }
        if(entity.getStatus().equals(OrderStatusEnum.UNCONFIRMED.getType()) && order.getStatus().equals(OrderStatusEnum.OUTSTANDING.getType())){
            orderService.payOrder(param);
        }else if(entity.getStatus().equals(OrderStatusEnum.COMPLETE.getType()) && order.getStatus().equals(OrderStatusEnum.UNCONFIRMED.getType())){
            orderService.confirmOrder(param);
        }else if(entity.getStatus().equals(OrderStatusEnum.APPEALING.getType()) && order.getStatus().equals(OrderStatusEnum.UNCONFIRMED.getType())){
            orderService.appealOrder(param);
        }else{
            return AjaxResult.error("参数错误!");
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('otc:appeal:list')")
    @GetMapping("/appeal/list")
    public TableDataInfo appealList(AppealEntity entity)
    {
        startPage();
        List<AppealEntity> list = appealService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 审核申诉
     */
    @PreAuthorize("@ss.hasPermi('otc:appeal:change')")
    @Log(title = "申诉审核", businessType = BusinessType.CHANGE_STATUS)
    @PutMapping("/appeal/changeStatus")
    public AjaxResult changeAppealStatus(@RequestBody AppealEntity entity, HttpServletRequest request)
    {
        AppealEntity appeal = appealService.getById(entity.getId());
        if(appeal == null){
            return AjaxResult.error("申诉不存在!");
        }
        if(appeal.getStatus() != 0){
            return AjaxResult.error("该申诉已处理!");
        }
        LoginUser loginUser = tokenService.getLoginUser(request);
        appeal.setStatus(entity.getStatus());
        appeal.setOperatorId(loginUser.getUser().getUserId());
        if(entity.getStatus() == 1){
            appealService.passAppeal(appeal);
        }else if(entity.getStatus() == 2){
            appealService.rejectAppeal(appeal);
        }else {
            return AjaxResult.error("未知的操作!");
        }
        appealService.update(appeal);
        return AjaxResult.success();
    }



}
