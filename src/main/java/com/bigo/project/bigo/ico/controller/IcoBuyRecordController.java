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
import com.bigo.project.bigo.ico.domain.IcoBuyRecord;
import com.bigo.project.bigo.ico.service.IIcoBuyRecordService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 现货预售购买记录Controller
 * 
 * @author bigo
 * @date 2023-05-11
 */
@RestController
@RequestMapping("/ico/IcobuyRecord")
public class IcoBuyRecordController extends BaseController
{
    @Autowired
    private IIcoBuyRecordService icoBuyRecordService;

    /**
     * 查询现货预售购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(IcoBuyRecord icoBuyRecord)
    {
        startPage();
        List<IcoBuyRecord> list = icoBuyRecordService.selectIcoBuyRecordList(icoBuyRecord);
        return getDataTable(list);
    }

    /**
     * 导出现货预售购买记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:export')")
    @Log(title = "现货预售购买记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(IcoBuyRecord icoBuyRecord)
    {
        List<IcoBuyRecord> list = icoBuyRecordService.selectIcoBuyRecordList(icoBuyRecord);
        ExcelUtil<IcoBuyRecord> util = new ExcelUtil<IcoBuyRecord>(IcoBuyRecord.class);
        return util.exportExcel(list, "IcobuyRecord");
    }

    /**
     * 获取现货预售购买记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(icoBuyRecordService.selectIcoBuyRecordById(id));
    }

    /**
     * 新增现货预售购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:add')")
    @Log(title = "现货预售购买记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IcoBuyRecord icoBuyRecord)
    {
        return toAjax(icoBuyRecordService.insertIcoBuyRecord(icoBuyRecord));
    }

    /**
     * 修改现货预售购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:edit')")
    @Log(title = "现货预售购买记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IcoBuyRecord icoBuyRecord)
    {
        return toAjax(icoBuyRecordService.updateIcoBuyRecord(icoBuyRecord));
    }

    /**
     * 删除现货预售购买记录
     */
    @PreAuthorize("@ss.hasPermi('ico:IcobuyRecord:remove')")
    @Log(title = "现货预售购买记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(icoBuyRecordService.deleteIcoBuyRecordByIds(ids));
    }
}
