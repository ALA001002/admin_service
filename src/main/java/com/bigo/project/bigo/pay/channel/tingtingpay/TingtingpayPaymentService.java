package com.bigo.project.bigo.pay.channel.tingtingpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.entity.BasePayment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 17:54
 */
@Slf4j
@Service
public class TingtingpayPaymentService extends BasePayment {



    @Override
    public String getChannelName() {
        return TingtingpayConfig.CHANNEL_NAME;
    }

    public JSONObject pay(PayOrder payOrder) {
        return null;
        /*TingtingpayConfig config = new TingtingpayConfig(getPayParam(payOrder));

        Long currencyAmount = payOrder.getAmount() * config.getRate();
        payOrder.setCurrencyAmount(new BigDecimal(currencyAmount));

        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();

        String payOrderId = payOrder.getPayOrderId();
        paramMap.put("mchNo", config.getMchNo());
        paramMap.put("appId", config.getAppId());
        paramMap.put("mchOrderNo", payOrderId);
        paramMap.put("wayCode", "TINGTING_B2B");
        paramMap.put("amount", currencyAmount);
        paramMap.put("currency", "VND");
        paramMap.put("subject", "Spot");
        paramMap.put("body", "Spot");
        paramMap.put("notifyUrl", config.getNotifyPayUrl());
        paramMap.put("reqTime", System.currentTimeMillis());
        paramMap.put("version", "1.0");

        JSONObject extParam = new JSONObject();
        extParam.put("bankId",config.getBankId());
        extParam.put("secretKeyId",config.getSecretKeyId());
        extParam.put("secretKey",config.getSecretKey());
        paramMap.put("extParam", extParam);

        String signValue = PayDigestUtil.getSign(paramMap,config.getKey()).toUpperCase();
        log.info("Sign Result:" + signValue);
        paramMap.put("sign", signValue);
        paramMap.put("signType", "MD5");

        try {
            JSONObject params = paramMap;
            log.info("[{}]请求数据:{}", getChannelName(), params);
            String reqUrl = config.getReqPayUrl();
            log.info("[{}]请求URL:{}", getChannelName(), reqUrl);
            String result = OkHttpUtil.postJsonParams(reqUrl, paramMap.toJSONString());
            log.info("[{}]返回数据:{}", getChannelName(), result);
            if (StringUtils.isBlank(result)) {
                log.error("[{}]上游通道返回空订单号",getChannelName());
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retCode", "FAIL");
                return retObj;
            }
            JSONObject resObj = JSONObject.parseObject(result);
            Integer code = resObj.getInteger("code");
            if(code == 0) {
                resObj = resObj.getJSONObject("data");
                String payUrl = resObj.getString("payData");
                int updateCount = payOrderService.updateStatusAndAmount(payOrder.getPayOrderId(), currencyAmount);
                log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), null, updateCount);
                retObj.put("payUrl", payUrl);
                retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
                retObj.put("errDes", "");
                retObj.put("retCode", "SUCCESS");
                return retObj;
            }else {
                String msg = resObj.getString("message");
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retMsg", msg);
                retObj.put("retCode", "FAIL");
                return retObj;
            }
        }catch (Exception e) {
            e.printStackTrace();
            retObj.put("retMsg", e.getMessage());
            retObj.put("errDes", "upstream_channel_returns_empty");
            retObj.put("retCode", "FAIL");
            return retObj;
        }*/
    }
}
