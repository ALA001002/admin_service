package com.bigo.common.utils;

import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 **/
public class OkHttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String get(String url, Map<String, String> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder()
                .url(sb.toString())
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = getUnsafeOkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }else {
                logger.error("okhttp3 get:url={} close error", url);
            }
        }
        return responseBody;
    }


    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, Object> params) {
        String responseBody = "";
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                String var = params.get(key)+"";
                builder.add(key, var);
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient =getUnsafeOkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }else{
                logger.warn("http code:"+status);
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            } else {
                logger.info("okhttp3 post:url={} close error", url);
            }
        }
        return responseBody;
    }



    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> header) {
        String responseBody = "";
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                String var = params.get(key)+"";
                builder.add(key, var);
            }
        }
        Headers headers = Headers.of(header);
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .headers(headers)
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient =getUnsafeOkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }else{
                logger.warn("http code:"+status);
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            } else {
                logger.error("okhttp3 post:url={} close error", url);
            }
        }
        return responseBody;
    }

    /**
     * get
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    public static String getForHeader(String url, Map<String, String> queries) {
        String responseBody = "";
        StringBuffer sb = new StringBuffer(url);
        if (queries != null && queries.keySet().size() > 0) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        Request request = new Request.Builder()
                .addHeader("key", "value")
                .url(sb.toString())
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 put error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            } else {
                logger.error("okhttp3 getForHeader:url={} close error", url);
            }
        }
        return responseBody;
    }

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public static String postJsonParams(String url, String jsonParams) {
        String responseBody = "";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }else {
                logger.error("okhttp3 postJsonParams:url={} close error", url);
            }
        }
        return responseBody;
    }


    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public static String postJsonParams(String url, String jsonParams, String headName, String headValue) {
        String responseBody = "";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .header(headName,headValue)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            } else{
                logger.info("okhttp3 postJsonParams:url={} close error", url);
            }
        }
        return responseBody;
    }


    /**
     * Post请求发送xml数据....
     * 参数一：请求Url
     * 参数二：请求的xmlString
     * 参数三：请求回调
     */
    public static String postXmlParams(String url, String xml) {
        String responseBody = "";
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"), xml);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            logger.error("okhttp3 post error >> ex = {}", ExceptionUtils.getStackTrace(e));
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseBody;
    }

    public static void main(String[] args) {
      /*  String url = "https://weixin110.qq.com/cgi-bin/mmspamsupport-bin/newredirectconfirmcgi?main_type=2&evil_type=20&source=2&url=";
        Response response = null;
        try {
            String purl = URLEncoder.encode("http://xx.adbto.cn/index.php?url=https://m3u8.91panzy.com/HweC5K9L.m3u8", "utf-8");
            String purl1 = URLEncoder.encode("http://jj.so18gos.cn/index.php?url=https://m3u8.91panzy.com/HweC5K9L.m3u8", "utf-8");
//            url = url + purl + "&exportkey=AzeujlY176G777U8sJ08Zrs%3D&pass_ticket=5xgX7w3jckEq2YO9AaHvbDbju3Xw7DnOeABSyzog7MiIzB1IiSb4eywxj7GArjqb&wechat_real_lang=zh_CN";
            url = url + purl1;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent","Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255")
                .addHeader("Referer", "https://mp.weixin.qq.com")
                .get()
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            int status = response.code();
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("type", type);                                         // 支付方式,wechat:微信,alipay:支付宝
        paramMap.put("money", 100000);                        // 单位分
        paramMap.put("thirdPartyId","r51");               // 订单号
        paramMap.put("sign", "4C8703649AA0F6E4C08BA6A76055DE80");
        Map map = paramMap;
        System.out.println(map.toString());
        JSONObject retObj = new JSONObject();
        try{
            String result = OkHttpUtil.post("https://139.159.213.191/order/create", map);
            System.out.println(result);

            if(StringUtils.isBlank(result)) {
                retObj.put("errDes", "上游通道返回空!");
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            }
            JSONObject resObj = JSONObject.parseObject(result);
            Integer code = resObj.getInteger("code");
            if(code == 200) {
                resObj = resObj.getJSONObject("data");
                String payUrl = resObj.getString("payUrl");     // 二维码URL
                String payId = resObj.getString("payId");       // 上游通道订单号
                JSONObject payParams = new JSONObject();
                // 扫码支付
                StringBuffer payForm = new StringBuffer();
                String action = payUrl;
                payForm.append("<form action=\"" + action + "\" method=\"post\">");
                payForm.append("<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >");
                payForm.append("</form>");
                payForm.append("<script>document.forms[0].submit();</script>");
                payParams.put("payUrl", payForm);       // 支付链接地址
                payParams.put("payMethod", PayConstant.PAY_METHOD_FORM_JUMP);
                retObj.put("payParams", payParams);
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
            }else {
                retObj.put("errDes", "操作失败!");
                retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
            }
        } catch (Exception e) {
            retObj.put("errDes", "操作失败!");
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_FAIL);
        }

*/
        IntStream.range(0,10).filter(i -> i% 2 ==0).forEach(System.out::print);;
    }

}
