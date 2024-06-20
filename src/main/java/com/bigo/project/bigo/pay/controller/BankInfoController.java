package com.bigo.project.bigo.pay.controller;

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
import com.bigo.project.bigo.pay.domain.BankInfo;
import com.bigo.project.bigo.pay.service.IBankInfoService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 银行信息Controller
 * 
 * @author bigo
 * @date 2022-05-23
 */
@RestController
@RequestMapping("/pay/bankInfo")
public class BankInfoController extends BaseController
{
    @Autowired
    private IBankInfoService bankInfoService;

    /**
     * 查询银行信息列表
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(BankInfo bankInfo)
    {
        startPage();
        List<BankInfo> list = bankInfoService.selectBankInfoList(bankInfo);
        return getDataTable(list);
    }

    /**
     * 导出银行信息列表
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:export')")
    @Log(title = "银行信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(BankInfo bankInfo)
    {
        List<BankInfo> list = bankInfoService.selectBankInfoList(bankInfo);
        ExcelUtil<BankInfo> util = new ExcelUtil<BankInfo>(BankInfo.class);
        return util.exportExcel(list, "bankInfo");
    }

    /**
     * 获取银行信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(bankInfoService.selectBankInfoById(id));
    }

    /**
     * 新增银行信息
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:add')")
    @Log(title = "银行信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BankInfo bankInfo)
    {
        return toAjax(bankInfoService.insertBankInfo(bankInfo));
    }

    /**
     * 修改银行信息
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:edit')")
    @Log(title = "银行信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BankInfo bankInfo)
    {
        return toAjax(bankInfoService.updateBankInfo(bankInfo));
    }

    /**
     * 删除银行信息
     */
    @PreAuthorize("@ss.hasPermi('pay:bankInfo:remove')")
    @Log(title = "银行信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bankInfoService.deleteBankInfoByIds(ids));
    }
}
