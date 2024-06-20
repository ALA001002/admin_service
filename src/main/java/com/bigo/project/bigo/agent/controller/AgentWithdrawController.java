package com.bigo.project.bigo.agent.controller;

import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.domain.AgentWithdraw;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.agent.service.IAgentWithdrawService;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.system.domain.SysUser;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/10 16:08
 */
@RestController
@RequestMapping("/agentWithdraw/")
public class AgentWithdrawController extends BaseController {

    @Autowired
    private IAgentWithdrawService withdrawService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    /**
     * 获取代理商提现列表
     */
    @PreAuthorize("@ss.hasPermi('agent:withdraw:list')")
    @GetMapping("/list")
    public Map list(AgentWithdraw param, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = loginUser.getUser();
        Agent agent = agentService.getByUserId(user.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            param.setAgentId(agent.getAgentId());
        }
        startPage();
        List<AgentWithdraw> list = withdrawService.listByEntity(param);
        Map<String, Object> map = Maps.newHashMap();
        TableDataInfo data = getDataTable(list);
        map.put("tableDate",data);
        map.put("agent", agent);
        return map;
    }

    /**
     * 提币审核
     */
    @PreAuthorize("@ss.hasPermi('agent:withdraw:check')")
    @Log(title = "提币审核", businessType = BusinessType.CHECK)
    @PutMapping("/checkWithdraw")
    public AjaxResult changeStatus(@RequestBody AgentWithdraw entity, HttpServletRequest request)
    {
        if(entity.getId() == null){
            return AjaxResult.error("提币申请ID不能为空");
        }
        AgentWithdraw withdraw = withdrawService.getById(entity.getId());
        if(withdraw == null){
            return AjaxResult.error("提币申请不存在");
        }
        if(withdraw.getStatus() != 0){
            return AjaxResult.error("提币申请已审核");
        }
        LoginUser user = tokenService.getLoginUser(request);
        withdraw.setOperatorId(user.getUser().getUserId());
        withdraw.setStatus(entity.getStatus());
        withdraw.setHash(entity.getHash());
        withdrawService.checkWithdraw(withdraw);
        return AjaxResult.success();
    }


}
