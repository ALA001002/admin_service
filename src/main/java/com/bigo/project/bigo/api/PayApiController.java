package com.bigo.project.bigo.api;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.PayConstant;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.SpringUtil;
import com.bigo.common.utils.ip.IpUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.dto.PayOrderDTO;
import com.bigo.project.bigo.api.vo.BankInfoVO;
import com.bigo.project.bigo.api.vo.PayOrderVO;
import com.bigo.project.bigo.api.vo.PayPassageVO;
import com.bigo.project.bigo.api.vo.TransOrderVO;
import com.bigo.project.bigo.pay.domain.*;
import com.bigo.project.bigo.pay.entity.PaymentInterface;
import com.bigo.project.bigo.pay.service.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 16:07
 */
@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayApiController extends BaseController {

    private PaymentInterface paymentInterface;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private ITransOrderService transOrderService;

    @Autowired
    private IPayPassageService payPassageService;

    @Autowired
    private IPayInterfaceTypeService payInterfaceTypeService;

    @Autowired
    private IBankInfoService bankInfoService;

    /**
     * 可用通道列表
     * @return
     */
    @GetMapping("/passageList")
    public AjaxResult passageList() {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        PayPassage payPassage = new PayPassage();
        payPassage.setStatus(1);
        List<PayPassage> payPassageList = payPassageService.selectPayPassageList(payPassage);
        List<PayPassageVO> voList = new ArrayList<>();
        if(payPassageList != null && payPassageList.size() > 0) {
            PayPassageVO vo = null;
            for (PayPassage passage : payPassageList) {
                vo = new PayPassageVO();
                BeanUtils.copyProperties(passage, vo);
                voList.add(vo);
            }
        }
        return AjaxResult.success(voList);
    }

    /**
     * 充值订单记录
     * @return
     */
    @GetMapping("/orderList")
    public AjaxResult orderList(@RequestParam(value = "pageNo") Integer pageNo,
                                @RequestParam("pageSize") Integer pageSize) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        PayOrder param = new PayOrder();
        param.setUid(user.getUid());
        startPage(pageNo,pageSize);
        List<PayOrder> payOrderList = payOrderService.selectPayOrderList(param);
        List<PayOrderVO> voList = new ArrayList<>();
        if(payOrderList != null && payOrderList.size() > 0) {
            PayOrderVO vo = null;
            for (PayOrder payOrder : payOrderList) {
                vo = new PayOrderVO();
                BeanUtils.copyProperties(payOrder, vo);
                voList.add(vo);
            }
        }
        return AjaxResult.success(voList);
    }

    /**
     * 统一下单
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/createOrder")
    public AjaxResult createOrder(@RequestBody PayOrderDTO dto, HttpServletRequest request) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        log.info("###### 开始接收商户统一下单请求 ######");
        String logPrefix = "【统一下单】";
        JSONObject payContext = new JSONObject();
        PayOrder payOrder = null;
        try {
            // 验证参数有效性
            Object object = validateParams(user, dto, payContext, request);
            if (object instanceof String) {
                log.info("{}参数校验不通过:{}", logPrefix, object);
                return AjaxResult.error(object.toString());  // 支付中心下单失败
            }
            if (object instanceof PayOrder) payOrder = (PayOrder) object;
            if (payOrder == null) return AjaxResult.error("failed_to_place_order_at_payment_center");  // 支付中心下单失败

            // 获取对应支付通道，下单获取支付链接
            PayPassage payPassage = payPassageService.selectPayPassageById(payOrder.getPayPassageId());
            if (payPassage == null) return AjaxResult.error("failed_to_call_payment_channel");  // 支付中心下单失败
            PayInterfaceType interfaceType = payInterfaceTypeService.selectPayInterfaceTypeById(payPassage.getIfTypeCode());
            if (interfaceType == null) return AjaxResult.error("failed_to_call_payment_channel");  // 支付中心下单失败

            //创建订单
            payOrder.setCurrencyAmount(new BigDecimal(payOrder.getAmount()).multiply(new BigDecimal(20)).setScale(2, BigDecimal.ROUND_HALF_UP));
            int result = payOrderService.insertPayOrder(payOrder);
            log.info("{}创建支付订单,结果:{}", logPrefix, result);
            if (result != 1) {
                return AjaxResult.error("failed_to_place_order_at_payment_center");  // 支付中心下单失败
            }
            try {
                paymentInterface = (PaymentInterface) SpringUtil.getBean(interfaceType.getIfTypeCode().toLowerCase() + "PaymentService");
            } catch (BeansException e) {
                log.error(e.getMessage());
                return AjaxResult.error("failed_to_call_payment_channel");  //调用支付渠道失败
            }
            JSONObject retObj = paymentInterface.pay(payOrder);
//            JSONObject retObj = new JSONObject();
            log.info("retCode = {}", retObj.get(PayConstant.RETURN_PARAM_RETCODE));
            if (retObj.get(PayConstant.RETURN_PARAM_RETCODE).equals(PayConstant.RETURN_VALUE_SUCCESS)) {
                // 成功，返回支付链接
                return AjaxResult.success(retObj);
            } else {
                return AjaxResult.error(retObj.getString("errDes"));  //调用支付渠道失败
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error("payment_is_abnormal_please_contact_customer_service"); //支付异常，请联系客服
        }
    }

    private Object validateParams(BigoUser user, PayOrderDTO dto, JSONObject payContext, HttpServletRequest request) {

        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        Long uid = user.getUid();   //用户ID
        Long amount = dto.getAmount();
        Long payPassageId = dto.getPayPassageId();
        //校验
        PayPassage payPassage = payPassageService.selectPayPassageById(payPassageId);
        if (payPassage == null){
            errorMessage = "failed_to_call_payment_channel";   //调用支付渠道失败
            return errorMessage;
        }
        if (amount <= 0) {
            errorMessage = "abnormal_payment_amount";   // 支付金额异常
            return errorMessage;
        }
        if(amount < payPassage.getMinEveryAmount() || amount > payPassage.getMaxEveryAmount()) {
            errorMessage = "abnormal_payment_amount";   // 支付金额异常
            return errorMessage;
        }
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(getPayOrderId());
        payOrder.setUid(uid);
        payOrder.setAmount(amount);
        payOrder.setCurrency("USD");
        payOrder.setStatus(0);
        payOrder.setPayPassageId(payPassageId);
        getPosition(payOrder, request);
        return payOrder;

    }

    //

    public static String getPayOrderId(){
        Date date = new Date();
        SimpleDateFormat sdformat = new SimpleDateFormat("HHmmssSSS");
        String randomString = RandomStringUtils.randomNumeric(8);
        String id = sdformat.format(date);
        return "PAY"+ id + randomString;
    }

    private void getPosition(PayOrder order, HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        log.info("支付订单下单IP：{}", ip);
        order.setClientIp(ip);
        if(ip.contains(",")) {
            String ips[] = ip.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : ips) {
                String address = IpUtils.getAddress(s.trim());
                String position = IpUtils.getPosition(address);
                sb.append(position).append(",");
            }
            order.setIpAddress(sb.substring(0, sb.length()-1).toString());
        } else {
            String address = IpUtils.getAddress(ip.trim());
            order.setIpAddress(IpUtils.getPosition(address));
        }
    }


    /**
     * 银行列表
     * @return
     */
/*    @GetMapping("/banCodeList")
    public AjaxResult banCodeList() {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<BankInfo> bankInfoList = bankInfoService.selectBankInfoList(new BankInfo());
        List<BankInfoVO> bankInfoVOList = new ArrayList<>();
        for (BankInfo bankInfo : bankInfoList) {
            BankInfoVO vo = new BankInfoVO();
            BeanUtils.copyProperties(bankInfo, vo);
            bankInfoVOList.add(vo);
        }
        return AjaxResult.success(bankInfoVOList);
    }*/

    /**
     * 银行列表
     * @return
     */
    @GetMapping("/transHistory")
    public AjaxResult transHistory(@RequestParam(value = "pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize){

        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        TransOrder param = new TransOrder();
        param.setUid(user.getUid());
        startPage(pageNo, pageSize);
        List<TransOrder> transOrderList = transOrderService.selectTransOrderList(param);
        List<TransOrderVO> voList = new ArrayList<>();
        for (TransOrder transOrder : transOrderList) {
            TransOrderVO vo = new TransOrderVO();
            BeanUtils.copyProperties(transOrder, vo);
            voList.add(vo);
        }
        return AjaxResult.success(voList);
    }

}
