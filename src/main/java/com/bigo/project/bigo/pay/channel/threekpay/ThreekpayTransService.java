package com.bigo.project.bigo.pay.channel.threekpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.TransOrder;
import com.bigo.project.bigo.pay.entity.BaseTrans;
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
public class ThreekpayTransService extends BaseTrans {



    @Override
    public String getChannelName() {
        return ThreekpayConfig.CHANNEL_NAME;
    }

    public JSONObject trans(TransOrder transOrder) {
        ThreekpayConfig config = new ThreekpayConfig(getTransParam(transOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
        String transOrderId = transOrder.getTransOrderId();
        paramMap.put("merchant_id", config.getMerchantId());
        paramMap.put("order_id", transOrderId);
        paramMap.put("amount", transOrder.getCurrencyAmount().toString());
        paramMap.put("bank_code", transOrder.getBankNumber());
        paramMap.put("receive_name", transOrder.getAccountName());
        paramMap.put("receive_account", transOrder.getAccountNo());
        paramMap.put("notify_url", config.getNotifyTransUrl());
        String signValue = PayDigestUtil.getSign(paramMap,config.getTransKey()).toLowerCase();
        log.info("Sign Result:" + signValue);
        paramMap.put("sign", signValue);

        try {
            JSONObject params = paramMap;
            log.info("[{}]请求数据:{}", getChannelName(), params);
            String reqUrl = config.getReqTransUrl();
            log.info("[{}]请求URL:{}", getChannelName(), reqUrl);
            String result = OkHttpUtil.postJsonParams(reqUrl, paramMap.toJSONString());
            log.info("[{}]返回数据:{}", getChannelName(), result);
            if (StringUtils.isBlank(result)) {
                log.error("[{}]代付上游通道返回空订单号",getChannelName());
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retCode", "FAIL");
                return retObj;
            }
            JSONObject resObj = JSONObject.parseObject(result);
            Integer status = resObj.getInteger("status");
            if(0 == status) {
//                String tradeNo = resObj.getString("tradeNo");
                retObj.put("channelOrderId", "");
                retObj.put("transOrderId", transOrder.getTransOrderId()); // 设置支付订单ID
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
