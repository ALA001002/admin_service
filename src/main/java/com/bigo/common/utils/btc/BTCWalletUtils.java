package com.bigo.common.utils.btc;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.CoinUtils;
import com.google.gson.Gson;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class BTCWalletUtils {

    private final static String USER = "bitcoinrpc";


    private  static String URL;

    private  static String PASS;

    @Value("${config.btcService}")
    private  String btcService;


    @Value("${config.btcPassword}")
    private  String btcPassword;


    @PostConstruct
    public void init() {
        URL = btcService;
        PASS = btcPassword;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getNewAddress("cccc"));
//        System.out.println(getTransactionTxid("e72ea518026b2d37797692b68fea0d0fa45b0c561a1c9f41a9e8bcaca049dcc3"));
//        System.out.println(sendToAddress("2NE9iz7qSkHAbFi22wfuDZ3z9yUvAUK2oWM",new BigDecimal(0.2).setScale(8, BigDecimal.ROUND_HALF_UP),"asd", "aaa"));
//        long count = getBlockCount();
//        System.out.println(listSinceBlock(getBlockHash(667216)));
//        System.out.println(getReceivedByAddress("2NE9iz7qSkHAbFi22wfuDZ3z9yUvAUK2oWM"));
//        System.out.println(getNewAddress("456111"));
    }

    public static String getAuthorization(String user,String pass){
        String auth = String.format("%s:%s",user,pass);
        System.out.println(auth);
        return String.format("Basic %s", Base64.encode(auth.getBytes(StandardCharsets.UTF_8)));
    }

    public static String getAuthorization(){
        return getAuthorization(USER, PASS);
    }

    //获取新地址
    /**
     *
     * @param label//获取新地址
     */
    public static String getNewAddress(String label) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"method\":\"getnewaddress\",\"params\":[\""+label+"\"]\n}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        Map<String,String> map = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return map.get("result");
    }

    //转账
    public static String sendToAddress(String address, BigDecimal num,String remark,String personName) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"jsonrpc\": \"1.0\",\n    \"id\": \"curltest\",\n    \"method\": \"sendtoaddress\",\n    \"params\": [\n        \""+address+"\",\n        "+num+",\n        \""+remark+"\",\n        \""+personName+"\"\n    ,true]\n}");
        Response response = client.newCall(builder(body)).execute();
        response.close();
        if(response.code() != 200 && !response.message().equals("OK")) {
            return "error";
        }
        return "success";
    }

    public static Request builder(RequestBody body){
        String authorization = getAuthorization();
        System.out.println(authorization);
        return new Request.Builder()
                .url(URL)
                .method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    //获取钱包余额
    public static Object getBalance() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n    \"method\":\"getbalance\"\n}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        Map<String,Object> map = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return map.get("result");
    }

    //获取钱包余额
    public static Object getReceivedByAddress(String address) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"getreceivedbyaddress\",\"params\":[\""+address+"\"]}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        Map<String,Object> map = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return map.get("result");
    }

    class ResultItem{
        String address;
        BigDecimal amount;
    }
    class ResultObject {
        List<ResultItem> result;
        String error;
        String id;
    }
    public static Object listReceivedByAddress(String address) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"listreceivedbyaddress\",\"params\":[6,false,false,\""+address+"\"]}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        ResultObject resultObject = gson.fromJson(response.body().string(), ResultObject.class);
        response.close();
        return resultObject.result;
    }

    //获取交易记录
    public static Object getTransactionAddress(String address) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"gettransaction\",\"params\":[\""+address+"\"]}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        Map resultObject = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return resultObject.get("result");
    }

    public static List<TransactionItem> listSinceBlock(String blockhash) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"listsinceblock\",\"params\":[\""+blockhash+"\",6,true]}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
//        JSONObject jsonObject = gson.fromJson(response.body().string(), JSONObject.class);
        TransactionResult resultObject = gson.fromJson(response.body().string(), TransactionResult.class);
        response.close();
        return resultObject.getResult().getTransactions();
    }

    class BlockResult{
        long result;
    }
    public static long getBlockCount() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"getblockcount\"}");
        Response response = client.newCall(builder(body)).execute();
        Gson gson = new Gson();
        BlockResult resultObject = gson.fromJson(response.body().string(), BlockResult.class);
        response.close();
        return resultObject.result;
    }

    public static String getBlockHash(long height) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"getblockhash\",\"params\":["+height+"]}");
        Response response = client.newCall(builder(body)).execute();
//        System.out.println(response.body().string());
        Gson gson = new Gson();
        Map resultObject = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return (String)resultObject.get("result");
    }

    //获取交易记录
    public static Object getTransactionTxid(String txid) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"method\":\"gettransaction\",\"params\":[\""+txid+"\"]}");
        Response response = client.newCall(builder(body)).execute();
//        System.out.println(response.body().string());
        Gson gson = new Gson();
        Map resultObject = gson.fromJson(response.body().string(), Map.class);
        response.close();
        return resultObject.get("result");
    }
}
