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
import com.bigo.project.bigo.wallet.domain.ToAddressInfo;
import com.bigo.project.bigo.wallet.service.IToAddressInfoService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 收币地址Controller
 * 
 * @author bigo
 * @date 2022-04-28
 */
@RestController
@RequestMapping("/wallet/toAddress")
public class ToAddressInfoController extends BaseController
{
    @Autowired
    private IToAddressInfoService toAddressInfoService;

    /**
     * 查询收币地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:list')")
    @GetMapping("/list")
    public TableDataInfo list(ToAddressInfo toAddressInfo)
    {
        startPage();
        List<ToAddressInfo> list = toAddressInfoService.selectToAddressInfoList(toAddressInfo);
        return getDataTable(list);
    }

    /**
     * 导出收币地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:export')")
    @Log(title = "收币地址", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ToAddressInfo toAddressInfo)
    {
        List<ToAddressInfo> list = toAddressInfoService.selectToAddressInfoList(toAddressInfo);
        ExcelUtil<ToAddressInfo> util = new ExcelUtil<ToAddressInfo>(ToAddressInfo.class);
        return util.exportExcel(list, "toAddress");
    }

    /**
     * 获取收币地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(toAddressInfoService.selectToAddressInfoById(id));
    }

    /**
     * 新增收币地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:add')")
    @Log(title = "收币地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ToAddressInfo toAddressInfo)
    {
        return toAjax(toAddressInfoService.insertToAddressInfo(toAddressInfo));
    }

    /**
     * 修改收币地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:edit')")
    @Log(title = "收币地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ToAddressInfo toAddressInfo)
    {
        return toAjax(toAddressInfoService.updateToAddressInfo(toAddressInfo));
    }

    /**
     * 删除收币地址
     */
    @PreAuthorize("@ss.hasPermi('wallet:toAddress:remove')")
    @Log(title = "收币地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(toAddressInfoService.deleteToAddressInfoByIds(ids));
    }
}
