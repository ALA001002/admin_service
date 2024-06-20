package com.bigo.project.system.controller;

import com.bigo.common.utils.DateUtils;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.system.service.ISysIndexService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 首页信息查询
 * 
 * @author bigo
 */
@RestController
@RequestMapping("/system/index")
public class SysIndexController extends BaseController
{
    @Autowired
    private ISysIndexService indexService;

    /**
     * 获取通知公告列表
     */
    @GetMapping("/getIndexInfo")
    public AjaxResult list(@RequestParam("type") String type) {
        Date today = new Date();
        Date startTime = null;
        Date endTime = null;
        if("today".equals(type)){
            startTime = DateUtils.getStartTime(today,0);
            endTime = DateUtils.getEndTime(today,0);
        }else if("yesterday".equals(type)){
            startTime = DateUtils.getStartTime(today,-1);
            endTime = DateUtils.getEndTime(today,-1);
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        return AjaxResult.success(indexService.getIndexInfo(params));
    }


}
