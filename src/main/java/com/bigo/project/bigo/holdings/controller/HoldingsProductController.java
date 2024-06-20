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
import com.bigo.project.bigo.holdings.domain.HoldingsProduct;
import com.bigo.project.bigo.holdings.service.IHoldingsProductService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 智能持仓产品Controller
 * 
 * @author bigo
 * @date 2023-12-26
 */
@RestController
@RequestMapping("/holdings/product")
public class HoldingsProductController extends BaseController
{
    @Autowired
    private IHoldingsProductService holdingsProductService;

    /**
     * 查询智能持仓产品列表
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(HoldingsProduct holdingsProduct)
    {
        startPage();
        List<HoldingsProduct> list = holdingsProductService.selectHoldingsProductList(holdingsProduct);
        return getDataTable(list);
    }

    /**
     * 导出智能持仓产品列表
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:export')")
    @Log(title = "智能持仓产品", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(HoldingsProduct holdingsProduct)
    {
        List<HoldingsProduct> list = holdingsProductService.selectHoldingsProductList(holdingsProduct);
        ExcelUtil<HoldingsProduct> util = new ExcelUtil<HoldingsProduct>(HoldingsProduct.class);
        return util.exportExcel(list, "product");
    }

    /**
     * 获取智能持仓产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(holdingsProductService.selectHoldingsProductById(id));
    }

    /**
     * 新增智能持仓产品
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:add')")
    @Log(title = "智能持仓产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HoldingsProduct holdingsProduct)
    {
        return toAjax(holdingsProductService.insertHoldingsProduct(holdingsProduct));
    }

    /**
     * 修改智能持仓产品
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:edit')")
    @Log(title = "智能持仓产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HoldingsProduct holdingsProduct)
    {
        return toAjax(holdingsProductService.updateHoldingsProduct(holdingsProduct));
    }

    /**
     * 删除智能持仓产品
     */
    @PreAuthorize("@ss.hasPermi('holdings:product:remove')")
    @Log(title = "智能持仓产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(holdingsProductService.deleteHoldingsProductByIds(ids));
    }
}
