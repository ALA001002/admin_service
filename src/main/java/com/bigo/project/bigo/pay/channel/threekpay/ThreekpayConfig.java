package com.bigo.project.bigo.pay.channel.threekpay;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * xm支付
 */
@Data
public class ThreekpayConfig {

    public final static String CHANNEL_NAME  = "threekpay";


    public static final String RETURN_VALUE_SUCCESS = "SUCCESS";
    public static final String RETURN_VALUE_FAIL = "FAIL";

    //商户ID
    private String merchantId;

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
    private String queryPayUrl;
    private String queryTransUrl;

    public ThreekpayConfig(String payParam) {
        Assert.notNull(payParam, "init threekpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.merchantId = object.getString("merchantId");
        this.payKey = object.getString("payKey");
        this.reqPayUrl = object.getString("reqPayUrl");
        this.notifyPayUrl = object.getString("notifyPayUrl");
        this.transKey = object.getString("transKey");
        this.reqTransUrl = object.getString("reqTransUrl");
        this.notifyTransUrl = object.getString("notifyTransUrl");
        this.queryPayUrl = object.getString("queryPayUrl");
        this.queryTransUrl = object.getString("queryTransUrl");
    }

}
