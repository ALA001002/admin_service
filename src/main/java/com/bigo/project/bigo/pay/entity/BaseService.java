package com.bigo.project.bigo.pay.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: dingzhiwei
 * @date: 17/12/24
 * @description:
 */
public class BaseService {

    protected JSONObject buildRetObj() {
        JSONObject retObj = new JSONObject();
        retObj.put("retCode", "SUCCESS");
        return retObj;
    }

    protected JSONObject buildFailRetObj() {
        JSONObject retObj = new JSONObject();
        retObj.put("retCode", "FAIL");
        return retObj;
    }

    protected JSONObject buildRetObj(String retValue, String retMsg) {
        JSONObject retObj = new JSONObject();
        retObj.put("retCode", retValue);
        retObj.put("retMsg", retMsg);
        return retObj;
    }

}
