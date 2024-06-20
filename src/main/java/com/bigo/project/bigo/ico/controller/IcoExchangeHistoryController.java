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
import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;
import com.bigo.project.bigo.ico.service.IIcoExchangeHistoryService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * ico交易记录Controller
 * 
 * @author xx
 * @date 2023-01-15
 */
@RestController
@RequestMapping("/ico/exchangehistory")
public class IcoExchangeHistoryController extends BaseController
{
    @Autowired
    private IIcoExchangeHistoryService icoExchangeHistoryService;

    /**
     * 查询ico交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(IcoExchangeHistory icoExchangeHistory)
    {
        startPage();
        List<IcoExchangeHistory> list = icoExchangeHistoryService.selectIcoExchangeHistoryList(icoExchangeHistory);
        return getDataTable(list);
    }

    /**
     * 导出ico交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:export')")
    @Log(title = "ico交易记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(IcoExchangeHistory icoExchangeHistory)
    {
        List<IcoExchangeHistory> list = icoExchangeHistoryService.selectIcoExchangeHistoryList(icoExchangeHistory);
        ExcelUtil<IcoExchangeHistory> util = new ExcelUtil<IcoExchangeHistory>(IcoExchangeHistory.class);
        return util.exportExcel(list, "exchangehistory");
    }

    /**
     * 获取ico交易记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(icoExchangeHistoryService.selectIcoExchangeHistoryById(id));
    }

    /**
     * 新增ico交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:add')")
    @Log(title = "ico交易记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IcoExchangeHistory icoExchangeHistory)
    {
        return toAjax(icoExchangeHistoryService.insertIcoExchangeHistory(icoExchangeHistory));
    }

    /**
     * 修改ico交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:edit')")
    @Log(title = "ico交易记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IcoExchangeHistory icoExchangeHistory)
    {
        return toAjax(icoExchangeHistoryService.updateIcoExchangeHistory(icoExchangeHistory));
    }

    /**
     * 删除ico交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:exchangehistory:remove')")
    @Log(title = "ico交易记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(icoExchangeHistoryService.deleteIcoExchangeHistoryByIds(ids));
    }
}
