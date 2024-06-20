package com.bigo.project.bigo.api;


import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.OrderParam;
import com.bigo.project.bigo.api.vo.OrderVO;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.OrderStatusEnum;
import com.bigo.project.bigo.otc.domain.Order;
import com.bigo.project.bigo.otc.domain.Payment;
import com.bigo.project.bigo.otc.service.IOrderService;
import com.bigo.project.bigo.otc.service.IPaymentService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 币高OTC api
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@RestController
@RequestMapping("/api/otc")
public class OtcApiController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IOrderService orderService;


    /**
     * 收款方式列表
     */
    @GetMapping("/getPayment")
    public AjaxResult getPayment(@RequestParam(value = "id", required = false) Long id){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(id != null){
            Payment payment = paymentService.getById(id);
            return AjaxResult.success(payment);
        }else {
            List<Payment> resultList = paymentService.listByUid(user.getUid());
            return AjaxResult.success(resultList);
        }
    }

    /**
     * 添加收款方式
     * @param payment
     * @return
     */
    @PostMapping("/addPayment")
    public AjaxResult addPayment(@Validated @RequestBody Payment payment){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        payment.setUid(user.getUid());
        Payment oldPayment = paymentService.getByUidAndLegal(payment.getUid(),payment.getLegalCurrency());
        if(oldPayment != null){
            return AjaxResult.error("only_one_payment_can_be_added");
        }
        paymentService.insert(payment);
        return AjaxResult.success();
    }

    /**
     * 修改收款方式
     * @param payment
     * @return
     */
    @PostMapping("/modifyPayment")
    public AjaxResult modifyPayment(@Validated @RequestBody Payment payment){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(payment.getId() == null){
            return AjaxResult.error("payment_id_cannot_be_empty");
        }
        Payment oldPayment = paymentService.getById(payment.getId());
        if(oldPayment == null){
            return AjaxResult.error("payment_not_exist");
        }
        if(!oldPayment.getUid().equals(user.getUid())){
            return AjaxResult.error("payment_cannot_be_modified");
        }
        payment.setLegalCurrency(oldPayment.getLegalCurrency());
        paymentService.update(payment);
        return AjaxResult.success();
    }

    /**
     * otc下单
     * @param param
     * @return
     */
    @PostMapping("/order")
    public AjaxResult order(@Validated @RequestBody OrderParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(param.getType() != 1 && param.getType() != 2){
            return AjaxResult.error("unknown_trade_type");
        }
        if(!param.getCoin().equals(CurrencyEnum.USDT.getCode())){
            return AjaxResult.error("unsupported_coin");
        }
        //未实名认证无法进行otc交易
        /*if(user.getAuthStatus() != 2){
            return AjaxResult.error("please_complete_real_name_authentication_first");
        }*/
        Order order = new Order();
        order.setUid(user.getUid());
        List<Order> orderList = orderService.listByParam(order);
        if(existOpenOrder(orderList)){
            return AjaxResult.error("there_are_currently_open_orders");
        }
        BigDecimal minNum = CoinUtils.getOtcMinNum(param.getCoin());
        if(param.getNum().compareTo(minNum) < 0){
            return AjaxResult.error("illegal_trade_quantity");
        }
        param.setUid(user.getUid());
        Long orderId = orderService.order(param);
        return AjaxResult.success(orderId);
    }

    /**
     * 获取订单列表
     * @param
     * @return
     */
    @GetMapping("/myOrderList")
    public AjaxResult myOrderList(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        Order param = new Order();
        param.setUid(user.getUid());
        List<Order> orderList = orderService.listByParam(param);
        List<OrderVO> voList = new ArrayList<>();
        for(Order order : orderList){
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            voList.add(orderVO);
        }
        return AjaxResult.success(voList);
    }

    /**
     * 获取订单详情
     * @param
     * @return
     */
    @GetMapping("/getOrder")
    public AjaxResult getOrder(@RequestParam("id") Long id, @RequestParam("payType") Integer payType){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        Order order = new Order();
        order.setId(id);
        order.setPayType(payType);
        order = orderService.getOrder(order);
        if(order != null && order.getSellerId().equals(user.getUid())
            || order.getBuyerId().equals(user.getUid())){
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            return AjaxResult.success(orderVO);
        }
        return AjaxResult.success(null);
    }

    /**
     * 撤销otc订单
     * @param param
     * @return
     */
    @PostMapping("/revokeOrder")
    public AjaxResult revokeOrder(@RequestBody OrderParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(param.getOrderId() == null) {
            return AjaxResult.error("order_id_cannot_be_empty");
        }
        param.setUid(user.getUid());
        orderService.revokeOrder(param);
        return AjaxResult.success();
    }

//    /**
//     * 确认已支付订单
//     * @param param
//     * @return
//     */
//    @PostMapping("/payOrder")
//    public AjaxResult payOrder(@RequestBody OrderParam param){
//        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
//        if(param.getOrderId() == null) {
//            return AjaxResult.error("order_id_cannot_be_empty");
//        }
//        param.setUid(user.getUid());
//        orderService.payOrder(param);
//        return AjaxResult.success();
//    }

    /**
     * 订单进行下一步操作
     * @param param
     * @return
     */
    @PostMapping("/nextStep")
    public AjaxResult nextStep(@RequestBody OrderParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(param.getOrderId() == null) {
            return AjaxResult.error("order_id_cannot_be_empty");
        }
        Order order = orderService.getById(param.getOrderId());
        if(order == null){
            return AjaxResult.error("order_is_not_exist");
        }
        param.setUid(user.getUid());
        if(order.getStatus().equals(OrderStatusEnum.OUTSTANDING.getType())){
            orderService.payOrder(param);
        }else if(order.getStatus().equals(OrderStatusEnum.UNCONFIRMED.getType())){
            orderService.confirmOrder(param);
        }else{
            return AjaxResult.error("the_operation_cannot_be_performed_at_this_time");
        }
        return AjaxResult.success();
    }

//    /**
//     * 确认已支付订单
//     * @param param
//     * @return
//     */
//    @PostMapping("/confirmOrder")
//    public AjaxResult confirmOrder(@RequestBody OrderParam param){
//        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
//        if(param.getOrderId() == null) {
//            return AjaxResult.error("order_id_cannot_be_empty");
//        }
//        param.setUid(user.getUid());
//        orderService.confirmOrder(param);
//        return AjaxResult.success();
//    }

    /**
     * 申诉订单
     * @param param
     * @return
     */
    @PostMapping("/appealOrder")
    public AjaxResult appealOrder(@RequestBody OrderParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(param.getOrderId() == null) {
            return AjaxResult.error("order_id_cannot_be_empty");
        }
        param.setUid(user.getUid());
        orderService.appealOrder(param);
        return AjaxResult.success();
    }

    /**
     * 判断是否有正在进行中的订单
     * @param orderList
     * @return
     */
    private Boolean existOpenOrder(List<Order> orderList){
        if(orderList.size() == 0){
            return Boolean.FALSE;
        }
        Order order = orderList.stream().filter(a->a.getStatus() < 2 || a.getStatus() == 4).findFirst().orElse(null);
        if(order != null){
            return Boolean.TRUE;
        }else{
            return Boolean.FALSE;
        }
    }


}