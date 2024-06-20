package com.bigo.project.bigo.wallet.controller;

import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WithdrawEntity;
import com.bigo.project.bigo.wallet.dao.WithdrawRepository;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 后台资产查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@RestController
@RequestMapping("/withdraw")
public class WithdrawController extends BaseController {

    @Autowired
    private IWithdrawService withdrawService;



    @Autowired
    private IAgentService agentService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService sysUserService;

    @Resource
    WithdrawRepository withdrawRepository;



    @PreAuthorize("@ss.hasPermi('bigo:withdraw:list')")
    @GetMapping("/list")
    public TableDataInfo list(WithdrawEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if (agent != null) {
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
//        entity.setType(2);
        if (!StringUtils.isEmpty(entity.getUids())) {
            entity.setUids(entity.getUids().replace("，", ","));
        }
        List<WithdrawEntity> list = withdrawService.withdrawListByEntity(entity);
        List<Long> userIds = list.stream().map(t -> t.getUid()).collect(Collectors.toList());
        Map<Long, List<Map>> withdrawResultMap = new HashMap<>();
        Map<Long, List<Map>> rechargeResultMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            List<Map> withdraw = withdrawRepository.withdraw(userIds);
            List<Map> recharge = withdrawRepository.recharge(userIds);
            withdrawResultMap = withdraw.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
            rechargeResultMap = recharge.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
        }
        for (WithdrawEntity withdrawEntity : list) {
            BigDecimal actualMoney = withdrawEntity.getMoney().subtract(withdrawEntity.getFee() == null?BigDecimal.ZERO:withdrawEntity.getFee()).setScale(8, BigDecimal.ROUND_DOWN);
            withdrawEntity.setActualMoney(actualMoney);
            Map<String, BigDecimal> failMap = new HashMap<>();
            Long uid = withdrawEntity.getUid();
            for (CurrencyEnum value : CurrencyEnum.values()) {
                failMap.put(value.getCode(), withdrawService.getWithdraAmount(withdrawEntity.getUid(), value.getCode(), 2, 2, 2));
            }
            withdrawEntity.setWithdrawFail(failMap);
            withdrawEntity.setWithdrawSuccess(mergeMap(withdrawResultMap.get(uid)));
            withdrawEntity.setRechargeSuccess(mergeMap(rechargeResultMap.get(uid)));
        }
        return getDataTable(list);
    }

    public static Map mergeMap(List<Map> map){
        Map result = new HashMap();
        if(map==null){
            return result;
        }
        for(Map item:map){
            String coin = (String) item.get("coin");
            result.put(coin,item.get("value"));
        }
        return result;
    }

    @PreAuthorize("@ss.hasPermi('bigo:withdraw:auditRecord')")
    @GetMapping("/auditRecord")
    public AjaxResult auditRecord(WithdrawEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        if(!StringUtils.isEmpty(entity.getUids())) {
            entity.setUids(entity.getUids().replace("，",","));
        }
        Map auditMap = new HashMap();

        for (CurrencyEnum value : CurrencyEnum.values()) {
            entity.setCheckStatus(0);
            entity.setStatus(3);
            entity.setCoin(value.getCode());
            auditMap.put("unreviewed"+value.getCode(),withdrawService.withdrawAuditRecord(entity));

            entity.setCheckStatus(1);
            entity.setStatus(1);
            auditMap.put("success"+value.getCode(),withdrawService.withdrawAuditRecord(entity));
        }
        return AjaxResult.success(auditMap);
    }

