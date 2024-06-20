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
import com.bigo.project.bigo.luck.domain.LotteryCodeRecord;
import com.bigo.project.bigo.luck.service.ILotteryCodeRecordService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 抽奖码使用记录Controller
 * 
 * @author bigo
 * @date 2021-03-29
 */
@RestController
@RequestMapping("/luck/lotteryCodeRecord")
public class LotteryCodeRecordController extends BaseController
{
    @Autowired
    private ILotteryCodeRecordService lotteryCodeRecordService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;
    /**
     * 查询抽奖码使用记录列表
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(LotteryCodeRecord lotteryCodeRecord, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            lotteryCodeRecord.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<LotteryCodeRecord> list = lotteryCodeRecordService.selectLotteryCodeRecordList(lotteryCodeRecord);
        return getDataTable(list);
    }

    /**
     * 导出抽奖码使用记录列表
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:export')")
    @Log(title = "抽奖码使用记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LotteryCodeRecord lotteryCodeRecord)
    {
        List<LotteryCodeRecord> list = lotteryCodeRecordService.selectLotteryCodeRecordList(lotteryCodeRecord);
        ExcelUtil<LotteryCodeRecord> util = new ExcelUtil<LotteryCodeRecord>(LotteryCodeRecord.class);
        return util.exportExcel(list, "lotteryCodeRecord");
    }

    /**
     * 获取抽奖码使用记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(lotteryCodeRecordService.selectLotteryCodeRecordById(id));
    }

    /**
     * 新增抽奖码使用记录
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:add')")
    @Log(title = "抽奖码使用记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LotteryCodeRecord lotteryCodeRecord)
    {
        return toAjax(lotteryCodeRecordService.insertLotteryCodeRecord(lotteryCodeRecord));
    }

    /**
     * 修改抽奖码使用记录
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:edit')")
    @Log(title = "抽奖码使用记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LotteryCodeRecord lotteryCodeRecord)
    {
        return toAjax(lotteryCodeRecordService.updateLotteryCodeRecord(lotteryCodeRecord));
    }

    /**
     * 删除抽奖码使用记录
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCodeRecord:remove')")
    @Log(title = "抽奖码使用记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lotteryCodeRecordService.deleteLotteryCodeRecordByIds(ids));
    }
}
