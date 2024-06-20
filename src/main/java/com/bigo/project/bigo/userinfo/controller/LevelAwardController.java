package com.bigo.project.bigo.userinfo.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.userinfo.domain.LevelAward;
import com.bigo.project.bigo.userinfo.service.ILevelAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 等级赠送奖励Controller
 * 
 * @author bigo
 * @date 2023-11-25
 */
@RestController
@RequestMapping("/level/award")
public class LevelAwardController extends BaseController
{
    @Autowired
    private ILevelAwardService levelAwardService;

    /**
     * 查询等级赠送奖励列表
     */
    @PreAuthorize("@ss.hasPermi('level:award:list')")
    @GetMapping("/list")
    public TableDataInfo list(LevelAward levelAward)
    {
        startPage();
        List<LevelAward> list = levelAwardService.selectLevelAwardList(levelAward);
        return getDataTable(list);
    }

    /**
     * 导出等级赠送奖励列表
     */
    @PreAuthorize("@ss.hasPermi('level:award:export')")
    @Log(title = "等级赠送奖励", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LevelAward levelAward)
    {
        List<LevelAward> list = levelAwardService.selectLevelAwardList(levelAward);
        ExcelUtil<LevelAward> util = new ExcelUtil<LevelAward>(LevelAward.class);
        return util.exportExcel(list, "award");
    }

    /**
     * 获取等级赠送奖励详细信息
     */
    @PreAuthorize("@ss.hasPermi('level:award:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(levelAwardService.selectLevelAwardById(id));
    }

    /**
     * 新增等级赠送奖励
     */
    @PreAuthorize("@ss.hasPermi('level:award:add')")
    @Log(title = "等级赠送奖励", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LevelAward levelAward)
    {
        return toAjax(levelAwardService.insertLevelAward(levelAward));
    }

    /**
     * 修改等级赠送奖励
     */
    @PreAuthorize("@ss.hasPermi('level:award:edit')")
    @Log(title = "等级赠送奖励", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LevelAward levelAward)
    {
        return toAjax(levelAwardService.updateLevelAward(levelAward));
    }

    /**
     * 删除等级赠送奖励
     */
    @PreAuthorize("@ss.hasPermi('level:award:remove')")
    @Log(title = "等级赠送奖励", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(levelAwardService.deleteLevelAwardByIds(ids));
    }
}
