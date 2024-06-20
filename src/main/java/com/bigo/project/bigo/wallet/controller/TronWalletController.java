package com.bigo.project.bigo.wallet.controller;

import com.bigo.framework.web.controller.BaseController;
import com.bigo.project.bigo.wallet.domain.PushData;
import com.bigo.project.bigo.wallet.service.INotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: 后台钱包查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@Slf4j
@RestController
@RequestMapping("/api/wallet/trc20/")
public class TronWalletController extends BaseController {

    @Resource
    INotifyService notifyService;

    @RequestMapping(value = "notify")
    public String notify(@RequestBody PushData pushData){
//        return notifyService.notify(pushData);
        return notifyService.sendNotify(pushData);
    }



}
