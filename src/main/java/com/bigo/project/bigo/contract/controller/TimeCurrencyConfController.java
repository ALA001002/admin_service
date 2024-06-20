package com.bigo.project.bigo.contract.controller;

import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.contract.domain.TimeCurrencyConf;
import com.bigo.project.bigo.contract.service.ITimeCurrencyConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 限时币种配置Controller
 * 
 * @author WY
 * @date 2021-02-01
 */
@RestController
@RequestMapping("/contract/timeCurrency")
public class TimeCurrencyConfController extends BaseController
{
    @Autowired
    private ITimeCurrencyConfService timeCurrencyConfService;

    /**
     * 查询限时币种配置列表
     */
    @PreAuthorize("@ss.hasPermi('contract:timeCurrency:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimeCurrencyConf timeCurrencyConf)
    {
        startPage();
        List<TimeCurrencyConf> list = timeCurrencyConfService.selectTimeCurrencyConfList(timeCurrencyConf);
        return getDataTable(list);
    }

    /**
     * 获取限时币种配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('contract:timeCurrency:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(timeCurrencyConfService.selectTimeCurrencyConfById(id));
    }



    /**
     * 修改限时币种配置
     */
    @PreAuthorize("@ss.hasPermi('contract:timeCurrency:edit')")
    @Log(title = "限时币种配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimeCurrencyConf timeCurrencyConf)
    {
        return toAjax(timeCurrencyConfService.updateTimeCurrencyConf(timeCurrencyConf));
    }
}
