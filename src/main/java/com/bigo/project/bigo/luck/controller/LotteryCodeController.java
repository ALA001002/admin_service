package com.bigo.project.bigo.luck.controller;

import java.util.List;

import com.bigo.common.utils.StringUtils;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
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
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.web.page.TableDataInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 抽奖码Controller
 * 
 * @author bigo
 * @date 2020-12-30
 */
@RestController
@RequestMapping("/luck/lotteryCode")
public class LotteryCodeController extends BaseController
{
    @Autowired
    private ILotteryCodeService lotteryCodeService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    /**
     * 查询抽奖码列表
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:list')")
    @GetMapping("/list")
    public TableDataInfo list(LotteryCode lotteryCode, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            lotteryCode.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<LotteryCode> list = lotteryCodeService.selectLotteryCodeList(lotteryCode);
        return getDataTable(list);
    }

    /**
     * 导出抽奖码列表
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:export')")
    @Log(title = "抽奖码", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(LotteryCode lotteryCode)
    {
        List<LotteryCode> list = lotteryCodeService.selectLotteryCodeList(lotteryCode);
        ExcelUtil<LotteryCode> util = new ExcelUtil<LotteryCode>(LotteryCode.class);
        return util.exportExcel(list, "lotteryCode");
    }

    /**
     * 获取抽奖码详细信息
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(lotteryCodeService.selectLotteryCodeById(id));
    }

    /**
     * 新增抽奖码
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:add')")
    @Log(title = "抽奖码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody LotteryCode lotteryCode, HttpServletRequest request)
    {
        BigoUser user = bigoUserService.getUserByUid(lotteryCode.getUid());
        if(user == null){
            return AjaxResult.error("用户不存在");
        }
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //查询是否是该代理商下面的会员
        if(agent != null){
            BigoUserEntity entity = new BigoUserEntity();
            entity.setAgentId(agent.getAgentCode());
            entity.setUid(user.getUid());
            List<BigoUserEntity> entityList = bigoUserService.listByEntity(entity);
            if (entityList.size() <= 0) return AjaxResult.error("操作失败，非自己代理底下会员!");
        }
        return toAjax(lotteryCodeService.insertLotteryCode(lotteryCode));
    }

    /**
     * 修改抽奖码
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:edit')")
    @Log(title = "抽奖码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody LotteryCode lotteryCode)
    {
        BigoUser user = bigoUserService.getUserByUid(lotteryCode.getUid());
        if(user == null){
            return AjaxResult.error("用户不存在");
        }
        return toAjax(lotteryCodeService.updateLotteryCode(lotteryCode));
    }

    /**
     * 删除抽奖码
     */
    @PreAuthorize("@ss.hasPermi('luck:lotteryCode:remove')")
    @Log(title = "抽奖码", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(lotteryCodeService.deleteLotteryCodeByIds(ids));
    }
}
