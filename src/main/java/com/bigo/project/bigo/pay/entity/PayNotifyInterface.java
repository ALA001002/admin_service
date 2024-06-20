package com.bigo.project.bigo.pay.entity;

import com.alibaba.fastjson.JSONObject;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/5/21 19:02
 */
public interface PayNotifyInterface {
    JSONObject doNotify(Object notifyData);
}
