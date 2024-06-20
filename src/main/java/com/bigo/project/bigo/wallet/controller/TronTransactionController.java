package com.bigo.project.bigo.wallet.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.service.ITronTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Trx充提Controller
 * 
 * @author bigo
 * @date 2021-11-23
 */
@RestController
@RequestMapping("/wallet/tronTransaction")
public class TronTransactionController extends BaseController
{
    @Autowired
    private ITronTransactionService tronTransactionService;

    /**
     * 查询Trx充提列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:list')")
    @GetMapping("/list")
    public TableDataInfo list(TronTransaction tronTransaction)
    {
        startPage();
        List<TronTransaction> list = tronTransactionService.listTronTransaction(tronTransaction);
        return getDataTable(list);
    }

    /**
     * 导出Trx充提列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:export')")
    @Log(title = "Trx充提", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(TronTransaction tronTransaction)
    {
        List<TronTransaction> list = tronTransactionService.selectTronTransactionList(tronTransaction);
        ExcelUtil<TronTransaction> util = new ExcelUtil<TronTransaction>(TronTransaction.class);
        return util.exportExcel(list, "tronTransaction");
    }

    /**
     * 获取Trx充提详细信息
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(tronTransactionService.selectTronTransactionById(id));
    }

    /**
     * 新增Trx充提
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:add')")
    @Log(title = "Trx充提", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TronTransaction tronTransaction)
    {
        return toAjax(tronTransactionService.insertTronTransaction(tronTransaction));
    }

    /**
     * 修改Trx充提
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:edit')")
    @Log(title = "Trx充提", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TronTransaction tronTransaction)
    {
        return toAjax(tronTransactionService.updateTronTransaction(tronTransaction));
    }

    /**
     * 删除Trx充提
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:remove')")
    @Log(title = "Trx充提", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tronTransactionService.deleteTronTransactionByIds(ids));
    }

    /**
     * Trx归集通知
     */
    @PreAuthorize("@ss.hasPermi('wallet:tronTransaction:collect')")
    @Log(title = "Trx归集通知", businessType = BusinessType.UPDATE)
    @PutMapping("/collect")
    public AjaxResult collect(@RequestBody TronTransaction tronTransaction)
    {
        return tronTransactionService.collect(tronTransaction);
    }
}
