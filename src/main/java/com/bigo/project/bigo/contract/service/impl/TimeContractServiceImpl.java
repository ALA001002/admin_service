package com.bigo.project.bigo.contract.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.*;
import com.bigo.common.utils.ip.IpUtils;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.websocket.server.WebSocketServer;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.TimeContractBuyParam;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.domain.TimeCurrencyConf;
import com.bigo.project.bigo.contract.domain.TimePeriod;
import com.bigo.project.bigo.contract.entity.TimeContractEntity;
import com.bigo.project.bigo.contract.mapper.TimeContractMapper;
import com.bigo.project.bigo.contract.service.ITimeContractService;
import com.bigo.project.bigo.contract.service.ITimeCurrencyConfService;
import com.bigo.project.bigo.contract.service.ITimePeriodService;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.ContractStatusEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.userinfo.service.IUserLevelService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.service.AsyncService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/21 17:49
 */
@Service
@Slf4j
public class TimeContractServiceImpl implements ITimeContractService {

    @Autowired
    private TimeContractMapper contractMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IUserLevelService userLevelService;

    @Autowired
    private IBigoUserService bigoUserService;
    
    @Autowired
    private IAgentService agentService;
    
    @Autowired
    private ITimeContractService contractService;

    @Autowired
    private ITimePeriodService periodService;

    @Autowired
    private ITimeCurrencyConfService timeCurrencyConfService;

//    @Resource
//    AsyncService asyncService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean generateContract(TimeContractBuyParam param, HttpServletRequest request) {
        TimeContract contract = new TimeContract();
        contract.setUid(param.getUid());
        contract.setMoney(param.getAmount());
        contract.setSymbol(param.getSymbolCode());
        contract.setCurrency(param.getCurrency());
        TimePeriod timePeriod = new TimePeriod();
        timePeriod.setPeriod(param.getPeriod());
        timePeriod.setSymbol(param.getSymbolCode());
        TimePeriod period = periodService.selectTimePeriodById(timePeriod);
        if(period == null){
            throw new CustomException("illegal_period");
        }
        if(param.getAmount().compareTo(period.getMinMoney()) < 0){
            throw new CustomException("the_amount_is_below_the_minimum_limit");
        }

        if(period.getYieldRate().compareTo(BigDecimal.ZERO) <= 0 || period.getLossRate().compareTo(BigDecimal.ZERO) <= 0){
            throw new CustomException("illegal_period");
        }
        Integer settlementType = CoinUtils.getSettlementType();
        contract.setPeriod(param.getPeriod());
        contract.setSettlementTime(getSettlementTime(period.getPeriod()));
        contract.setSettlementType(settlementType);
        contract.setYieldRate(period.getYieldRate().divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN));
        contract.setLossRate(period.getLossRate().divide(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_DOWN));

        //获取实时价格
        BigDecimal price = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        //做点差
        price = CoinUtils.getSlipPrice(price, contract.getSymbol(), param.getTradeType());
        contract.setBuyPrice(price);
        contract.setTradeType(param.getTradeType());
        getPosition(contract, request);
        openContract(contract);
        return Boolean.TRUE;
    }

    private void getPosition(TimeContract contract, HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        log.info("限时合约下单IP：{}", ip);
        contract.setIpAddress(ip);
        if(ip.contains(",")) {
            String ips[] = ip.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : ips) {
                String address = IpUtils.getAddress(s.trim());
                String position = IpUtils.getPosition(address);
                sb.append(position).append(",");
            }
            contract.setPosition(sb.substring(0, sb.length()-1).toString());
        } else {
            String address = IpUtils.getAddress(ip.trim());
            contract.setPosition(IpUtils.getPosition(address));
        }
    }

