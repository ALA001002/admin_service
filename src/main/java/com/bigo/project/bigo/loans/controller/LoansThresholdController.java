package com.bigo.project.bigo.loans.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.loans.domain.LoansThreshold;
import com.bigo.project.bigo.loans.service.ILoansThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 借款门槛Controller
 * 
 * @author bigo
 * @date 2022-01-12
 */
@RestController
@RequestMapping("/loans/LoansThreshold")
public class LoansThresholdController extends BaseController
{
    @Autowired
    private ILoansThresholdService loansThresholdService;

    /**
     * 查询借款门槛列表
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:list')")
    @GetMapping("/list")
    public TableDataInfo list(LoansThreshold loansThreshold)
    {
        startPage();
        List<LoansThreshold> list = loansThresholdService.selectLoansThresholdList(loansThreshold);
        return getDataTable(list);
    }

    /**
     * 导出借款门槛列表
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:export')")
    @Log(title = "借款门槛", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LoansThreshold loansThreshold)
    {
        List<LoansThreshold> list = loansThresholdService.selectLoansThresholdList(loansThreshold);
        ExcelUtil<LoansThreshold> util = new ExcelUtil<LoansThreshold>(LoansThreshold.class);
        return util.exportExcel(list, "LoansThreshold");
    }

    /**
     * 获取借款门槛详细信息
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(loansThresholdService.selectLoansThresholdById(id));
    }

    /**
     * 新增借款门槛
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:add')")
    @Log(title = "借款门槛", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LoansThreshold loansThreshold)
    {
        return toAjax(loansThresholdService.insertLoansThreshold(loansThreshold));
    }

    /**
     * 修改借款门槛
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:edit')")
    @Log(title = "借款门槛", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LoansThreshold loansThreshold)
    {
        return toAjax(loansThresholdService.updateLoansThreshold(loansThreshold));
    }

    /**
     * 删除借款门槛
     */
    @PreAuthorize("@ss.hasPermi('loans:LoansThreshold:remove')")
    @Log(title = "借款门槛", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(loansThresholdService.deleteLoansThresholdByIds(ids));
    }
}
