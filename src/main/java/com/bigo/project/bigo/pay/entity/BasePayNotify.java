package com.bigo.project.bigo.pay.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.exception.CustomException;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.domain.PayPassage;
import com.bigo.project.bigo.pay.service.IPayOrderService;
import com.bigo.project.bigo.pay.service.IPayPassageService;
import com.bigo.project.bigo.pay.vo.ParamVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 19:01
 */
public abstract class BasePayNotify extends BaseService implements PayNotifyInterface {
    @Autowired
    public IPayOrderService payOrderService;
    @Autowired
    public IPayPassageService payPassageService;

    public abstract String getChannelName();

    public JSONObject doNotify(Object notifyData) {
        return null;
    }

    /**
     * 获取三方支付配置信息
     * 如果是平台账户,则使用平台对应的配置,否则使用商户自己配置的渠道
     * @param payOrder
     * @return
     */
    public String getPayParam(PayOrder payOrder) {
        String payParam = "";
        PayPassage payPassage = payPassageService.selectPayPassageById(payOrder.getPayPassageId());
        if(payPassage != null && payPassage.getStatus() == 1) {
            payParam = payPassage.getParam();
        }
        if(StringUtils.isBlank(payParam)) {
            throw new CustomException("order_failed_please_contact_customer_service");
        }
        JSONObject valueJson = new JSONObject();
        List<ParamVo> voList = JSONObject.parseArray(payParam, ParamVo.class);
        for (ParamVo paramVo : voList) {
            valueJson.put(paramVo.getField(), paramVo.getValue());
        }
        return valueJson.toJSONString();
    }

    protected JSONObject getParams(HttpServletRequest request) {
        String params = request.getParameter("params");
        if(StringUtils.isNotBlank(params)) {
            return JSON.parseObject(params);
        }
        // 参数Map
        Map properties = request.getParameterMap();
//        if(properties.size() < 1) {
//            try {
//                return jsonParams(request);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        // 返回值Map
        JSONObject returnObject = new JSONObject();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name;
        String value = "";
        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            returnObject.put(name, value);
        }
         return returnObject;
    }
}
