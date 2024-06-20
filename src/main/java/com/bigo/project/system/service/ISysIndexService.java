package com.bigo.project.system.service;

import com.bigo.project.system.domain.vo.IndexInfoVO;

import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/18 20:08
 */
public interface ISysIndexService {

    /**
     * 获取首页展示信息
     * @param params
     * @return
     */
    IndexInfoVO getIndexInfo(Map<String,Object> params);

    IndexInfoVO getIndexInfoNew(Map<String,Object> params);

    void sumUserBalance(Map map, Map params);

    Long getRechargeNum(Map params);
}
