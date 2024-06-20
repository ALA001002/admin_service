package com.bigo.project.bigo.marketsituation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.PageDomain;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.framework.web.page.TableSupport;
import com.bigo.project.bigo.marketsituation.domain.SlipConfig;
import com.bigo.project.bigo.marketsituation.service.ISlipConfigService;
import com.bigo.project.bigo.marketsituation.service.impl.SlipConfigService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 滑点配置Controller
 * 
 * @author bigo
 * @date 2021-03-30
 */
@Slf4j
@RestController
@RequestMapping("/marketsituation/slipConfig")
public class SlipConfigController extends BaseController {

    @Autowired
    private ISlipConfigService slipConfigService;

    private final static String PAGE_URL = "http://127.0.0.1:7777/bgSlip/page";

    private final static String GET_URL = "http://127.0.0.1:7777/bgSlip/getConfig";

    private final static String SAVE_URL = "http://127.0.0.1:7777/bgSlip/save";

    private final static String UPDATE_STATUS_URL = "http://127.0.0.1:7777/bgSlip/updateStatus";

    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:query')")
    @GetMapping(value = "getConfig/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        if(id == null) return AjaxResult.error("id不存在");
        String result = OkHttpUtil.get(GET_URL +"/"+ id, null);
        if(StringUtils.isEmpty(result)) return AjaxResult.error("未找到该记录");
        JSONObject resultJson = JSONObject.parseObject(result);
        Integer code = resultJson.getInteger("code");
        if(!code.equals(200)) return AjaxResult.error("错误状态码："+code+"!请联系技术管理员");
        String data = resultJson.getString("data");
        SlipConfig config = JSONObject.parseObject(data, SlipConfig.class);
        if(config != null) {
            config.setStartTimeDate(new Date(config.getStartTime()));
            config.setEndTimeDate(new Date(config.getEndTime()));
            config.setOpenFlagInter(config.getOpenFlag() == true ? 1 : 0);
            config.setDelFlagInter(config.getDelFlag() == true ? 1 : 0);
            config.setAddValueStr(config.getAddValue().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
        }
        return AjaxResult.success(config);
    }

    /**
     * 查询滑点配置列表
     */
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(SlipConfig slipConfig)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        List<SlipConfig> list = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("pageNo", pageNum);
        params.put("pageSize", pageSize);
        if(StringUtils.isNotNull(slipConfig.getSymbol())) params.put("symbol", slipConfig.getSymbol());
        String result = OkHttpUtil.postJsonParams(PAGE_URL, params.toString());

        JSONObject resultJson = JSONObject.parseObject(result);
        resultJson = resultJson.getJSONObject("data");
        String content = resultJson.getString("content");
        list = JSON.parseArray(content, SlipConfig.class);
        if(list == null) {
            list = new ArrayList<>();
        } else {
            for (SlipConfig config : list) {
                config.setStartTimeDate(new Date(config.getStartTime()));
                config.setEndTimeDate(new Date(config.getEndTime()));
                config.setOpenFlagInter(config.getOpenFlag() == true ? 1 : 0);
                config.setDelFlagInter(config.getDelFlag() == true ? 1 : 0);
                config.setAddValueStr(config.getAddValue().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
            }
        }
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:add')")
    @Log(title = "滑点配置", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult add(@RequestBody SlipConfig slipConfig) {

        JSONObject params = new JSONObject();
        params.put("symbol", slipConfig.getSymbol());
        params.put("addValue", slipConfig.getAddValue());
        params.put("startTime", slipConfig.getStartTimeDate().getTime());
        params.put("intervalTime", slipConfig.getIntervalTime());
        params.put("type", slipConfig.getType());
        params.put("openFlag", slipConfig.getOpenFlagInter() == 1?true:false);

        String result = OkHttpUtil.postJsonParams(SAVE_URL, params.toString());
        log.info("添加滑点配置返回结果：{}", result);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:edit')")
    @Log(title = "滑点配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody SlipConfig slipConfig) {

        JSONObject params = new JSONObject();
        params.put("id", slipConfig.getId());
        params.put("symbol", slipConfig.getSymbol());
        params.put("addValue", slipConfig.getAddValue());
        params.put("startTime", slipConfig.getStartTimeDate().getTime());
        params.put("intervalTime", slipConfig.getIntervalTime());
        params.put("type", slipConfig.getType());
        params.put("openFlag", slipConfig.getOpenFlagInter() == 1?true:false);

        String result = OkHttpUtil.postJsonParams(SAVE_URL, params.toString());
        JSONObject resultJson = JSONObject.parseObject(result);
        Integer code = resultJson.getInteger("code");
        if(!code.equals(200)) return AjaxResult.error("新增修改滑点配置失败！");
        return AjaxResult.success();
    }


    @PostMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody SlipConfig slipConfig) {

        JSONObject params = new JSONObject();
        params.put("id", slipConfig.getId());
        params.put("openFlag", slipConfig.getOpenFlagInter() == 1?true:false);

        String result = OkHttpUtil.postJsonParams(UPDATE_STATUS_URL, params.toString());
        JSONObject resultJson = JSONObject.parseObject(result);
        Integer code = resultJson.getInteger("code");
        if(!code.equals(200)) return AjaxResult.error("更新配置状态失败！");
        return AjaxResult.success();
    }

    /**
     * 导出滑点配置列表
     *//*
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:export')")
    @Log(title = "滑点配置", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SlipConfig slipConfig)
    {
        List<SlipConfig> list = slipConfigService.selectSlipConfigList(slipConfig);
        ExcelUtil<SlipConfig> util = new ExcelUtil<SlipConfig>(SlipConfig.class);
        return util.exportExcel(list, "slipConfig");
    }

    *//**
     * 获取滑点配置详细信息
     *//*
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(slipConfigService.selectSlipConfigById(id));
    }

    *//**
     * 新增滑点配置
     *//*
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:add')")
    @Log(title = "滑点配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SlipConfig slipConfig)
    {
        return toAjax(slipConfigService.insertSlipConfig(slipConfig));
    }

    *//**
     * 修改滑点配置
     *//*
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:edit')")
    @Log(title = "滑点配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SlipConfig slipConfig)
    {
        return toAjax(slipConfigService.updateSlipConfig(slipConfig));
    }

    /**
     * 删除滑点配置
     */
    @PreAuthorize("@ss.hasPermi('marketsituation:slipConfig:remove')")
    @Log(title = "滑点配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/remove/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(slipConfigService.deleteSlipConfigById(id));
    }
}
