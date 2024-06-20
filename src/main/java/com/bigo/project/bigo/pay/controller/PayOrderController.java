package com.bigo.project.bigo.pay.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.pay.domain.PayOrder;
import com.bigo.project.bigo.pay.service.IPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 三方支付订单表Controller
 * 
 * @author bigo
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/pay/order")
public class PayOrderController extends BaseController
{
    @Autowired
    private IPayOrderService payOrderService;

    /**
     * 查询三方支付订单表列表
     */
    @PreAuthorize("@ss.hasPermi('pay:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayOrder payOrder)
    {
        startPage();
        List<PayOrder> list = payOrderService.selectPayOrderList(payOrder);
        return getDataTable(list);
    }

    /**
     * 导出三方支付订单表列表
     */
    @PreAuthorize("@ss.hasPermi('pay:order:export')")
    @Log(title = "三方支付订单表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(PayOrder payOrder)
    {
        List<PayOrder> list = payOrderService.selectPayOrderList(payOrder);
        ExcelUtil<PayOrder> util = new ExcelUtil<PayOrder>(PayOrder.class);
        return util.exportExcel(list, "order");
    }

    /**
     * 获取三方支付订单表详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(payOrderService.selectPayOrderById(id));
    }

    /**
     * 新增三方支付订单表
     */
    @PreAuthorize("@ss.hasPermi('pay:order:add')")
    @Log(title = "三方支付订单表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PayOrder payOrder)
    {
        return toAjax(payOrderService.insertPayOrder(payOrder));
    }

    /**
     * 修改三方支付订单表
     */
    @PreAuthorize("@ss.hasPermi('pay:order:edit')")
    @Log(title = "三方支付订单表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PayOrder payOrder)
    {
        return toAjax(payOrderService.updatePayOrder(payOrder));
    }

    /**
     * 删除三方支付订单表
     */
    @PreAuthorize("@ss.hasPermi('pay:order:remove')")
    @Log(title = "三方支付订单表", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(payOrderService.deletePayOrderByIds(ids));
    }
}
