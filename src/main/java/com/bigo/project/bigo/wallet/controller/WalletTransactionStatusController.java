package com.bigo.project.bigo.wallet.controller;

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
import com.bigo.project.bigo.wallet.domain.WalletTransactionStatus;
import com.bigo.project.bigo.wallet.service.IWalletTransactionStatusService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 钱包交易状态Controller
 * 
 * @author bigo
 * @date 2021-01-25
 */
@RestController
@RequestMapping("/wallet/transactionStatus")
public class WalletTransactionStatusController extends BaseController
{
    @Autowired
    private IWalletTransactionStatusService walletTransactionStatusService;


    /**
     * 查询钱包交易状态列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:list')")
    @GetMapping("/list")
    public TableDataInfo list(WalletTransactionStatus walletTransactionStatus)
    {
        startPage();
        List<WalletTransactionStatus> list = walletTransactionStatusService.selectWalletTransactionStatusList(walletTransactionStatus);
        return getDataTable(list);
    }

    /**
     * 导出钱包交易状态列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:export')")
    @Log(title = "钱包交易状态", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WalletTransactionStatus walletTransactionStatus)
    {
        List<WalletTransactionStatus> list = walletTransactionStatusService.selectWalletTransactionStatusList(walletTransactionStatus);
        ExcelUtil<WalletTransactionStatus> util = new ExcelUtil<WalletTransactionStatus>(WalletTransactionStatus.class);
        return util.exportExcel(list, "transactionStatus");
    }

    /**
     * 获取钱包交易状态详细信息
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(walletTransactionStatusService.selectWalletTransactionStatusById(id));
    }

    /**
     * 新增钱包交易状态
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:add')")
    @Log(title = "钱包交易状态", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WalletTransactionStatus walletTransactionStatus)
    {
        return toAjax(walletTransactionStatusService.insertWalletTransactionStatus(walletTransactionStatus));
    }

    /**
     * 修改钱包交易状态
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:edit')")
    @Log(title = "钱包交易状态", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WalletTransactionStatus walletTransactionStatus)
    {
        return toAjax(walletTransactionStatusService.updateWalletTransactionStatus(walletTransactionStatus));
    }

    /**
     * 删除钱包交易状态
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:remove')")
    @Log(title = "钱包交易状态", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(walletTransactionStatusService.deleteWalletTransactionStatusByIds(ids));
    }

    /**
     * 手动同步处理区块
     * @param id
     * @return
     */
    @PreAuthorize("@ss.hasPermi('wallet:transactionStatus:update')")
    @GetMapping(value = "/synchronizeBlock/{id}")
    public AjaxResult synchronizeBlock(@PathVariable("id") Long id)
    {
        WalletTransactionStatus transactionStatus = walletTransactionStatusService.selectWalletTransactionStatusById(id);
        if(transactionStatus == null) return AjaxResult.error("同步失败，该订单不存在！");
        walletTransactionStatusService.synchronizeBlock(transactionStatus);
        return AjaxResult.success();
    }

}
