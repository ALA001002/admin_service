package com.bigo.project.bigo.ico.controller;

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
import com.bigo.project.bigo.ico.domain.SymbolCoin;
import com.bigo.project.bigo.ico.service.ISymbolCoinService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 发币Controller
 * 
 * @author bigo
 * @date 2023-01-06
 */
@RestController
@RequestMapping("/ico/symbolCoin")
public class SymbolCoinController extends BaseController
{
    @Autowired
    private ISymbolCoinService symbolCoinService;

    /**
     * 查询发币列表
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:list')")
    @GetMapping("/list")
    public TableDataInfo list(SymbolCoin symbolCoin)
    {
        startPage();
        List<SymbolCoin> list = symbolCoinService.selectSymbolCoinList(symbolCoin);
        return getDataTable(list);
    }

    /**
     * 导出发币列表
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:export')")
    @Log(title = "发币", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SymbolCoin symbolCoin)
    {
        List<SymbolCoin> list = symbolCoinService.selectSymbolCoinList(symbolCoin);
        ExcelUtil<SymbolCoin> util = new ExcelUtil<SymbolCoin>(SymbolCoin.class);
        return util.exportExcel(list, "symbolCoin");
    }

    /**
     * 获取发币详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(symbolCoinService.selectSymbolCoinById(id));
    }

    /**
     * 新增发币
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:add')")
    @Log(title = "发币", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SymbolCoin symbolCoin)
    {
        return toAjax(symbolCoinService.insertSymbolCoin(symbolCoin));
    }

    /**
     * 修改发币
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:edit')")
    @Log(title = "发币", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SymbolCoin symbolCoin)
    {
        return toAjax(symbolCoinService.updateSymbolCoin(symbolCoin));
    }

    /**
     * 删除发币
     */
    @PreAuthorize("@ss.hasPermi('ico:symbolCoin:remove')")
    @Log(title = "发币", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(symbolCoinService.deleteSymbolCoinByIds(ids));
    }
}
