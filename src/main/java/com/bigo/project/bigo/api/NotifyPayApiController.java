package com.bigo.project.bigo.api;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.PayConstant;
import com.bigo.common.utils.SpringUtil;
import com.bigo.project.bigo.pay.entity.PayNotifyInterface;
import com.bigo.project.bigo.pay.entity.TransNotifyInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/22 2:29
 */
@Slf4j
@RestController
@RequestMapping("/api/notify")
public class NotifyPayApiController {

    PayNotifyInterface payNotifyInterface;

    TransNotifyInterface transNotifyInterface;
    /**
     * 支付渠道后台通知响应
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/doPay/{channel}")
    public String payNotifyRes(HttpServletRequest request, @PathVariable("channel") String channel) throws ServletException, IOException {
        log.info("====== 开始接收{}支付回调通知 ======", channel);
        // 回调IP白名单校验
/*		JSONObject whiteListObj = checkIpWhiteList(channel, request);
		int code = whiteListObj.getInteger("code");
		if(code != 1){
			String errorMsg = whiteListObj.getString(PayConstant.RESPONSE_RESULT);
			_log.info("响应给{}: {}", channel, errorMsg);
			_log.info("====== 完成接收{}支付回调通知 ======", channel);
			return errorMsg;
		}*/
        try {
            payNotifyInterface = (PayNotifyInterface) SpringUtil.getBean(channel.toLowerCase() +  "PayNotifyService");
        }catch (BeansException e) {
            log.error(e.getMessage());
            return "Fail";
        }
        JSONObject retObj = payNotifyInterface.doNotify(request);
        String notifyRes = retObj.getString(PayConstant.RESPONSE_RESULT);
        log.info("响应给{}:{}", channel, notifyRes);
        log.info("====== 完成接收{}支付回调通知 ======", channel);
        return notifyRes;
    }

    /**
     * 支付渠道后台通知响应
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("/trans/{channel}")
    public String transNotifyRes(HttpServletRequest request, @PathVariable("channel") String channel) throws ServletException, IOException {
        log.info("====== 开始接收{}支付回调通知 ======", channel);
        // 回调IP白名单校验
/*		JSONObject whiteListObj = checkIpWhiteList(channel, request);
		int code = whiteListObj.getInteger("code");
		if(code != 1){
			String errorMsg = whiteListObj.getString(PayConstant.RESPONSE_RESULT);
			_log.info("响应给{}: {}", channel, errorMsg);
			_log.info("====== 完成接收{}支付回调通知 ======", channel);
			return errorMsg;
		}*/
        try {
            transNotifyInterface = (TransNotifyInterface) SpringUtil.getBean(channel.toLowerCase() +  "TransNotifyService");
        }catch (BeansException e) {
            log.error(e.getMessage());
            return "Fail";
        }
        JSONObject retObj = transNotifyInterface.doNotify(request);
        String notifyRes = retObj.getString(PayConstant.RESPONSE_RESULT);
        log.info("响应给{}:{}", channel, notifyRes);
        log.info("====== 完成接收{}代付回调通知 ======", channel);
        return notifyRes;
    }
}
