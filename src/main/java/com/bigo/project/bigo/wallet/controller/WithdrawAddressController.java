package com.bigo.project.bigo.wallet.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.wallet.domain.WithdrawAddress;
import com.bigo.project.bigo.wallet.service.IWithdrawAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提现地址Controller
 * 
 * @author bigo
 * @date 2022-03-27
 */
@RestController
@RequestMapping("/wallet/withdrawAddress")
public class WithdrawAddressController extends BaseController
{
    @Autowired
    private IWithdrawAddressService withdrawAddressService;

    /**
     * 查询提现地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:list')")
    @GetMapping("/list")
    public TableDataInfo list(WithdrawAddress withdrawAddress)
    {
        startPage();
        List<WithdrawAddress> list = withdrawAddressService.selectWithdrawAddressList(withdrawAddress);
        return getDataTable(list);
    }

    /**
     * 导出提现地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:export')")
    @Log(title = "提现地址", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WithdrawAddress withdrawAddress)
    {
        List<WithdrawAddress> list = withdrawAddressService.selectWithdrawAddressList(withdrawAddress);
        ExcelUtil<WithdrawAddress> util = new ExcelUtil<WithdrawAddress>(WithdrawAddress.class);
        return util.exportExcel(list, "withdrawAddress");
    }

    /**
     * 获取提现地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(withdrawAddressService.selectWithdrawAddressById(id));
    }

    /**
     * 新增提现地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:add')")
    @Log(title = "提现地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WithdrawAddress withdrawAddress)
    {
        return toAjax(withdrawAddressService.insertWithdrawAddress(withdrawAddress));
    }

    /**
     * 修改提现地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:edit')")
    @Log(title = "提现地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WithdrawAddress withdrawAddress)
    {
        return toAjax(withdrawAddressService.updateWithdrawAddress(withdrawAddress));
    }

    /**
     * 删除提现地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:withdrawAddress:remove')")
    @Log(title = "提现地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(withdrawAddressService.deleteWithdrawAddressByIds(ids));
    }
}
