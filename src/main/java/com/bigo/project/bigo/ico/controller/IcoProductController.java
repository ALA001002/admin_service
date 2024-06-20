package com.bigo.project.bigo.ico.controller;

import java.util.List;

import com.bigo.project.bigo.ico.domain.SymbolCoin;
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
import com.bigo.project.bigo.ico.domain.IcoProduct;
import com.bigo.project.bigo.ico.service.IIcoProductService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * ico产品Controller
 * 
 * @author xx
 * @date 2023-01-07
 */
@RestController
@RequestMapping("/ico/icoProduct")
public class IcoProductController extends BaseController
{
    @Autowired
    private IIcoProductService icoProductService;



    /**
     * 查询ico产品列表
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:list')")
    @GetMapping("/list")
    public TableDataInfo list(IcoProduct icoProduct)
    {
        startPage();
        List<IcoProduct> list = icoProductService.selectIcoProductList(icoProduct);
        return getDataTable(list);
    }

    /**
     * 导出ico产品列表
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:export')")
    @Log(title = "ico产品", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(IcoProduct icoProduct)
    {
        List<IcoProduct> list = icoProductService.selectIcoProductList(icoProduct);
        ExcelUtil<IcoProduct> util = new ExcelUtil<IcoProduct>(IcoProduct.class);
        return util.exportExcel(list, "icoProduct");
    }

    /**
     * 获取ico产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(icoProductService.selectIcoProductById(id));
    }

    /**
     * 新增ico产品
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:add')")
    @Log(title = "ico产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IcoProduct icoProduct)
    {
        return toAjax(icoProductService.insertIcoProduct(icoProduct));
    }

    /**
     * 修改ico产品
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:edit')")
    @Log(title = "ico产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IcoProduct icoProduct)
    {
        return toAjax(icoProductService.updateIcoProduct(icoProduct));
    }

    /**
     * 删除ico产品
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProduct:remove')")
    @Log(title = "ico产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(icoProductService.deleteIcoProductByIds(ids));
    }


}
