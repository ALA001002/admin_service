package com.bigo.project.bigo.luck.controller;

import java.util.List;

import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.system.domain.SysUser;
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
import com.bigo.project.bigo.luck.domain.WinningRecord;
import com.bigo.project.bigo.luck.service.IWinningRecordService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 中奖记录Controller
 * 
 * @author bigo
 * @date 2020-12-31
 */
@RestController
@RequestMapping("/luck/record")
public class WinningRecordController extends BaseController
{
    @Autowired
    private IWinningRecordService winningRecordService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    /**
     * 查询中奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('luck:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(WinningRecord winningRecord, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            winningRecord.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<WinningRecord> list = winningRecordService.selectWinningRecordList(winningRecord);
        return getDataTable(list);
    }

    /**
     * 导出中奖记录列表
     */
    @PreAuthorize("@ss.hasPermi('luck:record:export')")
    @Log(title = "中奖记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WinningRecord winningRecord)
    {
        List<WinningRecord> list = winningRecordService.selectWinningRecordList(winningRecord);
        ExcelUtil<WinningRecord> util = new ExcelUtil<WinningRecord>(WinningRecord.class);
        return util.exportExcel(list, "record");
    }

    /**
     * 获取中奖记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('luck:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(winningRecordService.selectWinningRecordById(id));
    }

    /**
     * 新增中奖记录
     */
    @PreAuthorize("@ss.hasPermi('luck:record:add')")
    @Log(title = "中奖记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WinningRecord winningRecord)
    {
        return toAjax(winningRecordService.insertWinningRecord(winningRecord));
    }

    /**
     * 修改中奖记录
     */
    @PreAuthorize("@ss.hasPermi('luck:record:edit')")
    @Log(title = "中奖记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WinningRecord winningRecord)
    {
        return toAjax(winningRecordService.updateWinningRecord(winningRecord));
    }

    /**
     * 删除中奖记录
     */
    @PreAuthorize("@ss.hasPermi('luck:record:remove')")
    @Log(title = "中奖记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(winningRecordService.deleteWinningRecordByIds(ids));
    }
}
