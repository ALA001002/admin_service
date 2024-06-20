package com.bigo.project.bigo.contract.controller;


import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.entity.TimeContractEntity;
import com.bigo.project.bigo.contract.service.ITimeContractService;
import com.bigo.project.bigo.enums.ContractStatusEnum;
import com.bigo.project.system.domain.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 限时合约查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/timeContract")
public class BigoTimeContractController extends BaseController {

    @Autowired
    private ITimeContractService contractService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PreAuthorize("@ss.hasPermi('bigo:timecontract:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimeContractEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<TimeContractEntity> list = contractService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 批量一键盈亏
     */
    @PreAuthorize("@ss.hasPermi('bigo:timecontract:edit')")
    @Log(title = "一键盈亏", businessType = BusinessType.ONE_KEY_CLOSE)
    @GetMapping("/batchOneKeyClose/{type}/{ids}")
    public AjaxResult batchOneKeyClose(@PathVariable Integer type, @PathVariable Long[] ids, HttpServletRequest request)
    {
        StringBuilder sb = new StringBuilder();

        for (Long id : ids) {
            try {
                Boolean locked = redisTemplate.opsForValue().setIfAbsent(id.toString(), "", 10, TimeUnit.SECONDS);
                if(!locked) continue;
                TimeContract contract = contractService.getById(id);
                if(contract == null){
                    logger.error("限时合约ID:{},订单不存在", id);
                    sb.append(id + ",");
                    continue;
                }
                if(contract.getStatus() != ContractStatusEnum.OPEN.getType()){
                    logger.error("限时合约ID:{},订单不是持仓状态", id);
                    sb.append(id + ",");
                    continue;
                }
                if(contract.getSettlementTime().compareTo(new Date()) <= 0 ) {
                    logger.error("限时合约ID:{},订单已过结算时间", id);
                    sb.append(id + ",");
                    continue;
                }
                contract.setSettlementType(type);
                if(contractService.getVariablePrice(contract).compareTo(BigDecimal.ZERO) <= 0 ) {
                    logger.error("限时合约ID:{},请先设置对应的交易对配置", id);
                    sb.append(id + ",");
                    continue;
                }
                // 平仓
                int status = contractService.oneKeyClose(contract, type);
            } catch (Exception e) {
                e.printStackTrace();
                sb.append(id + ",");
                continue;
            } finally {
                redisTemplate.delete(id.toString());
            }
        }
        if(sb.length() > 0) {
            return AjaxResult.error("订单ID:["+sb.toString()+"] 设置失败");
        }
        return AjaxResult.success();
    }

    /**
     * 批量一键盈亏
     */
    @PreAuthorize("@ss.hasPermi('bigo:timecontract:edit')")
    @Log(title = "一键盈亏", businessType = BusinessType.ONE_KEY_CLOSE)
    @GetMapping("/oneKeyClose/{type}/{id}")
    public AjaxResult oneKeyClose(@PathVariable Integer type, @PathVariable Long id, HttpServletRequest request)
    {
            try {
                Boolean locked = redisTemplate.opsForValue().setIfAbsent(id.toString(), "", 10, TimeUnit.SECONDS);
                if(!locked){
                    return AjaxResult.error("此订单正在结算中");
                }

                TimeContract contract = contractService.getById(id);
                if(contract == null){
                    logger.error("限时合约ID:{},订单不存在", id);
                    return AjaxResult.error("该订单不存在");
                }
                if(contract.getStatus() != ContractStatusEnum.OPEN.getType()){
                    logger.error("限时合约ID:{},订单不是持仓状态", id);
                    return AjaxResult.error("该订单不是持仓状态");
                }
                contract.setSettlementType(type);
                if(contractService.getVariablePrice(contract).compareTo(BigDecimal.ZERO) <= 0 ) {
                    logger.error("限时合约ID:{},请先设置对应的交易对配置", id);
                    return AjaxResult.error("请先设置对应的交易对配置");
                }
                if(contract.getSettlementTime().compareTo(new Date()) <= 0 ) {
                    logger.error("限时合约ID:{},订单已过结算时间", id);
                    return AjaxResult.error("该订单已过结算时间");
                }
                // 平仓
                int status = contractService.oneKeyClose(contract, type);
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxResult.error("一键盈亏出现错误");
            } finally {
                redisTemplate.delete(id.toString());
            }
        return AjaxResult.success();
    }


    /*    *//**
     * 一键止盈
     *//*
    @PreAuthorize("@ss.hasPermi('bigo:timecontract:surplus')")
    @Log(title = "一键止盈", businessType = BusinessType.ONE_KEY_CLOSE)
    @PutMapping("/oneKeyStopSurplus")
    public AjaxResult oneKeyStopSurplus(@RequestBody ContractEntity entity, HttpServletRequest request)
    {
        TimeContract contract = contractService.getById(entity.getId());
        if(contract == null){
            return AjaxResult.error("订单不存在!");
        }
        if(contract.getStatus() != 0){
            return AjaxResult.error("订单不是持仓状态!");
        }

        LoginUser user = tokenService.getLoginUser(request);
        contractService.oneKeyClose(contract, 1);
        //发出合约状态变更通知
        WebSocketServer.noticeStatusChange("TimeContract", contract.getUid());
        return AjaxResult.success();
    }*/

}
