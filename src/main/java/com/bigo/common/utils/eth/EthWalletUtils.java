package com.bigo.common.utils.eth;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/22 23:11
 */
@Slf4j
public class EthWalletUtils {

    private static final String URI = "http://127.0.0.1:3000";
//    private static final String URI = "http://198.11.181.130:3000";


    public static void main(String[] args) {
        try {
            System.out.println(getGasPrice(436L));
            submitCollection(436L, new BigDecimal(114));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject submitCollection(Long addressId, BigDecimal gasPrice) throws IOException {
        log.info("addressId = {},提交归集，gasPrice = {}", addressId, gasPrice);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"id\":\""+addressId+"\",\n    \"gas_price\":\""+gasPrice+"\"\n}");
        Request request = new Request.Builder()
                .url(URI + "/sendTo")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute(); //发送交易接口 id
        String bodyStr = response.body().string();
        log.info("手动归集responseBody = {}", bodyStr);
        response.close();
        JSONObject jsonObject = JSONObject.parseObject(bodyStr);
        log.info("addressId = {},提交归集,结果：{}", addressId, bodyStr);
        if(!"0".equals(jsonObject.getString("ret_code"))) {
            return null;
        }
        return jsonObject;
    }


    public static JSONObject getGasPrice(Long addressId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"id\":\""+addressId+"\"\n}");
        Request request = new Request.Builder()
                .url(URI + "/detail")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println();
        String bodyStr = response.body().string();
        response.close();
        JSONObject jsonObject = JSONObject.parseObject(bodyStr);
        if(!"0".equals(jsonObject.getString("ret_code"))) {
            return null;
        }
        return jsonObject;//购买金额
    }


}
