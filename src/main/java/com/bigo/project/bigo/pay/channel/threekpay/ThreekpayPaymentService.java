package com.bigo.project.bigo.pay.channel.threekpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.entity.BasePayment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 17:54
 */
@Slf4j
@Service
public class ThreekpayPaymentService extends BasePayment {



    @Override
    public String getChannelName() {
        return ThreekpayConfig.CHANNEL_NAME;
    }

    public JSONObject pay(PayOrder payOrder) {
        ThreekpayConfig config = new ThreekpayConfig(getPayParam(payOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();

//        BigDecimal amount = new BigDecimal(payOrder.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP);
        String payOrderId = payOrder.getPayOrderId();
        paramMap.put("merchant_id", config.getMerchantId());
        paramMap.put("pay_type", 101);
        paramMap.put("order_id", payOrderId);
        paramMap.put("remark", "by");
        paramMap.put("amount", payOrder.getCurrencyAmount().toString());
        paramMap.put("notify_url", config.getNotifyPayUrl());

        String signValue = PayDigestUtil.getSign(paramMap,config.getPayKey()).toLowerCase();
        log.info("Sign Result:" + signValue);
        paramMap.put("sign", signValue);

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
            Integer status = resObj.getInteger("status");
            if(status == 0) {
                resObj = resObj.getJSONObject("data");
                String payUrl = resObj.getString("pay_url");
                int updateCount = payOrderService.updateStatusIng(payOrder.getPayOrderId());
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
            retObj.put("retMsg", e.getMessage());
            retObj.put("errDes", "upstream_channel_returns_empty");
            retObj.put("retCode", "FAIL");
            return retObj;
        }
    }
}
