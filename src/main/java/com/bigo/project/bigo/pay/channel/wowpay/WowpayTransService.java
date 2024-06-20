package com.bigo.project.bigo.pay.channel.wowpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.domain.TransOrder;
import com.bigo.project.bigo.pay.entity.BasePayment;
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
public class WowpayTransService extends BaseTrans {



    @Override
    public String getChannelName() {
        return WowpayConfig.CHANNEL_NAME;
    }

    public JSONObject trans(TransOrder transOrder) {
        WowpayConfig config = new WowpayConfig(getTransParam(transOrder));
        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
        String transOrderId = transOrder.getTransOrderId();
        paramMap.put("mch_id", config.getMchId());
        paramMap.put("mch_transferId", transOrderId);
        paramMap.put("transfer_amount", transOrder.getCurrencyAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        paramMap.put("apply_date", DateUtils.getTime());
        paramMap.put("bank_code", transOrder.getBankCode());
        paramMap.put("receive_name", transOrder.getAccountName());
        paramMap.put("receive_account", transOrder.getAccountNo());
        paramMap.put("receiver_telephone", transOrder.getReceiverPhone());
        paramMap.put("back_url", config.getNotifyTransUrl());
        String signValue = PayDigestUtil.getSign(paramMap,config.getTransKey()).toLowerCase();
        log.info("Sign Result:" + signValue);

        paramMap.put("sign", signValue);
        paramMap.put("sign_type", "MD5");

        try {
            JSONObject params = paramMap;
            log.info("[{}]请求数据:{}", getChannelName(), params);
            String reqUrl = config.getReqTransUrl();
            log.info("[{}]请求URL:{}", getChannelName(), reqUrl);
            String result = OkHttpUtil.post(reqUrl, paramMap);
            log.info("[{}]返回数据:{}", getChannelName(), result);
            if (StringUtils.isBlank(result)) {
                log.error("[{}]代付上游通道返回空订单号",getChannelName());
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retCode", "FAIL");
                return retObj;
            }
            JSONObject resObj = JSONObject.parseObject(result);
            String respCode = resObj.getString("respCode");
            if("SUCCESS".equals(respCode)) {
                String tradeNo = resObj.getString("tradeNo");
                retObj.put("channelOrderId", tradeNo);
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
