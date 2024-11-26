package com.bigo.project.bigo.task;

import com.bigo.common.utils.CoinUtils;
import com.bigo.common.utils.DateUtils;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.service.IContractPlanService;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.contract.service.ITimeContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wenxm
 * @Description: 合约处理定时任务
 * @date 2019/7/27 下午9:50
 */
@Component("contractTask")
@Slf4j
public class ContractTask {

    @Autowired
    private IContractPlanService contractPlanService;

    @Autowired
    private IContractService contractService;

    @Autowired
    private ITimeContractService timeContractService;

    /**
     * 处理委托中的计划合约
     */
    public void contractPlanTask() {
        try {
            List<ContractPlan> planList = contractPlanService.getPlaningContract();
            if(CollectionUtils.isEmpty(planList)) {
                return;
            }
            for(ContractPlan plan : planList) {
                contractPlanService.dealPlaningContract(plan);
            }
        } catch (Exception e) {
            log.error("处理计划委托失败, error.", e);
        }
    }

    /**
     * 扫描正在持仓中的合约(止盈止损，强制平仓等)
     */
    public void contractTask() {
        try {
            //获取所有正在持仓中的合约
            Contract param = new Contract();
            param.setStatus(0);
            List<Contract> contractList = contractService.listContract(param);
            for(Contract contract : contractList) {
                try {
                    contractService.scanAndDealContract(contract);
                }catch (Exception ex){
                    log.error("处理合约失败，合约id:{},错误信息：{}",contract.getId(), ex.getMessage(), ex);
                }
            }
        } catch (Exception e) {
            log.error("处理止盈止损强平失败, error.", e);
        }
    }

    /**
     * 留仓费定时任务
     */
    public void capitalTask() {
        try {
            //获取所有正在持仓中的合约
            Contract param = new Contract();
            param.setStatus(0);
            List<Contract> contractList = contractService.listContract(param);
            BigDecimal capitalRate = CoinUtils.getCapitalRate();
            //判断收取留仓费的临界时间, 购买时间小于这个时间的，不收取留仓费
            Date capitalTime = DateUtils.addHours(new Date(), -4);
            for(Contract contract : contractList) {
                try {
                    if(contract.getBuyTime().before(capitalTime)) {
                        contractService.capitalContract(contract, capitalRate);
                    }
                }catch (Exception ex){
                    log.error("计算合约留仓费失败，合约id:{},错误信息：{}",contract.getId(), ex.getMessage(), ex);
                }
            }
        } catch (Exception e) {
            log.error("处理合约留仓费失败, error.", e);
        }
    }

    /**
     * 扫描正在持仓中的限时合约
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void timeContractTask() {
        try {
            //获取所有正在持仓中的合约
            TimeContract param = new TimeContract();
            param.setStatus(0);
            List<TimeContract> contractList = timeContractService.listContract(param);
            Date now = new Date();
            for(TimeContract contract : contractList) {
                try {
                    if(!contract.getSettlementTime().after(now)) {
                        timeContractService.closeContract(contract);
                    }
                }catch (Exception ex){
                    log.error("处理合约失败，合约id:{},错误信息：{}",contract.getId(), ex.getMessage(), ex);
                }
            }
        } catch (Exception e) {
            log.error("处理止盈止损强平失败, error.", e);
        }
    }
}
