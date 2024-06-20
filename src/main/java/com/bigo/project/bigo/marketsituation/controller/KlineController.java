package com.bigo.project.bigo.marketsituation.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.marketsituation.entity.KlineConfig;
import com.bigo.project.bigo.marketsituation.service.IKlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/6/15 18:10
 */
@RestController
@RequestMapping("/bigo/kline")
public class KlineController extends BaseController {

    @Autowired
    private IKlineService klineService;

//    @PreAuthorize("@ss.hasPermi('bigo:kline:list')")
//    @GetMapping("/list")
//    public TableDataInfo list(KlineConfig config)
//    {
//        List<KlineConfig> list = new ArrayList<>();
//        String uri = CoinUtils.getKlineConfigUrl();
//        String res = OkHttpUtil.get(uri +"/kline/info/"+ SymbolEnum.CREUSDT.getCode(), null);
//        if(StringUtils.isEmpty(res)) return getDataTable(list);
//        KlineConfig klineConfig = JSONObject.parseObject(res, KlineConfig.class);
//        if(klineConfig !=null) list.add(klineConfig);
//        return getDataTable(list);
//    }

    @GetMapping("/get/{symbol}")
    public AjaxResult get(@PathVariable String symbol)
    {
        String uri = CoinUtils.getKlineConfigUrl();
        String res = OkHttpUtil.get(uri +"/kline/info/"+ symbol, null);
        if(res == null) return AjaxResult.success();
        KlineConfig klineConfig = JSONObject.parseObject(res, KlineConfig.class);
        return AjaxResult.success(klineConfig);
    }

    @PreAuthorize("@ss.hasPermi('bigo:kline:save')")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody KlineConfig config)
    {
//        if(!config.getSymbol().equals(SymbolEnum.CREUSDT.getCode())) {
//            AjaxResult.error("不支持该币种配置");
//        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(config);
        String uri = CoinUtils.getKlineConfigUrl();
        String res = OkHttpUtil.postJsonParams(uri +"/kline/save/", jsonObject.toJSONString());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('bigo:kline:edit')")
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody KlineConfig config)
    {
//        if(!config.getSymbol().equals(SymbolEnum.CREUSDT.getCode())) {
//            AjaxResult.error("不支持该币种配置");
//        }
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(config);
        String uri = CoinUtils.getKlineConfigUrl();
        String res = OkHttpUtil.postJsonParams(uri +"/kline/save/", jsonObject.toJSONString());
        return AjaxResult.success();
    }
}
