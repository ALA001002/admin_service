package com.bigo.project.bigo.product.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.product.domain.ProductType;
import com.bigo.project.bigo.product.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 理财产品类型Controller
 * 
 * @author bigo
 * @date 2022-03-22
 */
@RestController
@RequestMapping("/product/type")
public class ProductTypeController extends BaseController
{
    @Autowired
    private IProductTypeService productTypeService;

    /**
     * 查询理财产品类型列表
     */
    @PreAuthorize("@ss.hasPermi('product:type:list')")
    @GetMapping("/list")
    public TableDataInfo list(ProductType productType)
    {
        startPage();
        List<ProductType> list = productTypeService.selectProductTypeList(productType);
        return getDataTable(list);
    }

    /**
     * 导出理财产品类型列表
     */
    @PreAuthorize("@ss.hasPermi('product:type:export')")
    @Log(title = "理财产品类型", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProductType productType)
    {
        List<ProductType> list = productTypeService.selectProductTypeList(productType);
        ExcelUtil<ProductType> util = new ExcelUtil<ProductType>(ProductType.class);
        return util.exportExcel(list, "type");
    }

    /**
     * 获取理财产品类型详细信息
     */
    @PreAuthorize("@ss.hasPermi('product:type:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(productTypeService.selectProductTypeById(id));
    }

    /**
     * 新增理财产品类型
     */
    @PreAuthorize("@ss.hasPermi('product:type:add')")
    @Log(title = "理财产品类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductType productType)
    {
        return toAjax(productTypeService.insertProductType(productType));
    }

    /**
     * 修改理财产品类型
     */
    @PreAuthorize("@ss.hasPermi('product:type:edit')")
    @Log(title = "理财产品类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductType productType)
    {
        return toAjax(productTypeService.updateProductType(productType));
    }

    /**
     * 删除理财产品类型
     */
    @PreAuthorize("@ss.hasPermi('product:type:remove')")
    @Log(title = "理财产品类型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(productTypeService.deleteProductTypeByIds(ids));
    }
}
