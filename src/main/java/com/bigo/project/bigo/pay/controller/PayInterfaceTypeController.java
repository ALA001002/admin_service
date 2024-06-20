package com.bigo.project.bigo.pay.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.pay.domain.PayInterfaceType;
import com.bigo.project.bigo.pay.service.IPayInterfaceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付接口类型Controller
 * 
 * @author bigo
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/pay/interface")
public class PayInterfaceTypeController extends BaseController
{
    @Autowired
    private IPayInterfaceTypeService payInterfaceTypeService;

    /**
     * 查询支付接口类型列表
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayInterfaceType payInterfaceType)
    {
        startPage();
        List<PayInterfaceType> list = payInterfaceTypeService.selectPayInterfaceTypeList(payInterfaceType);
        return getDataTable(list);
    }

    /**
     * 导出支付接口类型列表
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:export')")
    @Log(title = "支付接口类型", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PayInterfaceType payInterfaceType)
    {
        List<PayInterfaceType> list = payInterfaceTypeService.selectPayInterfaceTypeList(payInterfaceType);
        ExcelUtil<PayInterfaceType> util = new ExcelUtil<PayInterfaceType>(PayInterfaceType.class);
        return util.exportExcel(list, "interface");
    }

    /**
     * 获取支付接口类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:query')")
    @GetMapping(value = "/{ifTypeCode}")
    public AjaxResult getInfo(@PathVariable("ifTypeCode") String ifTypeCode)
    {
        return AjaxResult.success(payInterfaceTypeService.selectPayInterfaceTypeById(ifTypeCode));
    }

    /**
     * 新增支付接口类型
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:add')")
    @Log(title = "支付接口类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayInterfaceType payInterfaceType)
    {
        return toAjax(payInterfaceTypeService.insertPayInterfaceType(payInterfaceType));
    }

    /**
     * 修改支付接口类型
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:edit')")
    @Log(title = "支付接口类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayInterfaceType payInterfaceType)
    {
        return toAjax(payInterfaceTypeService.updatePayInterfaceType(payInterfaceType));
    }

    /**
     * 删除支付接口类型
     */
    @PreAuthorize("@ss.hasPermi('pay:interface:remove')")
    @Log(title = "支付接口类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ifTypeCodes}")
    public AjaxResult remove(@PathVariable String[] ifTypeCodes)
    {
        return toAjax(payInterfaceTypeService.deletePayInterfaceTypeByIds(ifTypeCodes));
    }

    @PreAuthorize("@ss.hasPermi('pay:interface:edit')")
    @Log(title = "修改配置", businessType = BusinessType.UPDATE)
    @PutMapping("/updateParam")
    public AjaxResult updateParam(@RequestBody PayInterfaceType payInterfaceType)
    {
        return toAjax(payInterfaceTypeService.updateParam(payInterfaceType));
    }
}
