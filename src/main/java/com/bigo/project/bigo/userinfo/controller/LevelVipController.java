package com.bigo.project.bigo.userinfo.controller;

import java.util.List;

import com.bigo.project.bigo.userinfo.domain.LevelVip;
import com.bigo.project.bigo.userinfo.service.ILevelVipService;
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
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

/**
 * vip等级返佣Controller
 * 
 * @author bigo
 * @date 2023-11-25
 */
@RestController
@RequestMapping("/level/levelVip")
public class LevelVipController extends BaseController
{
    @Autowired
    private ILevelVipService levelVipService;

    /**
     * 查询vip等级返佣列表
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:list')")
    @GetMapping("/list")
    public TableDataInfo list(LevelVip levelVip)
    {
        startPage();
        List<LevelVip> list = levelVipService.selectLevelVipList(levelVip);
        return getDataTable(list);
    }

    /**
     * 导出vip等级返佣列表
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:export')")
    @Log(title = "vip等级返佣", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LevelVip levelVip)
    {
        List<LevelVip> list = levelVipService.selectLevelVipList(levelVip);
        ExcelUtil<LevelVip> util = new ExcelUtil<LevelVip>(LevelVip.class);
        return util.exportExcel(list, "levelVip");
    }

    /**
     * 获取vip等级返佣详细信息
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(levelVipService.selectLevelVipById(id));
    }

    /**
     * 新增vip等级返佣
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:add')")
    @Log(title = "vip等级返佣", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LevelVip levelVip)
    {
        return toAjax(levelVipService.insertLevelVip(levelVip));
    }

    /**
     * 修改vip等级返佣
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:edit')")
    @Log(title = "vip等级返佣", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LevelVip levelVip)
    {
        return toAjax(levelVipService.updateLevelVip(levelVip));
    }

    /**
     * 删除vip等级返佣
     */
    @PreAuthorize("@ss.hasPermi('level:levelVip:remove')")
    @Log(title = "vip等级返佣", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(levelVipService.deleteLevelVipByIds(ids));
    }
}
