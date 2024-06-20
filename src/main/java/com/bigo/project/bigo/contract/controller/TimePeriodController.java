package com.bigo.project.bigo.contract.controller;

import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.contract.domain.TimePeriod;
import com.bigo.project.bigo.contract.service.ITimePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 限时合约周期信息Controller
 * 
 * @author bigo
 * @date 2021-02-04
 */
@RestController
@RequestMapping("/contract/period")
public class TimePeriodController extends BaseController
{
    @Autowired
    private ITimePeriodService timePeriodService;

    /**
     * 查询限时合约周期信息列表
     */
    @PreAuthorize("@ss.hasPermi('contract:period:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimePeriod timePeriod)
    {
        startPage();
        List<TimePeriod> list = timePeriodService.listByEntity(timePeriod);
        return getDataTable(list);
    }

    /**
     * 获取限时合约周期信息详细信息
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('contract:period:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setId(id);
        return AjaxResult.success(timePeriodService.selectTimePeriodById(timePeriod));
    }

    /**
     * 新增
     * @param timePeriod
     * @return
     */
    @PreAuthorize("@ss.hasPermi('contract:period:add')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimePeriod timePeriod) {
        TimePeriod period = new TimePeriod();
        period.setSymbol(timePeriod.getSymbol());
        period.setPeriod(timePeriod.getPeriod());
        period = timePeriodService.selectTimePeriodById(period);
        if(period != null) {
            return AjaxResult.error("该周期时限已存在！");
        }
        int status = timePeriodService.insert(timePeriod);
        return toAjax(status);
    }

    /**
     * 修改
     * @param timePeriod
     * @return
     */
    @PreAuthorize("@ss.hasPermi('contract:period:edit')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimePeriod timePeriod) {
        TimePeriod period = new TimePeriod();
        period.setId(timePeriod.getId());
        period = timePeriodService.selectTimePeriodById(period);
        if(period == null) {
            return AjaxResult.error("该周期时限不存在！");
        }
        TimePeriod period2 = new TimePeriod();
        period2.setSymbol(timePeriod.getSymbol());
        period2.setPeriod(timePeriod.getPeriod());
        period2 = timePeriodService.selectTimePeriodById(period2);
        if(period2 != null && period2.getId() != period.getId()) {
            return AjaxResult.error("该周期时限不可重复！");
        }
        return toAjax(timePeriodService.update(timePeriod));
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @PreAuthorize("@ss.hasPermi('contract:period:remove')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(timePeriodService.deleteById(ids));
    }
/*
    *//**
     * 导出限时合约周期信息列表
     *//*
    @PreAuthorize("@ss.hasPermi('contract:period:export')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TimePeriod timePeriod)
    {
        List<TimePeriod> list = timePeriodService.selectTimePeriodList(timePeriod);
        ExcelUtil<TimePeriod> util = new ExcelUtil<TimePeriod>(TimePeriod.class);
        return util.exportExcel(list, "period");
    }

    *//**
     * 获取限时合约周期信息详细信息
     *//*
    @PreAuthorize("@ss.hasPermi('contract:period:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(timePeriodService.selectTimePeriodById(id));
    }

    *//**
     * 新增限时合约周期信息
     *//*
    @PreAuthorize("@ss.hasPermi('contract:period:add')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimePeriod timePeriod)
    {
        return toAjax(timePeriodService.insertTimePeriod(timePeriod));
    }

    *//**
     * 修改限时合约周期信息
     *//*
    @PreAuthorize("@ss.hasPermi('contract:period:edit')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimePeriod timePeriod)
    {
        return toAjax(timePeriodService.updateTimePeriod(timePeriod));
    }

    *//**
     * 删除限时合约周期信息
     *//*
    @PreAuthorize("@ss.hasPermi('contract:period:remove')")
    @Log(title = "限时合约周期信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(timePeriodService.deleteTimePeriodByIds(ids));
    }*/
}
