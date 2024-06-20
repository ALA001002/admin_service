package com.bigo.common.utils.usd;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DictUtils;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @description: USD(美金) 价格行情
 * @author: wenxm
 * @date: 2021/1/12 10:04
 */
public class UsdPriceUtil {

    private static final String apiKey = "4c06a4c2-289a-4f8b-832d-5aeb8ea5655c";
    private static final String uri = "https://pro-api.coinmarketcap.com/v2/tools/price-conversion";
//    private static final String apiKey = DictUtils.getDictValue("market_config", "apiKey");
//    private static final String uri = DictUtils.getDictValue("market_config", "uri");

    public static void main(String[] args) {

        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        paratmers.add(new BasicNameValuePair("start","3"));
        paratmers.add(new BasicNameValuePair("limit","1"));
        paratmers.add(new BasicNameValuePair("convert_id","1,2781"));

        try {
           UsdResultInfo info = makeAPICall();
            System.out.println(info.getPrice());
        } catch (IOException e) {
            System.out.println("Error: cannont access content - " + e.toString());
        } catch (URISyntaxException e) {
            System.out.println("Error: Invalid URL " + e.toString());
        }

    }

    public static UsdResultInfo makeAPICall()
            throws URISyntaxException, IOException {


        List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
        paratmers.add(new BasicNameValuePair("amount","1"));
        paratmers.add(new BasicNameValuePair("id","1"));
        paratmers.add(new BasicNameValuePair("symbol","BTC"));

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = df.format(new Date());
        paratmers.add(new BasicNameValuePair("time",timestamp));
        paratmers.add(new BasicNameValuePair("convert_id","1,2781"));


        String response_content = "";

        URIBuilder query = new URIBuilder(uri);
        query.addParameters(paratmers);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        CloseableHttpResponse response = client.execute(request);
        UsdResultInfo usdResultInfo = null;
        try {
//            System.out.println(response.getStatusLine());
            if(response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            Gson gson = new Gson();
            usdResultInfo = gson.fromJson(response_content, UsdResultInfo.class);
        } finally {
            response.close();
        }
        return usdResultInfo;
    }
}
