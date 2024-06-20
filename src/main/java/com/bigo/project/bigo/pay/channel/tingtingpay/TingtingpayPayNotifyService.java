package com.bigo.project.bigo.pay.channel.tingtingpay;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.PayConstant;
import com.bigo.common.utils.PayDigestUtil;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.entity.BasePayNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 19:12
 */
@Slf4j
@Service
public class TingtingpayPayNotifyService extends BasePayNotify {

    @Override
    public String getChannelName() {
        return TingtingpayConfig.CHANNEL_NAME;
    }

    @Override
    public JSONObject doNotify(Object notifyData) {
        String logPrefix = "【TingTing支付回调】";
        log.info("====== 开始处理TingTing支付回调通知 ======");
        HttpServletRequest request = (HttpServletRequest) notifyData;
        JSONObject retObj = buildRetObj();
        Map<String, Object> payContext = new HashMap();
        PayOrder payOrder;
        String respString = TingtingpayConfig.RETURN_VALUE_FAIL;
        try {
            JSONObject paramsObject = getJson(request);
            log.info("回调参数:{}",paramsObject);
            if(!verifyPayParams(payContext, paramsObject)) {
                retObj.put("resResult", TingtingpayConfig.RETURN_VALUE_FAIL);
                return retObj;
            }
            payOrder = (PayOrder) payContext.get("payOrder");
            // 处理订单
            Integer payStatus = payOrder.getStatus(); // 0：订单生成，1：支付中，-1：支付失败，2：支付成功，3：业务处理完成
            if (payStatus != 2 && payStatus != 3) {
                int updatePayOrderRows = payOrderService.updateStatusSuccess(payOrder);
                if (updatePayOrderRows != 1) {
                    log.error("{}更新支付状态失败,将payOrderId={},更新payStatus={}失败", logPrefix, payOrder.getPayOrderId(),"FAIL");
                    retObj.put(PayConstant.RESPONSE_RESULT, "处理订单失败");
                    return retObj;
                }
                log.info("{}更新支付状态成功,将payOrderId={},更新payStatus={}成功", logPrefix, payOrder.getPayOrderId(), PayConstant.PAY_STATUS_SUCCESS);
                payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
            }
            log.info("====== 完成处理TingTing支付回调 ======");
            respString = TingtingpayConfig.RETURN_VALUE_SUCCESS;
        }catch (Exception e) {
            log.error(e.getMessage());
            log.error(logPrefix + "处理异常");
            e.printStackTrace();
        }
        retObj.put("resResult", respString);
        return retObj;
    }

    public boolean verifyPayParams(Map<String, Object> payContext, JSONObject params) {
        // 校验结果是否成功PAYED
        String mchOrderNo = params.getString("mchOrderNo"); // 返回商户传入的订单号
        String amount = params.getString("amount");
        String state = params.getString("state");   //状态
        String sign = params.getString("sign");

        if(state == null || !state.equals("2")){
            log.error("=========TingTing支付回调state状态异常, order_status={}, 订单号={} ", state, mchOrderNo);
            payContext.put("retMsg", "支付回调order_status异常");
            return false;
        }

        // 查询payOrder记录
        String payOrderId = mchOrderNo;
        PayOrder payOrder = payOrderService.selectOrderId(payOrderId);
        if (payOrder == null) {
            log.error("Can't found payOrder form db. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "Can't found payOrder");
            return false;
        }
        TingtingpayConfig config = new TingtingpayConfig(getPayParam(payOrder));
        params.remove("sign");
        String signValue = PayDigestUtil.getSign(params, config.getKey()).toUpperCase();
        log.info("Sign Result:" + signValue);

        if(!signValue.equals(sign)) {
            log.error("验证签名失败. payOrderId={}, ", payOrderId);
            payContext.put("retMsg", "验证签名失败");
            return false;
        }
        // 核对金额
        BigDecimal payAmount = payOrder.getCurrencyAmount();
        BigDecimal resultAmount = new BigDecimal(amount);
        if (payAmount.compareTo(resultAmount) != 0) {
            log.error("金额不一致. usd_amount={},payOrderId={}", resultAmount, payOrderId);
            payContext.put("retMsg", "金额不一致");
            return false;
        }
/*
       // 查询订单，进行二次验证
        JSONObject queryParamMap = new JSONObject();
        queryParamMap.put("merchant_id", config.getMerchantId());
        queryParamMap.put("merchant_order_id", payOrderId);

        String querySignValue = PayDigestUtil.getSign(queryParamMap, config.getPayKey()).toLowerCase();
        queryParamMap.put("sign",querySignValue);

        log.info("[{}]查询订单请求数据:{}", getChannelName(), queryParamMap.toJSONString());
        String url = config.getQueryPayUrl();
        log.info("[{}]查询订单请求URL:{}", getChannelName(), url);
        String result = OkHttpUtil.postJsonParams(url, queryParamMap.toJSONString());
        log.info("[{}]查询订单返回数据:{}", getChannelName(), result);
        if (StringUtils.isBlank(result)) {
            log.error("查询回调订单失败，result={}, payOrderId={} ", result, payOrder.getPayOrderId());
            payContext.put("retMsg", "查询回调订单失败");
            return false;
        }
        JSONObject resObj = JSONObject.parseObject(result);
        Integer queryStatus = resObj.getInteger("status");
        if(queryStatus == 0){
            resObj = resObj.getJSONObject("data");
            int orderStatus = resObj.getInteger("order_status");
            if(orderStatus != 1) {
                log.error("处理回调订单失败，查询订单状态为：orderStatus={}, payOrderId={} ", orderStatus, payOrderId);
                payContext.put("retMsg", "处理回调订单失败");
                return false;
            }
        } else {
            String msg = resObj.getString("msg");
            log.error("处理回调订单失败，查询订单状态为:resultCode={}, Msg={}, payOrderId={}", queryStatus, msg,  payOrderId);
            return false;
        }*/
        payOrder.setChannelOrderId("");
        payOrder.setFee(BigDecimal.ZERO);
        payOrder.setTradeValue(BigDecimal.ZERO);
        payOrder.setSettValue(BigDecimal.ZERO);
        payContext.put("payOrder", payOrder);
        return true;
    }

/*    public static void main(String[] args) {
        String paramsStr = "amount=30.00&attach=&buy_currency=USDT&create_time=1634544288&fee_amount=0.00&fee_usd_amount=0.00&merchant_id=100520000157&order_id=TLAK442883936815706&order_status=1&out_order_id=08434972988919174&pay_currency=USD&settle_value=0.0000000&usd_amount=30.00&value=0.0000000&secret=3iwC0otqsOhzZttqbDqWsFHlW36qhzQE";
        log.info("Sign Before MD5:" + paramsStr);
        String signValue = PayDigestUtil.md5(paramsStr, "utf-8").toUpperCase();
        System.out.println(signValue);
    }*/

    private JSONObject getJson(HttpServletRequest request) throws IOException {
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null){
            responseStrBuilder.append(inputStr);
        }
        if(responseStrBuilder.length() > 0) {
            return JSONObject.parseObject(responseStrBuilder.toString());
        }
        JSONObject requestJson = new JSONObject();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] pv = request.getParameterValues(paramName);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pv.length; i++) {
                if (pv[i].length() > 0) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(pv[i]);
                }
            }
            requestJson.put(paramName, sb.toString());
        }
        return requestJson;
    }
}
