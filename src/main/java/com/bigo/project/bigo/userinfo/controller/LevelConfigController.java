package com.bigo.project.bigo.userinfo.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.userinfo.domain.LevelConfig;
import com.bigo.project.bigo.userinfo.service.ILevelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户等级Controller
 * 
 * @author bigo
 * @date 2022-07-22
 */
@RestController
@RequestMapping("/level/config")
public class LevelConfigController extends BaseController
{
    @Autowired
    private ILevelConfigService levelConfigService;

    /**
     * 查询用户等级列表
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(LevelConfig levelConfig)
    {
        startPage();
        List<LevelConfig> list = levelConfigService.selectLevelConfigList(levelConfig);
        return getDataTable(list);
    }

    /**
     * 导出用户等级列表
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:export')")
    @Log(title = "用户等级", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LevelConfig levelConfig)
    {
        List<LevelConfig> list = levelConfigService.selectLevelConfigList(levelConfig);
        ExcelUtil<LevelConfig> util = new ExcelUtil<LevelConfig>(LevelConfig.class);
        return util.exportExcel(list, "levelConfig");
    }

    /**
     * 获取用户等级详细信息
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(levelConfigService.selectLevelConfigById(id));
    }

    /**
     * 新增用户等级
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:add')")
    @Log(title = "用户等级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LevelConfig levelConfig)
    {
        return toAjax(levelConfigService.insertLevelConfig(levelConfig));
    }

    /**
     * 修改用户等级
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:edit')")
    @Log(title = "用户等级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LevelConfig levelConfig)
    {
        return toAjax(levelConfigService.updateLevelConfig(levelConfig));
    }

    /**
     * 删除用户等级
     */
    @PreAuthorize("@ss.hasPermi('userinfo:levelConfig:remove')")
    @Log(title = "用户等级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(levelConfigService.deleteLevelConfigByIds(ids));
    }
}
