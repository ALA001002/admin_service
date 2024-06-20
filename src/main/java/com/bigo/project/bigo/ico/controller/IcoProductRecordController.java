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
import com.bigo.project.bigo.ico.domain.IcoProductRecord;
import com.bigo.project.bigo.ico.service.IIcoProductRecordService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * ico产品购买记录Controller
 * 
 * @author bigo
 * @date 2023-01-09
 */
@RestController
@RequestMapping("/ico/icoProductRecord")
public class IcoProductRecordController extends BaseController
{
    @Autowired
    private IIcoProductRecordService icoProductRecordService;

    /**
     * 查询ico产品购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(IcoProductRecord icoProductRecord)
    {
        startPage();
        List<IcoProductRecord> list = icoProductRecordService.selectIcoProductRecordList(icoProductRecord);
        return getDataTable(list);
    }

    /**
     * 导出ico产品购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:export')")
    @Log(title = "ico产品购买记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(IcoProductRecord icoProductRecord)
    {
        List<IcoProductRecord> list = icoProductRecordService.selectIcoProductRecordList(icoProductRecord);
        ExcelUtil<IcoProductRecord> util = new ExcelUtil<IcoProductRecord>(IcoProductRecord.class);
        return util.exportExcel(list, "icoProductRecord");
    }

    /**
     * 获取ico产品购买记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(icoProductRecordService.selectIcoProductRecordById(id));
    }

    /**
     * 新增ico产品购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:add')")
    @Log(title = "ico产品购买记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IcoProductRecord icoProductRecord)
    {
        return toAjax(icoProductRecordService.insertIcoProductRecord(icoProductRecord));
    }

    /**
     * 修改ico产品购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:edit')")
    @Log(title = "ico产品购买记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IcoProductRecord icoProductRecord)
    {
        return toAjax(icoProductRecordService.updateIcoProductRecord(icoProductRecord));
    }

    /**
     * 删除ico产品购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:icoProductRecord:remove')")
    @Log(title = "ico产品购买记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(icoProductRecordService.deleteIcoProductRecordByIds(ids));
    }
}
