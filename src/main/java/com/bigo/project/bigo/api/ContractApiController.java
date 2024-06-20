package com.bigo.project.bigo.api;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.CurrencyUtils;
import com.bigo.common.utils.DictUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.ContractStopParam;
import com.bigo.project.bigo.api.vo.ContractPlanVO;
import com.bigo.project.bigo.api.vo.ContractVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.service.IContractPlanService;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 币高用户合约api
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@RestController
@RequestMapping("/api/contract/")
public class ContractApiController {


    @Autowired
    private TokenService tokenService;


    @Autowired
    private IContractService contractService;

    @Autowired
    private IContractPlanService contractPlanService;


    /**
     * 合约列表
     * @param param
     * @return
     */
    @GetMapping("/listContract")
    public AjaxResult listContract(ContractQueryParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        param.setUid(user.getUid());
        if(TrustTypeEnum.PLAN.getType().equals(param.getTrustType())){
            List<ContractPlanVO> contractPlanVOList = contractPlanService.listContractPlan(param);
            return AjaxResult.success(contractPlanVOList);
        }else {
            List<ContractVO> contractVOList =  contractService.listContractVO(param);
            return AjaxResult.success(contractVOList);
        }
    }

    /**
     * 购买合约
     * @param param
     * @return
     */
    @PostMapping("/buyContract")
    public AjaxResult myAccountList(@Validated @RequestBody ContractBuyParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if("0".equals(DictUtils.getDictValue("bigo_contract_conf","open_contract", "0"))){
            return AjaxResult.error("the_operation_cannot_be_performed_at_this_time");
        }
        param.setUid(user.getUid());
        BigDecimal min = CoinUtils.getContractMinByCoin(param.getCurrency());
        BigDecimal max = CoinUtils.getContractMaxByCoin(param.getCurrency());
        //先校验是否支持该币种
        CurrencyUtils.validateCurrency(param.getCurrency(), OperateEnum.BUY_NORMAL_CONTRACT);
        if(param.getAmount().compareTo(min) < 0){
            return AjaxResult.error("the_amount_is_below_the_minimum_limit");
        }
        if(param.getAmount().compareTo(max) > 0){
            return AjaxResult.error("the_amount_is_greater_than_the_maximum_limit");
        }
        ContractStopParam stopInfo = new ContractStopParam();
        stopInfo.setStopLoss(param.getStopLoss());
        stopInfo.setStopSurplus(param.getStopSurplus());
        stopInfo.setStopSurplusType(param.getStopSurplusType() == null ? 1 : param.getStopSurplusType());
        stopInfo.setStopLossType(param.getStopLossType() == null ? 1 : param.getStopLossType());
        param.setStopInfo(stopInfo);
        if(TrustTypeEnum.PLAN.getType().equals(param.getTrustType())){
            if(param.getTriggerPrice() == null){
                return AjaxResult.error("trigger_price_cannot_be_empty");
            }
            contractPlanService.generateContractPlan(param);
        }else {
            contractService.generateContract(param);
        }
        WebSocketNotifyServer.sendMessage(WebSocketNotifyServer.NotifyType.ORDER);
        return AjaxResult.success();
    }

    /**
     * 手动平仓
     * @param param 合约id
     * @return
     */
    @PostMapping("/closeContract")
    public AjaxResult closeContract(@RequestBody Map param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        contractService.closeContract(user.getUid(), Integer.valueOf(param.get("contractId").toString()).longValue());
        return AjaxResult.success();
    }

    /**
     * 修改止盈止损
     * @param param 合约id
     * @return
     */
    @PostMapping("/updateStopInfo")
    public AjaxResult updateStopInfo(@RequestBody ContractStopParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(param.getContractId() == null){
            return AjaxResult.error("contract_id_cannot_be_null");
        }
        Contract oldContract = contractService.getByUIdAndContractId(user.getUid(), param.getContractId());
        if(oldContract == null || !oldContract.getStatus().equals(ContractStatusEnum.OPEN.getType())){
            throw new CustomException("contract_status_has_been_cahnged");
        }
        //兼容旧参数
        if(param.getStopSurplusType() == null){
            param.setStopSurplusType(1);
        }
        if(param.getStopLossType() == null){
            param.setStopLossType(1);
        }
        contractService.updateStopInfo(oldContract, param);
        return AjaxResult.success();
    }

    /**
     * 撤销委托
     * @param param 合约id
     * @return
     */
    @PostMapping("/revokePlan")
    public AjaxResult revokePlan(@RequestBody Map param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        Boolean result = contractPlanService.revokeContractPlan(user.getUid(), Integer.valueOf(param.get("planId").toString()).longValue());
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error("revoke_failed");
        }
    }

    /**
     * 追加补仓费
     * @param param
     * @return
     */
    @PostMapping("/replenishContract")
    public AjaxResult replenishContract(@RequestBody ContractBuyParam param){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        param.setUid(user.getUid());
        if(param.getContractId() == null){
            return AjaxResult.error("contract_id_cannot_be_null");
        }
        if(param.getReplenish() == null){
            return AjaxResult.error("replenish_quantity_cannot_be_null");
        }
        Boolean result = contractService.replenishContract(param);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error("replenish_contract_failed");
        }
    }

}
