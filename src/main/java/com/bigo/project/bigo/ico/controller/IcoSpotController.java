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
import com.bigo.project.bigo.ico.domain.IcoSpot;
import com.bigo.project.bigo.ico.service.IIcoSpotService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * 现货交易记录Controller
 * 
 * @author bigo
 * @date 2023-03-14
 */
@RestController
@RequestMapping("/ico/spot")
public class IcoSpotController extends BaseController
{
    @Autowired
    private IIcoSpotService icoSpotService;

    /**
     * 查询现货交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:list')")
    @GetMapping("/list")
    public TableDataInfo list(IcoSpot icoSpot)
    {
        startPage();
        List<IcoSpot> list = icoSpotService.selectIcoSpotList(icoSpot);
        return getDataTable(list);
    }

    /**
     * 导出现货交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:export')")
    @Log(title = "现货交易记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(IcoSpot icoSpot)
    {
        List<IcoSpot> list = icoSpotService.selectIcoSpotList(icoSpot);
        ExcelUtil<IcoSpot> util = new ExcelUtil<IcoSpot>(IcoSpot.class);
        return util.exportExcel(list, "spot");
    }

    /**
     * 获取现货交易记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(icoSpotService.selectIcoSpotById(id));
    }

    /**
     * 新增现货交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:add')")
    @Log(title = "现货交易记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IcoSpot icoSpot)
    {
        return toAjax(icoSpotService.insertIcoSpot(icoSpot));
    }

    /**
     * 修改现货交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:edit')")
    @Log(title = "现货交易记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IcoSpot icoSpot)
    {
        return toAjax(icoSpotService.updateIcoSpot(icoSpot));
    }

    /**
     * 删除现货交易记录
     */
    @PreAuthorize("@ss.hasPermi('ico:spot:remove')")
    @Log(title = "现货交易记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(icoSpotService.deleteIcoSpotByIds(ids));
    }
}
