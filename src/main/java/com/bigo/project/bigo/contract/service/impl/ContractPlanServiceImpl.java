package com.bigo.project.bigo.contract.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.MarketSituationUtils;
import com.bigo.common.utils.RandomNumberUtils;
import com.bigo.framework.websocket.server.WebSocketServer;
import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.ContractStopParam;
import com.bigo.project.bigo.api.vo.ContractPlanVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;
import com.bigo.project.bigo.contract.mapper.ContractPlanMapper;
import com.bigo.project.bigo.contract.mapper.ContractStopInfoMapper;
import com.bigo.project.bigo.contract.service.IContractPlanService;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.ContractPlanStatusEnum;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/23 15:41
 */
@Service
@Slf4j
public class ContractPlanServiceImpl implements IContractPlanService {

    @Autowired
    private ContractPlanMapper contractPlanMapper;

    @Autowired
    private IContractService contractService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private ContractStopInfoMapper stopInfoMapper;

    @Override
    public Boolean generateContractPlan(ContractBuyParam param) {
        BigDecimal curPrice = MarketSituationUtils.getCurrentPriceBySymbol(param.getSymbolCode());
        ContractPlan plan = new ContractPlan();
        plan.setUid(param.getUid());
        plan.setOrderNo(RandomNumberUtils.getRandom("PL"));
        plan.setSymbol(param.getSymbolCode());
        plan.setCurrency(param.getCurrency());
        plan.setStatus(ContractPlanStatusEnum.PLANING.getType());
        plan.setMoney(param.getAmount());
        plan.setContractMultiple(param.getContractMultiple());
        plan.setTradeType(param.getTradeType());
        plan.setTriggerPrice(param.getTriggerPrice());
        plan.setTrustPrice(curPrice);
        plan.setStopLoss(param.getStopLoss());
        plan.setStopSurplus(param.getStopSurplus());
        contractPlanMapper.insertContractPlan(plan);
        ContractStopParam stopInfo = param.getStopInfo();
        stopInfo.setContractPlanId(plan.getId());
        stopInfoMapper.insert(stopInfo);
        return Boolean.TRUE;
    }

    @Override
    public List<ContractPlanVO> listContractPlan(ContractQueryParam param) {
        return contractPlanMapper.listContractPlan(param);
    }

    @Override
    public Boolean revokeContractPlan(Long uid, Long planId) {
        ContractPlan plan = contractPlanMapper.getByUidAndPlanId(uid, planId);
        if(plan == null){
            throw new CustomException("contract_plan_does_not_exist");
        }
        if(!plan.getStatus().equals(ContractPlanStatusEnum.PLANING.getType())){
            throw new CustomException("contract_plan_status_has_changed");
        }
        return contractPlanMapper.revokeContractPlan(planId) > 0;
    }

    @Override
    public void dealPlaningContract(ContractPlan plan) {
            String symbol = plan.getSymbol();
            BigDecimal curPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            //判断触发价是否大于委托时的价格
            Boolean diffTrust = plan.getTriggerPrice().compareTo(plan.getTrustPrice()) > 0;
            //1、如果触发价大于委托时的价格，并且当前价格大于等于触发价，则处理此委托
            //2、如果触发价小于委托时的价格，并且当前价格小于等于触发价，则处理此委托
            if((diffTrust && curPrice.compareTo(plan.getTriggerPrice()) >= 0)
                || (!diffTrust && curPrice.compareTo(plan.getTriggerPrice()) <= 0)){
                try {
                    contractService.geneContractByPlan(plan, curPrice);
                    plan.setFinalPrice(curPrice);
                    plan.setDealTime(new Date());
                    plan.setStatus(ContractPlanStatusEnum.DONE.getType());
                    contractPlanMapper.updatePlan(plan);
                    WebSocketServer.noticeStatusChange("ContractPlan", plan.getUid());
                } catch (Exception ex) {
                    plan.setStatus(ContractPlanStatusEnum.FAILED.getType());
                    plan.setFinalPrice(null);
                    plan.setDealTime(new Date());
                    contractPlanMapper.updatePlan(plan);
                    log.error("计划委托处理失败，单号：{}，失败原因：{}", plan.getOrderNo(), ex.getMessage(), ex);
                }
            }
    }

    @Override
    public List<ContractPlan> getPlaningContract() {
        return contractPlanMapper.getPlaningContract();
    }
}
