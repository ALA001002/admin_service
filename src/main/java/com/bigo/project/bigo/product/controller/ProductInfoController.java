package com.bigo.project.bigo.product.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.service.IProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 理财产品信息Controller
 * 
 * @author bigo
 * @date 2021-01-27
 */
@RestController
@RequestMapping("/product/productInfo")
public class ProductInfoController extends BaseController
{
    @Autowired
    private IProductInfoService productInfoService;

    /**
     * 查询理财产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProductInfo productInfo)
    {
        startPage();
        List<ProductInfo> list = productInfoService.selectProductInfoList(productInfo);
        return getDataTable(list);
    }

    /**
     * 导出理财产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:export')")
    @Log(title = "理财产品信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProductInfo productInfo)
    {
        List<ProductInfo> list = productInfoService.selectProductInfoList(productInfo);
        ExcelUtil<ProductInfo> util = new ExcelUtil<ProductInfo>(ProductInfo.class);
        return util.exportExcel(list, "productInfo");
    }

    /**
     * 获取理财产品信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(productInfoService.selectProductInfoById(id));
    }

    /**
     * 新增理财产品信息
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:add')")
    @Log(title = "理财产品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductInfo productInfo)
    {
        return toAjax(productInfoService.insertProductInfo(productInfo));
    }

    /**
     * 修改理财产品信息
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:edit')")
    @Log(title = "理财产品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductInfo productInfo)
    {
        return toAjax(productInfoService.updateProductInfo(productInfo));
    }

    /**
     * 删除理财产品信息
     */
    @PreAuthorize("@ss.hasPermi('product:productInfo:remove')")
    @Log(title = "理财产品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(productInfoService.deleteProductInfoByIds(ids));
    }
}
