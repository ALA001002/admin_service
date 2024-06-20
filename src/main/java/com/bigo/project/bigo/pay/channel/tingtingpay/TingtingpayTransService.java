package com.bigo.project.bigo.pay.channel.tingtingpay;

import com.alibaba.fastjson.JSONObject;
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
public class TingtingpayTransService extends BaseTrans {



    @Override
    public String getChannelName() {
        return TingtingpayConfig.CHANNEL_NAME;
    }

    public JSONObject trans(TransOrder transOrder) {
        TingtingpayConfig config = new TingtingpayConfig(getTransParam(transOrder));
        Long currencyAmount = transOrder.getAmount().multiply(new BigDecimal(config.getRate())).longValue();

        JSONObject retObj = new JSONObject();
        JSONObject paramMap = new JSONObject();
        String transOrderId = transOrder.getTransOrderId();
        paramMap.put("mchNo", config.getMchNo());
        paramMap.put("appId", config.getAppId());
        paramMap.put("mchOrderNo", transOrderId); //商户订单号
        paramMap.put("ifCode", "tingtingpay");
        paramMap.put("entryType", "BANK_CARD");
        paramMap.put("amount", currencyAmount);
        paramMap.put("currency", "VND");
        paramMap.put("accountNo", transOrder.getAccountNo());
        paramMap.put("accountName", transOrder.getAccountName());
        paramMap.put("notifyUrl", config.getNotifyTransUrl());
        paramMap.put("reqTime", System.currentTimeMillis());
        paramMap.put("version", "1.0");
        paramMap.put("transferDesc","trans");

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
            String reqUrl = config.getReqTransUrl();
            log.info("[{}]请求代付URL:{}", getChannelName(), reqUrl);
            String result = OkHttpUtil.postJsonParams(reqUrl, paramMap.toJSONString());
            log.info("[{}]返回数据:{}", getChannelName(), result);
            if (StringUtils.isBlank(result)) {
                log.error("[{}]代付上游通道返回空订单号",getChannelName());
                retObj.put("errDes", "upstream_channel_returns_empty");
                retObj.put("retCode", "FAIL");
                return retObj;
            }
            JSONObject resObj = JSONObject.parseObject(result);
            Integer code = resObj.getInteger("code");
            String msg = resObj.getString("msg");
            if(0 == code ) {
                resObj = resObj.getJSONObject("data");
                Integer state = resObj.getInteger("state");
                if (state == 1 || state == 2) {
                    String transferId = resObj.getString("transferId");
                    //代付成功，更新订单真实货币金额
                    TransOrder updateOrder = new TransOrder();
                    updateOrder.setId(transOrder.getId());
                    updateOrder.setCurrency("VND");
                    updateOrder.setCurrencyAmount(new BigDecimal(currencyAmount));
                    transOrderService.updateTransOrder(updateOrder);
                    retObj.put("channelOrderId", transferId);
                    retObj.put("transOrderId", transOrder.getTransOrderId()); // 设置支付订单ID
                    retObj.put("errDes", "");
                    retObj.put("retCode", "SUCCESS");
                    return retObj;
                }else {
                    retObj.put("errDes", "upstream_channel_returns_empty");
                    retObj.put("retMsg", msg);
                    retObj.put("retCode", "FAIL");
                    return retObj;
                }
            }else {
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
