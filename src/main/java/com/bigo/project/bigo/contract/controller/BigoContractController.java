package com.bigo.project.bigo.contract.controller;

import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.framework.websocket.server.WebSocketServer;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.Frame;
import com.bigo.project.bigo.contract.entity.ContractEntity;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.contract.service.IFrameService;
import com.bigo.project.bigo.enums.ContractStatusEnum;
import com.bigo.project.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 后台订单查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/contract")
public class BigoContractController extends BaseController {

    @Autowired
    private IContractService contractService;

    @Autowired
    private IFrameService frameService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @PreAuthorize("@ss.hasPermi('bigo:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentId());
        }
        startPage();
        List<ContractEntity> list = contractService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 一键止损/强平
     */
    @PreAuthorize("@ss.hasPermi('bigo:contract:loss')")
    @Log(title = "一键止损/强平", businessType = BusinessType.ONE_KEY_CLOSE)
    @PutMapping("/oneKeyStopLoss")
    public AjaxResult oneKeyStopLoss(@RequestBody ContractEntity entity, HttpServletRequest request)
    {
        Contract contract = contractService.getById(entity.getId());
        if(contract == null){
            return AjaxResult.error("订单不存在!");
        }
        if(contract.getStatus() != 0){
            return AjaxResult.error("订单不是持仓状态!");
        }
        //有止损价按止损价平仓，没有止损价就以强平价平仓
        BigDecimal closePrice;
        if(contract.getStopLoss() != null){
            closePrice = contract.getStopLoss();
            contract.setStatus(ContractStatusEnum.TRIGGER_CLOSE.getType());
        }else{
            closePrice = contract.getPredictPrice();
            contract.setStatus(ContractStatusEnum.FORCE_CLOSE.getType());
        }
        LoginUser user = tokenService.getLoginUser(request);
        contractService.oneKeyClose(contract, closePrice, 1,user.getUser().getUserId());
        //发出合约状态变更通知
        WebSocketServer.noticeStatusChange("Contract", contract.getUid());
        return AjaxResult.success();
    }

    /**
     * 一键止盈
     */
    @PreAuthorize("@ss.hasPermi('bigo:contract:surplus')")
    @Log(title = "一键止盈", businessType = BusinessType.ONE_KEY_CLOSE)
    @PutMapping("/oneKeyStopSurplus")
    public AjaxResult oneKeyStopSurplus(@RequestBody ContractEntity entity, HttpServletRequest request)
    {
        Contract contract = contractService.getById(entity.getId());
        if(contract == null){
            return AjaxResult.error("订单不存在!");
        }
        if(contract.getStatus() != 0){
            return AjaxResult.error("订单不是持仓状态!");
        }
        if(contract.getStopSurplus() == null){
            return AjaxResult.error("未设置止盈价的订单无法止盈!");
        }
        LoginUser user = tokenService.getLoginUser(request);
        contract.setStatus(ContractStatusEnum.TRIGGER_CLOSE.getType());
        contractService.oneKeyClose(contract, contract.getStopSurplus(), 2, user.getUser().getUserId());
        //发出合约状态变更通知
        WebSocketServer.noticeStatusChange("Contract", contract.getUid());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('bigo:frame:list')")
    @GetMapping("/frameList")
    public TableDataInfo frameList(Frame entity)
    {
        startPage();
        List<Frame> list = frameService.listByParam(entity);
        return getDataTable(list);
    }

}
