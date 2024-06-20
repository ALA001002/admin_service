package com.bigo.project.bigo.holdings.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.project.bigo.holdings.domain.HoldingsOrder;
import com.bigo.project.bigo.holdings.service.IHoldingsOrderService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 智能持仓订单Controller
 * 
 * @author bigo
 * @date 2023-12-28
 */
@RestController
@RequestMapping("/holdings/order")
public class HoldingsOrderController extends BaseController
{
    @Autowired
    private IHoldingsOrderService holdingsOrderService;

    /**
     * 查询智能持仓订单列表
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(HoldingsOrder holdingsOrder)
    {
        startPage();
        List<HoldingsOrder> list = holdingsOrderService.selectHoldingsOrderList(holdingsOrder);
        return getDataTable(list);
    }

    /**
     * 导出智能持仓订单列表
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:export')")
    @Log(title = "智能持仓订单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(HoldingsOrder holdingsOrder)
    {
        List<HoldingsOrder> list = holdingsOrderService.selectHoldingsOrderList(holdingsOrder);
        ExcelUtil<HoldingsOrder> util = new ExcelUtil<HoldingsOrder>(HoldingsOrder.class);
        return util.exportExcel(list, "order");
    }

    /**
     * 获取智能持仓订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(holdingsOrderService.selectHoldingsOrderById(id));
    }

    /**
     * 新增智能持仓订单
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:add')")
    @Log(title = "智能持仓订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HoldingsOrder holdingsOrder)
    {
        return toAjax(holdingsOrderService.insertHoldingsOrder(holdingsOrder));
    }

    /**
     * 修改智能持仓订单
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:edit')")
    @Log(title = "智能持仓订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HoldingsOrder holdingsOrder)
    {
        return toAjax(holdingsOrderService.updateHoldingsOrder(holdingsOrder));
    }

    /**
     * 删除智能持仓订单
     */
    @PreAuthorize("@ss.hasPermi('holdings:order:remove')")
    @Log(title = "智能持仓订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(holdingsOrderService.deleteHoldingsOrderByIds(ids));
    }
}
