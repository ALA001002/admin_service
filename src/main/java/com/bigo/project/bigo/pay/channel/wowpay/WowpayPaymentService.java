package com.bigo.project.bigo.pay.channel.wowpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.domain.TransOrder;
import com.bigo.project.bigo.pay.entity.BasePayment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
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
public class WowpayPaymentService extends BasePayment {



    @Override
    public String getChannelName() {
        return WowpayConfig.CHANNEL_NAME;
    }

    public JSONObject pay(PayOrder payOrder) {
        WowpayConfig config = new WowpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();

//        BigDecimal amount = new BigDecimal(payOrder.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
        String payOrderId = payOrder.getPayOrderId();
        paramMap.put("version", "1.0");
        paramMap.put("mch_id", config.getMchId());
        paramMap.put("notify_url", config.getNotifyPayUrl());
        paramMap.put("mch_order_no", payOrderId);
        paramMap.put("pay_type", 772);
        paramMap.put("trade_amount", payOrder.getCurrencyAmount());
        paramMap.put("order_date", DateUtils.getTime());
        paramMap.put("goods_name", "bycoin");
//        String timeStr = String.valueOf(System.currentTimeMillis());
//        timeStr = timeStr.substring(0,timeStr.length()-3);

        String signValue = PayDigestUtil.getSign(paramMap,config.getPayKey()).toLowerCase();
//        log.info("Sign Before MD5:" + signValue);
//        signValue = PayDigestUtil.md5(signValue,"utf-8").toUpperCase();
        log.info("Sign Result:" + signValue);

        paramMap.put("sign", signValue);
        paramMap.put("sign_type", "MD5");

        try {
            JSONObject params = paramMap;
            log.info("[{}]请求数据:{}", getChannelName(), params);
            String reqUrl = config.getReqPayUrl();
            log.info("[{}]请求URL:{}", getChannelName(), reqUrl);
            String result = OkHttpUtil.post(reqUrl, paramMap);
            log.info("[{}]返回数据:{}", getChannelName(), result);
            if (StringUtils.isBlank(result)) {
                log.error("[{}]上游通道返回空订单号",getChannelName());
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retCode", "FAIL");
                return retObj;
            }
            log.info(result);
            JSONObject resObj = JSONObject.parseObject(result);
            String respCode = resObj.getString("respCode");
            if("SUCCESS".equals(respCode)) {
                String payUrl = resObj.getString("payInfo");
                int updateCount = payOrderService.updateStatusIng(payOrder.getPayOrderId());
                log.info("[{}]更新订单状态为支付中:payOrderId={},payId={},result={}", getChannelName(), payOrder.getPayOrderId(), null, updateCount);
                retObj.put("payUrl", payUrl);
                retObj.put("payOrderId", payOrder.getPayOrderId()); // 设置支付订单ID
                retObj.put("errDes", "");
                retObj.put("retCode", "SUCCESS");
                return retObj;
            }else {
                String msg = resObj.getString("msg");
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retMsg", msg);
                retObj.put("retCode", "FAIL");
                return retObj;
            }
        }catch (Exception e) {
            retObj.put("retMsg", e.getMessage());
            retObj.put("errDes", "upstream_channel_returns_empty");
            retObj.put("retCode", "FAIL");
            return retObj;
        }
    }
}
