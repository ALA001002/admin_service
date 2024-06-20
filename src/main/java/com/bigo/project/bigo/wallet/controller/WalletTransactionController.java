package com.bigo.project.bigo.wallet.controller;

import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.domain.WalletTransactionStatus;
import com.bigo.project.bigo.wallet.service.ITronTransactionService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import com.bigo.project.system.domain.SysUser;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @description: 交易控制器
 * @author: wenxm
 * @date: 2020/8/20 17:07
 */
@RestController
@RequestMapping("/transaction")
public class WalletTransactionController extends BaseController {

    @Autowired
    private IWalletTransactionService transactionService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private ITronTransactionService tronTransactionService;

    @PreAuthorize("@ss.hasPermi('bigo:transaction:list')")
    @GetMapping("/list")
    public TableDataInfo list(WalletTransaction entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setTopUid(agent.getAgentCode());
        }
        startPage();
        List<WalletTransaction> list = transactionService.listTransaction(entity);
        return getDataTable(list);
    }


    /**
     * 导出钱包交易状态列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:transaction:export')")
    @Log(title = "钱包交易记录", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WalletTransaction entity) {
        List<Long> lowerUids = bigoUserService.getLowerUids(entity.getParentUid());
        if(lowerUids == null || lowerUids.size() < 1) {
            return AjaxResult.error();
        }
        StringBuilder sb = new StringBuilder();
        for (Long lowerUid : lowerUids) {
            sb.append(lowerUid.toString()).append(",");
        }
        String uids = sb.substring(0, sb.length() -1);
        startPage(1,99999);
        entity.setLowerUids(uids);
//        entity.setType(1);
//        entity.setStatus(2);
        entity.setHandleStatus(1);
        List<WalletTransaction> list = transactionService.subordinateRecordList(entity);
        ExcelUtil<WalletTransaction> util = new ExcelUtil<WalletTransaction>(WalletTransaction.class);
        return util.exportExcel(list, "transaction");
    }

    @PreAuthorize("@ss.hasPermi('bigo:transaction:subordinateRecordList')")
    @GetMapping("/subordinateRecordList")
    public TableDataInfo subordinateRecordList(WalletTransaction entity)
    {
        List<Long> lowerUids = bigoUserService.getLowerUids(entity.getParentUid());
        if(lowerUids == null || lowerUids.size() < 1) {
            return getDataTable(new ArrayList<>());
        }
        StringBuilder sb = new StringBuilder();
        for (Long lowerUid : lowerUids) {
            sb.append(lowerUid.toString()).append(",");
        }
        String uids = sb.substring(0, sb.length() -1);
        startPage();
        entity.setLowerUids(uids);
//        entity.setType(1);
//        entity.setStatus(2);
        entity.setHandleStatus(1);
        List<WalletTransaction> list = transactionService.subordinateRecordList(entity);
        return getDataTable(list);
    }

    /**
     * 导出钱包交易状态列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:transaction:export')")
    @Log(title = "钱包交易记录", businessType = BusinessType.EXPORT)
    @GetMapping("/lowerExport")
    public AjaxResult lowerExport(WalletTransaction entity) {
        List<Long> lowerUids = bigoUserService.getLowerUids(entity.getParentUid());
        if(lowerUids == null || lowerUids.size() < 1) {
            return AjaxResult.error();
        }
        StringBuilder sb = new StringBuilder();
        for (Long lowerUid : lowerUids) {
            sb.append(lowerUid.toString()).append(",");
        }
        String uids = sb.substring(0, sb.length() -1);
        startPage(1,99999);
        entity.setLowerUids(uids);
//        entity.setType(1);
//        entity.setStatus(2);
        entity.setHandleStatus(1);
        List<WalletTransaction> list = transactionService.subordinateRecordList(entity);

        TronTransaction tronEntity = new TronTransaction();
        tronEntity.setLowerUids(uids);
        tronEntity.setScore(1);
        tronEntity.setSymbol(entity.getCoin());
        List<TronTransaction> tronList = tronTransactionService.subordinateRecordList(tronEntity);
        if(tronList != null && tronList.size() > 0) {
            WalletTransaction transaction = null;
            for (TronTransaction tronTransaction : tronList) {
                transaction = new WalletTransaction();
                transaction.setId(tronTransaction.getId());
                transaction.setUid(tronTransaction.getUid());
                transaction.setUsername(tronTransaction.getUsername());
                transaction.setParentUid(tronTransaction.getParentUid());
                transaction.setTopUid(tronTransaction.getTopUid());
                transaction.setCoin(tronTransaction.getSymbol());
                transaction.setType(1);
                transaction.setMoney(tronTransaction.getAmount());
                transaction.setConvertedPrice(tronTransaction.getAmount());
                transaction.setFrom(tronTransaction.getFromAddress());
                transaction.setTo(tronTransaction.getToAddress());
                transaction.setStatus(2);
                transaction.setCreateTime(tronTransaction.getCreatedAt());
                list.add(transaction);
            }
        }
        list.sort(Comparator.comparing(WalletTransaction::getCreateTime));
        Collections.reverse(list);

        for (WalletTransaction transaction : list) {
            // 获取所有的上级
            List<BigoUser> parents = bigoUserService.listParentUids(transaction.getUid());
            Long salesmanId = null;
            for (BigoUser parent : parents) {
                if(parent.getParentUid().equals(transaction.getTopUid())) {
                    salesmanId = parent.getUid();
                    break;
                }
            }
            transaction.setSalesmanId(salesmanId);
        }
        ExcelUtil<WalletTransaction> util = new ExcelUtil<WalletTransaction>(WalletTransaction.class);
        return util.exportExcel(list, "transaction");
    }

    @PreAuthorize("@ss.hasPermi('bigo:transaction:subordinateRecordList')")
    @GetMapping("/lowerRecordList")
    public TableDataInfo lowerRecordList(WalletTransaction entity)
    {
        List<Long> lowerUids = bigoUserService.getLowerUids(entity.getParentUid());
        if(lowerUids == null || lowerUids.size() < 1) {
            return getDataTable(new ArrayList<>());
        }
        StringBuilder sb = new StringBuilder();
        for (Long lowerUid : lowerUids) {
            sb.append(lowerUid.toString()).append(",");
        }
        String uids = sb.substring(0, sb.length() -1);
//        startPage();
        entity.setLowerUids(uids);
//        entity.setType(1);
//        entity.setStatus(2);
        entity.setHandleStatus(1);
        List<WalletTransaction> list = transactionService.subordinateRecordList(entity);
        TronTransaction tronEntity = new TronTransaction();
        tronEntity.setLowerUids(uids);
        tronEntity.setScore(1);
        tronEntity.setSymbol(entity.getCoin());
        List<TronTransaction> tronList = tronTransactionService.subordinateRecordList(tronEntity);
        if(tronList != null && tronList.size() > 0) {
            WalletTransaction transaction = null;
            for (TronTransaction tronTransaction : tronList) {
                transaction = new WalletTransaction();
                transaction.setId(tronTransaction.getId());
                transaction.setUid(tronTransaction.getUid());
                transaction.setUsername(tronTransaction.getUsername());
                transaction.setParentUid(tronTransaction.getParentUid());
                transaction.setTopUid(tronTransaction.getTopUid());
                transaction.setCoin(tronTransaction.getSymbol());
                transaction.setType(1);
                transaction.setMoney(tronTransaction.getAmount());
                transaction.setConvertedPrice(tronTransaction.getAmount());
                transaction.setFrom(tronTransaction.getFromAddress());
                transaction.setTo(tronTransaction.getToAddress());
                transaction.setStatus(2);
                transaction.setCreateTime(tronTransaction.getCreatedAt());
                list.add(transaction);
            }
        }
        list.sort(Comparator.comparing(WalletTransaction::getCreateTime));
        Collections.reverse(list);

        for (WalletTransaction transaction : list) {
            // 获取所有的上级
            List<BigoUser> parents = bigoUserService.listParentUids(transaction.getUid());
            Long salesmanId = null;
            for (BigoUser parent : parents) {
                if(parent.getParentUid().equals(transaction.getTopUid())) {
                    salesmanId = parent.getUid();
                    break;
                }
            }
            transaction.setSalesmanId(salesmanId);
        }
        return getDataTable(list);
    }

}
