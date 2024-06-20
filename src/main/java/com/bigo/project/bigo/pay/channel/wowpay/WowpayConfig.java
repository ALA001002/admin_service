package com.bigo.project.bigo.pay.channel.wowpay;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * xm支付
 */
@Data
public class WowpayConfig {

    public final static String CHANNEL_NAME  = "wowpay";


    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "fail";

    //商户ID
    private String mchId;

    // 支付Key
    private String payKey;
    // 支付请求地址
    private String reqPayUrl;
    // 支付回调地址
    private String notifyPayUrl;

    // 商户代付Key
    private String transKey;
    // 代付请求地址
    private String reqTransUrl;
    // 代付回调地址
    private String notifyTransUrl;

    // 查询地址
    private String queryUrl;

    public WowpayConfig(String payParam) {
        Assert.notNull(payParam, "init wowpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.mchId = object.getString("mchId");
        this.payKey = object.getString("payKey");
        this.reqPayUrl = object.getString("reqPayUrl");
        this.notifyPayUrl = object.getString("notifyPayUrl");
        this.transKey = object.getString("transKey");
        this.reqTransUrl = object.getString("reqTransUrl");
        this.notifyTransUrl = object.getString("notifyTransUrl");
    }

}