/*    public static void main(String[] args) {
        String ip = "183.182.123.161";
        log.info("下单IP：{}", ip);
        if(ip.contains(",")) {
            String ips[] = ip.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : ips) {
                String address = IpUtils.getAddress(s.trim());
                String position = IpUtils.getPosition(address);
                sb.append(position).append(",");
            }
            System.out.println(sb);
        } else {
            String address = IpUtils.getAddress(ip.trim());
            System.out.println(IpUtils.getPosition(address));
        }
    }*/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeContract(TimeContract contract) {
        if(contract == null){
            throw new CustomException("contract_does_not_exist");
        }
        if(!contract.getStatus().equals(ContractStatusEnum.OPEN.getType())){
            throw new CustomException("contract_status_has_been_cahnged");
        }
        contractService.calContract(contract, null);
        contract.setStatus(1);
        contractMapper.closeContract(contract);
        //处理代理商分成
        agentService.calAgentShare(contract.getId(), true);

        //发出合约状态变更通知
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                WebSocketServer.noticeStatusChange("TimeContract", contract.getUid());
            }
        });

    }

    @Override
    public List<TimeContract> listContract(TimeContract param) {
        return contractMapper.listContract(param);
    }

    @Override
    public TimeContract getByUIdAndContractId(Long uid, Long contractId) {
        ContractQueryParam param = new ContractQueryParam();
        param.setUid(uid);
        param.setContractId(contractId);
        return contractMapper.getByUidAndContractId(param);
    }

    @Override
    public TimeContract getById(Long id) {
        return contractMapper.getById(id);
    }

    @Override
    public List<TimeContractEntity> listByEntity(TimeContractEntity entity) {
        return contractMapper.listByEntity(entity);
    }


    @Override
    public BigDecimal getTotalFeeByUid(Long uid, String currency) {
        return contractMapper.getTotalFeeByUid(uid, currency);
    }

    /**
     * 建仓
     * @param contract
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openContract(TimeContract contract){
        //判断是否超过持仓上限
//        BigDecimal totalLimit = CoinUtils.getContractTotalLimitByCoin(contract.getCurrency());
//        BigDecimal totalAmout = contractMapper.getTotalAmountByUid(contract.getUid(),contract.getCurrency());
//        totalAmout = totalAmout.add(contract.getMoney());
//        if(totalAmout.compareTo(totalLimit) > 0){
//            throw new CustomException("the_maximum_position_has_been_exceeded");
//        }
        //生成订单号
        contract.setOrderNo(RandomNumberUtils.getRandom("TC"));
//        BigDecimal feeRate = CoinUtils.getTimeContractFeeRate();
        //计算手续费
//        BigDecimal fee = contract.getMoney().multiply(feeRate).setScale(4, RoundingMode.HALF_DOWN);
        contract.setFee(BigDecimal.ZERO);
        contract.setBuyTime(new Date());
        //持仓
        contract.setStatus(0);
        //插入合约
        contractMapper.insertContract(contract);
        //余额扣减
        BigDecimal totalMoney = contract.getMoney().add(BigDecimal.ZERO);
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.BUY_TIME_CONTRACT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .amount(totalMoney)
                .build();
        walletService.changeAsset(change,contract.getId());

        int rebateStatus = ConfigSettingUtil.getRebateStatus();
        int mostRebateLevel = ConfigSettingUtil.getMostRebateLevel();
        int tradeFee = ConfigSettingUtil.getTradeFeeRebate();
        log.info("mostRebateLevel={},rebateStatus={}",mostRebateLevel,rebateStatus);
        BigDecimal amount = totalMoney.multiply(new BigDecimal(tradeFee).divide(new BigDecimal(100)));
        /*if(1 == rebateStatus){ // 开启分销
            if(mostRebateLevel >= 1 && amount !=null && amount.compareTo(BigDecimal.ZERO) > 0) { //最高分销层级要大于1层
                log.info("开始处理分销逻辑");
                asyncService.levelRebate(contract.getUid(), amount);
                log.info("结束处理分销逻辑");
            }
        }*/
    }

    /**
     * 计算平仓时的相关数据
     * @param contract
     * @param curPrice
     */
    @Override
    @Transactional
    public void calContract(TimeContract contract, BigDecimal curPrice){
        BigoUser user = bigoUserService.getUserByUid(contract.getUid());

//        Integer type = CoinUtils.getSettlementType();
        Integer type = user.getTimeContractControl();
        if(type != 1 && type != 2 && type != 3) {
            type = 3;
        }
        if(type != 3) {
            contract.setSettlementType(type);
        }
        if(contract.getSettlementType() == 3) {
            hqPrice(contract, curPrice);    // 根据行情
        } else {
            houtaiPrice(contract);  // 根据后台手动设置
        }
    }

    @Transactional
    public void houtaiPrice(TimeContract contract) {
        Integer profitType = contract.getSettlementType();
        BigDecimal variablePrice = getVariablePrice(contract);    // 变动价格

        // 结算价
        BigDecimal curPrice = null;
        //价格跌了
        if(profitType == 1) { //盈
            if(contract.getTradeType() == 1) { // 做多
                // 建仓价格 + 随机数
                curPrice = contract.getBuyPrice().add(variablePrice);
            } else if(contract.getTradeType() == 2) { //做空
                curPrice = contract.getBuyPrice().subtract(variablePrice);
            }
        } else if (profitType == 2) { // 亏
            if(contract.getTradeType() == 1) { // 做多
                // 建仓价格 + 随机数
                curPrice = contract.getBuyPrice().subtract(variablePrice);
            } else if(contract.getTradeType() == 2) { //做空
                curPrice = contract.getBuyPrice().add(variablePrice);
            }
        } else {
            curPrice = contract.getBuyPrice();
        }
        contract.setProfitType(profitType);
        contract.setSettlementPrice(curPrice);
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.TIME_CONTRACT_SETTLEMENT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .build();

        //收益值
        BigDecimal profit = BigDecimal.ZERO;
        if(contract.getProfitType() == 1){
            //赢了，收益值等于本金乘以收益率
            profit = contract.getMoney().multiply(contract.getYieldRate()).setScale(8,RoundingMode.HALF_UP);
            BigDecimal totalMoney = contract.getMoney().add(profit);
            change.setAmount(totalMoney);
            walletService.changeAsset(change, contract.getId());
        }else if(contract.getProfitType() == 2){
            //输了,亏损金额等于本金乘于亏损率
            profit = contract.getMoney().multiply(contract.getLossRate()).setScale(8, RoundingMode.HALF_UP);
            BigDecimal totalMoney = contract.getMoney().subtract(profit);
            if(totalMoney.compareTo(BigDecimal.ZERO) > 0){
                change.setAmount(totalMoney);
                walletService.changeAsset(change, contract.getId());
            } else {
                profit = contract.getMoney();
            }
        }else if(contract.getProfitType() == 3){
            //平局返还本金
            change.setAmount(contract.getMoney());
            walletService.changeAsset(change, contract.getId());
        }
        contract.setProfit(profit);
       /* BigDecimal realPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        //添加插帧记录
        Frame frame = new Frame();
        frame.setUid(contract.getUid());
        frame.setContractId(contract.getId());
        frame.setRealPrice(realPrice);
        frame.setFramePrice(curPrice);
        frame.setType(profitType);
//        frame.setOperatorId(userId);
        frame.setContractType(ContractTypeEnum.CONTRACT.getType());
        frameMapper.insert(frame);*/
    }



    public BigDecimal getVariablePrice(TimeContract contract) {
        List<TimeCurrencyConf> timeCurrencyConfs = RedisUtils.getCacheList("time_currency_conf");
        if(timeCurrencyConfs == null || timeCurrencyConfs.size() < 1) {
            timeCurrencyConfs = timeCurrencyConfService.selectTimeCurrencyConfList(new TimeCurrencyConf());
            RedisUtils.setCacheList("time_currency_conf", timeCurrencyConfs);
        }
        TimeCurrencyConf currencyConf = null;
        for (TimeCurrencyConf timeCurrencyConf : timeCurrencyConfs) {
            if(contract.getSymbol().equals(timeCurrencyConf.getSymbol())) {
                currencyConf = timeCurrencyConf;
                break;
            }
        }
        if(contract.getSettlementType() == 1) {
            // 盈
            return currencyConf.getSurplus();
        }else if(contract.getSettlementType() == 2) {
            // 亏
            return currencyConf.getDeficit();
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    public void hqPrice(TimeContract contract, BigDecimal curPrice) {
        if (curPrice == null) {
            curPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        }
        contract.setSettlementPrice(curPrice);
        int flag = curPrice.compareTo(contract.getBuyPrice());
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.TIME_CONTRACT_SETTLEMENT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .build();
        //价格跌了
        if (flag < 0) {
            //做多 - 输
            if (contract.getTradeType() == 1) {
                contract.setProfitType(2);
            }
            //做空 -赢
            else if (contract.getTradeType() == 2) {
                contract.setProfitType(1);
            }
            //价格涨了
        } else if (flag > 0) {
            //做多 - 赢
            if (contract.getTradeType() == 1) {
                contract.setProfitType(1);
            }
            //做空 -输
            else if (contract.getTradeType() == 2) {
                contract.setProfitType(2);
            }
        } else {
            //价格没变
            contract.setProfitType(3);
        }
        //收益值
        BigDecimal profit = BigDecimal.ZERO;
        if (contract.getProfitType() == 1) {
            //赢了，收益值等于本金乘以收益率
            profit = contract.getMoney().multiply(contract.getYieldRate()).setScale(8, RoundingMode.HALF_UP);
            BigDecimal totalMoney = contract.getMoney().add(profit);
            change.setAmount(totalMoney);
            walletService.changeAsset(change, contract.getId());
        } else if (contract.getProfitType() == 2) {
            //输了,亏损金额等于本金乘于亏损率
            profit = contract.getMoney().multiply(contract.getLossRate()).setScale(8, RoundingMode.HALF_UP);
            BigDecimal totalMoney = contract.getMoney().subtract(profit);
            if(totalMoney.compareTo(BigDecimal.ZERO) > 0){
                change.setAmount(totalMoney);
                walletService.changeAsset(change, contract.getId());
            } else {
                profit = contract.getMoney();
            }
        } else if (contract.getProfitType() == 3) {
            //平局返还本金
            change.setAmount(contract.getMoney());
            walletService.changeAsset(change, contract.getId());
        }
        contract.setProfit(profit);
    }

    /**
     * 一键止损/止盈
     * @param contract 合约
     * @param profitType
     */
    @Override
    public int oneKeyClose(TimeContract contract, int profitType) {
        /*this.calContract(contract, profitType, userId);
        contract.setStatus(1);
        contractMapper.closeContract(contract);*/
        TimeContract timeContract = new TimeContract();
        timeContract.setId(contract.getId());
        timeContract.setSettlementType(profitType);
        return contractMapper.updateSettType(timeContract);
    }

    /**
     * 手动平仓时的相关数据
     * @param contract
     * @param profitType 1-盈，2-亏，3-平
     */
/*    @Transactional
    public void calContract(TimeContract contract, int profitType, Long userId){
        BigDecimal randomNum = getRandomNum(contract.getBuyPrice());
        // 结算价
        BigDecimal curPrice = null;
        //价格跌了
        if(profitType == 1) { //盈
            if(contract.getTradeType() == 1) { // 做多
                // 建仓价格 + 随机数
                curPrice = contract.getBuyPrice().add(randomNum);
            } else if(contract.getTradeType() == 2) { //做空
                curPrice = contract.getBuyPrice().subtract(randomNum);
            }
        } else if (profitType == 2) { // 亏
            if(contract.getTradeType() == 1) { // 做多
                // 建仓价格 + 随机数
                curPrice = contract.getBuyPrice().subtract(randomNum);
            } else if(contract.getTradeType() == 2) { //做空
                curPrice = contract.getBuyPrice().add(randomNum);
            }
        } else {
            curPrice = contract.getBuyPrice();
        }
        contract.setProfitType(profitType);
        contract.setSettlementPrice(curPrice);
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.TIME_CONTRACT_SETTLEMENT)
                .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                .build();
        
        //收益值
        BigDecimal profit = BigDecimal.ZERO;
        if(contract.getProfitType() == 1){
            //赢了，收益值等于本金乘以收益率
            profit = contract.getMoney().multiply(contract.getYieldRate()).setScale(8,RoundingMode.HALF_UP);
            BigDecimal totalMoney = contract.getMoney().add(profit);
            change.setAmount(totalMoney);
            walletService.changeAsset(change, contract.getId());
        }else if(contract.getProfitType() == 2){
            //输了亏损本金
            profit = contract.getMoney();
        }else if(contract.getProfitType() == 3){
            //平局返还本金
            change.setAmount(contract.getMoney());
            walletService.changeAsset(change, contract.getId());
        }
        contract.setProfit(profit);
        BigDecimal realPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        //添加插帧记录
        Frame frame = new Frame();
        frame.setUid(contract.getUid());
        frame.setContractId(contract.getId());
        frame.setRealPrice(realPrice);
        frame.setFramePrice(curPrice);
        frame.setType(profitType);
        frame.setOperatorId(userId);
        frame.setContractType(ContractTypeEnum.CONTRACT.getType());
        frameMapper.insert(frame);
    }*/

    //根据建仓价格获取随机数
/*    private BigDecimal getRandomNum(BigDecimal buyPrice) {
        BigDecimal rate = RandomNumberUtils.randomBigdecimal(3L, 1L, 6);
        rate = rate.divide(new BigDecimal(100));//百分比
        return buyPrice.multiply(rate).setScale(4, BigDecimal.ROUND_HALF_UP);
    }*/


    /**
     * 处理返佣
     * @param fee
     * @param uid
     */
    private void dealRakeBack(BigDecimal fee, Long uid, String currency, Long contractId){
        BigoUser user = bigoUserService.getUserByUid(uid);
        if(user.getParentUid() != null & !user.getParentUid().equals(90000L)){
            //一级分佣
            BigoUser parent = bigoUserService.getUserByUid(user.getParentUid());
            BigDecimal firstRate = userLevelService.getFirstRateByUid(parent.getUid());
            BigDecimal firstFee = fee.multiply(firstRate).setScale(8, RoundingMode.HALF_UP);
            AssetChange change = AssetChange.builder().uid(parent.getUid())
                    .currency(currency)
                    .dim(0)
                    .type(AssetLogTypeEnum.RAKE_BACK)
                    .subType(AssetLogSubTypeEnum.FIRST_BACK)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(firstFee)
                    .build();
            walletService.changeAsset(change, contractId);
            if(parent.getParentUid() != null && !user.getParentUid().equals(90000L)){
                //二级分佣
                BigoUser grand = bigoUserService.getUserByUid(parent.getParentUid());
                BigDecimal secondRate = userLevelService.getSecondRateByUid(grand.getUid());
                BigDecimal secondFee = fee.multiply(secondRate).setScale(8, RoundingMode.HALF_UP);
                AssetChange secChange = AssetChange.builder().uid(grand.getUid())
                        .currency(currency)
                        .dim(0)
                        .type(AssetLogTypeEnum.RAKE_BACK)
                        .subType(AssetLogSubTypeEnum.SECOND_BACK)
                        .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                        .amount(secondFee)
                        .build();
                walletService.changeAsset(secChange, contractId);
            }
        }
    }

    private Date getSettlementTime(Integer period){
        return DateUtils.addSeconds(DateUtils.getNowDate(), period);
    }

}
