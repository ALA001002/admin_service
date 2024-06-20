package com.bigo.project.bigo.wallet.controller;

import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.wallet.service.TransactionService;
import com.bigo.project.bigo.wallet.view.TransactionReq;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value="/transaction")
public class TransactionController {

    @Resource
    TransactionService transactionService;

    @RequestMapping(value="/withdraws")
    public AjaxResult withdraws(@RequestBody TransactionReq req){
        return transactionService.withdraws(req);
    }

    @RequestMapping(value="/recharges")
    public AjaxResult recharges(@RequestBody TransactionReq req){
        return transactionService.recharges(req);
    }

    @RequestMapping(value="/collect")
    public AjaxResult collect(@RequestBody TransactionReq req){
        return transactionService.collect(req);
    }
}
