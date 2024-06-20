package com.bigo.project.bigo.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.pay.domain.PayPassage;
import com.bigo.project.bigo.pay.service.IPayPassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付通道Controller
 * 
 * @author bigo
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/pay/passage")
public class PayPassageController extends BaseController
{
    @Autowired
    private IPayPassageService payPassageService;

    /**
     * 查询支付通道列表
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayPassage payPassage)
    {
        startPage();
        List<PayPassage> list = payPassageService.selectPayPassageList(payPassage);
        return getDataTable(list);
    }

    /**
     * 导出支付通道列表
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:export')")
    @Log(title = "支付通道", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PayPassage payPassage)
    {
        List<PayPassage> list = payPassageService.selectPayPassageList(payPassage);
        ExcelUtil<PayPassage> util = new ExcelUtil<PayPassage>(PayPassage.class);
        return util.exportExcel(list, "passage");
    }

    /**
     * 获取支付通道详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(payPassageService.selectPayPassageById(id));
    }

    /**
     * 新增支付通道
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:add')")
    @Log(title = "支付通道", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayPassage payPassage)
    {
        return toAjax(payPassageService.insertPayPassage(payPassage));
    }

    /**
     * 修改支付通道
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:edit')")
    @Log(title = "支付通道", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayPassage payPassage)
    {
        return toAjax(payPassageService.updatePayPassage(payPassage));
    }

    /**
     * 删除支付通道
     */
    @PreAuthorize("@ss.hasPermi('pay:passage:remove')")
    @Log(title = "支付通道", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(payPassageService.deletePayPassageByIds(ids));
    }

    @PostMapping("updateParam")
    public AjaxResult updateParam(@RequestBody JSONObject object) {
        payPassageService.updateParam(object);
        return AjaxResult.success();
    }
}
