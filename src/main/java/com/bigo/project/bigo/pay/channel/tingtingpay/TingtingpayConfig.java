package com.bigo.project.bigo.pay.channel.tingtingpay;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * xm支付
 */
@Data
public class TingtingpayConfig {

    public final static String CHANNEL_NAME  = "tingtingpay";


    public static final String RETURN_VALUE_SUCCESS = "success";
    public static final String RETURN_VALUE_FAIL = "FAIL";

    //商户ID
    private String mchNo;

    //应用id
    private String appId;

    //银行id
    private String bankId;

    private String secretKeyId;

    private String secretKey;

    // 支付Key
    private String key;
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

    private String currency;

    private Long rate;

    public TingtingpayConfig(String payParam) {
        Assert.notNull(payParam, "init tingtingpay config error");
        JSONObject object = JSONObject.parseObject(payParam);
        this.mchNo = object.getString("mchNo");
        this.appId = object.getString("appId");
        this.bankId = object.getString("bankId");
        this.secretKeyId = object.getString("secretKeyId");
        this.secretKey = object.getString("secretKey");
        this.key = object.getString("key");
        this.reqPayUrl = object.getString("reqPayUrl");
        this.notifyPayUrl = object.getString("notifyPayUrl");
        this.transKey = object.getString("transKey");
        this.reqTransUrl = object.getString("reqTransUrl");
        this.notifyTransUrl = object.getString("notifyTransUrl");
        this.queryPayUrl = object.getString("queryPayUrl");
        this.queryTransUrl = object.getString("queryTransUrl");
        this.currency = object.getString("currency");
        this.rate = object.getLong("rate");
    }

}
