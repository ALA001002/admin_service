package com.bigo.project.bigo.api;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.CurrencyUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.ContractStopParam;
import com.bigo.project.bigo.api.domain.TimeContractBuyParam;
import com.bigo.project.bigo.api.vo.ContractPlanVO;
import com.bigo.project.bigo.api.vo.TimePeriodVO;
import com.bigo.project.bigo.config.entity.ConfigSetting;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.domain.TimePeriod;
import com.bigo.project.bigo.contract.service.IContractPlanService;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.contract.service.ITimeContractService;
import com.bigo.project.bigo.contract.service.ITimePeriodService;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.entity.BigoUserEntity;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 限时合约api
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@Slf4j
@RestController
@RequestMapping("/api/timeContract/")
public class TimeContractApiController {


    @Autowired
    private TokenService tokenService;


    @Autowired
    private ITimeContractService contractService;

    @Autowired
    private ITimePeriodService periodService;


    @Autowired
    private IBigoUserService bigoUserService;


    /**
     * 合约列表
     * @param param
     * @return
     */
    @GetMapping("/listContract")
    public AjaxResult listContract(TimeContract param){
        if(param == null){
            param = new TimeContract();
        }
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        param.setUid(user.getUid());
        List<TimeContract> contractList = contractService.listContract(param);
        return AjaxResult.success(contractList);
    }

    /**
     * 购买合约
     * @param param
     * @return
     */
    @PostMapping("/buyContract")
    public AjaxResult myAccountList(@Validated @RequestBody TimeContractBuyParam param, HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        param.setUid(user.getUid());
        //先校验是否支持该币种
        CurrencyUtils.validateCurrency(param.getCurrency(), OperateEnum.BUY_TIME_CONTRACT);
//        BigDecimal min = CoinUtils.getTimeMinAmountUsdt();
//        if(param.getAmount().compareTo(min) < 0){
//            return AjaxResult.error("the_amount_is_below_the_minimum_limit");
//        }
//        if(param.getAmount().compareTo(max) > 0){
//            return AjaxResult.error("the_amount_is_greater_than_the_maximum_limit");
//        }
        //未实名认证无法进行otc交易
//        if(user.getAuthStatus() != 2){
//            return AjaxResult.error("please_complete_real_name_authentication_first");
//        }
        /*if(!SymbolEnum.isSupTimeContract(param.getSymbolCode())){
            return AjaxResult.error("unsupport_symbol");
        }*/
        BigoUser entity = bigoUserService.getUserByUid(user.getUid());
        if(entity.getTimeContractStatus() == 0) {
            log.info("登录用户：{} 下注已被禁用.", user.getUid());
            return AjaxResult.error("network_error");
        }
        Integer status = ConfigSettingUtil.getTimeContractStatus();
        if(status == 0) {
            return AjaxResult.error("feature_service_is_closed");
        }
        contractService.generateContract(param, request);
//        WebSocketNotifyServer.sendMessage(WebSocketNotifyServer.NotifyType.CONTRACT);
        return AjaxResult.success();
    }

    @GetMapping("/getTimeContractPeriod")
    public AjaxResult getTimeContractPeriod(@RequestParam("symbolCode") String symbolCode){
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setSymbol(symbolCode);
        timePeriod.setStatus(0);
        List<TimePeriod> list = periodService.listByEntity(timePeriod);
        List<TimePeriodVO> listVO = Lists.newArrayList();
        for(TimePeriod period : list){
            TimePeriodVO periodVO = new TimePeriodVO();
            BeanUtils.copyProperties(period, periodVO);
            listVO.add(periodVO);
        }
        return AjaxResult.success(listVO);
    }

}