    /**
     * 提币审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:check')")
    @Log(title = "提币审核", businessType = BusinessType.CHECK)
    @PutMapping("/checkWithdraw")
    public AjaxResult changeStatus(@RequestBody WithdrawEntity entity, HttpServletRequest request)
    {
        if(entity.getId() == null){
            return AjaxResult.error("提币申请ID不能为空");
        }
        Withdraw withdraw = withdrawService.getById(entity.getId());
        if(withdraw == null){
            return AjaxResult.error("提币申请不存在");
        }
        if(withdraw.getCheckStatus() != 0){
            return AjaxResult.error("提币申请已审核");
        }
        if(entity.getCheckStatus() == 1) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            SysUser sysUser = loginUser.getUser();
            if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
            sysUser = sysUserService.selectUserById(sysUser.getUserId());
            if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
                return AjaxResult.error("谷歌验证码不正确");
            }
        }
        LoginUser user = tokenService.getLoginUser(request);
        withdraw.setOperatorId(user.getUser().getUserId());
        withdraw.setVerifyTime(new Date());
        withdraw.setCheckStatus(entity.getCheckStatus());
        withdraw.setError(entity.getError());
        withdrawService.checkWithdraw(withdraw);
        return AjaxResult.success();
    }

    /**
     *批量提币审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:check')")
    @Log(title = "批量提币审核", businessType = BusinessType.CHECK)
    @PutMapping("/batchCheckWithdraw")
    public AjaxResult batchCheckWithdraw(@RequestBody WithdrawEntity entity, HttpServletRequest request)
    {
        if(entity.getIds() == null || entity.getIds().length <= 0){
            return AjaxResult.error("申请ID不能为空");
        }

        if(entity.getCheckStatus() == 1) {
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            SysUser sysUser = loginUser.getUser();
            if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
            sysUser = sysUserService.selectUserById(sysUser.getUserId());
            if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
                return AjaxResult.error("谷歌验证码不正确");
            }
        }

        for (Long id : entity.getIds()) {
            Withdraw withdraw = withdrawService.getById(id);
            if(withdraw == null){
                return AjaxResult.error("提币申请不存在");
            }
            if(withdraw.getCheckStatus() != 0){
                return AjaxResult.error("提币申请已审核");
            }
            LoginUser user = tokenService.getLoginUser(request);
            withdraw.setOperatorId(user.getUser().getUserId());
            withdraw.setVerifyTime(new Date());
            withdraw.setCheckStatus(entity.getCheckStatus());
            withdraw.setError(entity.getError());
            withdrawService.checkWithdraw(withdraw);
        }
        return AjaxResult.success();
    }
    /**
     * 充值记录
     */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:list')")
    @GetMapping("/listRecharge")
    public TableDataInfo listRecharge(WithdrawEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<Integer> types = new ArrayList<>();
        types.add(4);
        types.add(5);
        types.add(6);
        entity.setTypes(types);
        if(!StringUtils.isEmpty(entity.getUids())) {
            entity.setUids(entity.getUids().replace("，",","));
        }
        List<WithdrawEntity> list = withdrawService.listByEntity(entity);
        return getDataTable(list);
    }

    /**
     * 充币审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:check')")
    @Log(title = "充币审核", businessType = BusinessType.CHECK)
    @PutMapping("/checkRecharge")
    public AjaxResult checkRecharge(@RequestBody WithdrawEntity entity, HttpServletRequest request)
    {
        if(entity.getId() == null){
            return AjaxResult.error("充币申请ID不能为空");
        }
        Withdraw withdraw = withdrawService.getById(entity.getId());
        if(withdraw == null){
            return AjaxResult.error("充币申请不存在");
        }
        if(withdraw.getCheckStatus() != 0){
            return AjaxResult.error("充币申请已审核");
        }
        LoginUser user = tokenService.getLoginUser(request);
        withdraw.setOperatorId(user.getUser().getUserId());
        withdraw.setVerifyTime(new Date());
        withdraw.setCheckStatus(entity.getCheckStatus());
        withdraw.setError(entity.getError());
        withdrawService.checkRecharge(withdraw);
        return AjaxResult.success();
    }

    /**
     *批量充值审核
     */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:check')")
    @Log(title = "批量充值审核", businessType = BusinessType.CHECK)
    @PutMapping("/batchCheckRecharge")
    public AjaxResult batchCheckRecharge(@RequestBody WithdrawEntity entity, HttpServletRequest request)
    {
        if(entity.getIds() == null || entity.getIds().length <= 0){
            return AjaxResult.error("申请ID不能为空");
        }
        for (Long id : entity.getIds()) {
            Withdraw withdraw = withdrawService.getById(id);
            if(withdraw == null){
                return AjaxResult.error("充币申请不存在");
            }
            if(withdraw.getCheckStatus() != 0){
                return AjaxResult.error("充币申请已审核");
            }
            LoginUser user = tokenService.getLoginUser(request);
            withdraw.setOperatorId(user.getUser().getUserId());
            withdraw.setVerifyTime(new Date());
            withdraw.setCheckStatus(entity.getCheckStatus());
            withdraw.setError(entity.getError());
            withdrawService.checkRecharge(withdraw);
        }

        return AjaxResult.success();
    }


    @PreAuthorize("@ss.hasPermi('bigo:withdraw:agentPay')")
    @Log(title = "人工代付", businessType = BusinessType.CHECK)
    @PostMapping("/agentPayWithdraw")
    public AjaxResult agentPayWithdraw(@RequestBody WithdrawEntity entity, HttpServletRequest request)
    {

        if(StringUtils.isEmpty(entity.getToAddressArr())){
            return AjaxResult.error("请输入正确地址");
        }

        String [] toAddressArr = entity.getToAddressArr().split(",");
        if(toAddressArr.length <= 0 ){
            return AjaxResult.error("请输入正确地址");
        }
        if(entity.getMoney() == null || entity.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("请输入正确金额");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        for (String toAddress : toAddressArr) {
            entity.setToAddress(toAddress.trim());
            withdrawService.agentPayWithdraw(entity,sysUser);
        }

        return AjaxResult.success();
    }

/**
 *批量提币审核
 */
    @PreAuthorize("@ss.hasPermi('bigo:withdraw:check')")
    @Log(title = "线下打款", businessType = BusinessType.CHECK)
    @PostMapping("/offlinePay")
    public AjaxResult offlinePay(@RequestBody WithdrawEntity entity, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }
        withdrawService.offlinePay(entity, sysUser);
        return AjaxResult.success();
    }

}
