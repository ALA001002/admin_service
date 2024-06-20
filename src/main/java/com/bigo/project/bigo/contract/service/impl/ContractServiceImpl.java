package com.bigo.project.bigo.contract.service.impl;

import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.*;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.websocket.server.WebSocketServer;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.api.domain.ContractBuyParam;
import com.bigo.project.bigo.api.domain.ContractQueryParam;
import com.bigo.project.bigo.api.domain.ContractStopParam;
import com.bigo.project.bigo.api.vo.ContractVO;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.ContractPlan;
import com.bigo.project.bigo.contract.domain.Frame;
import com.bigo.project.bigo.contract.entity.ContractEntity;
import com.bigo.project.bigo.contract.mapper.ContractMapper;
import com.bigo.project.bigo.contract.mapper.ContractStopInfoMapper;
import com.bigo.project.bigo.contract.mapper.FrameMapper;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.*;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.userinfo.service.IUserLevelService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.AssetLog;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.loader.CglibProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/21 17:49
 */
@Service
@Slf4j
public class ContractServiceImpl implements IContractService {

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IUserLevelService userLevelService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private FrameMapper frameMapper;

    @Autowired
    private ContractStopInfoMapper stopInfoMapper;
    
    @Autowired
    private IAgentService agentService;
    
    @Autowired
    private IContractService contractService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean generateContract(ContractBuyParam param) {
        Contract contract = new Contract();
        contract.setUid(param.getUid());
        contract.setMoney(param.getAmount());
        contract.setSymbol(param.getSymbolCode());
        contract.setCurrency(param.getCurrency());
        //获取实时价格
        BigDecimal price = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        //做点差
        price = CoinUtils.getSlipPrice(price, contract.getSymbol(), param.getTradeType());
        contract.setBuyPrice(price);
        contract.setTradeType(param.getTradeType());
        contract.setContractType(param.getContractType());
        contract.setContractMultiple(param.getContractMultiple());
        //计算止盈止损价格
        ContractStopParam stopInfo = param.getStopInfo();
        calStopInfo(contract,stopInfo);
        openContract(contract);
        stopInfo.setContractId(contract.getId());
        stopInfoMapper.insert(stopInfo);
        return Boolean.TRUE;
    }

    @Override
    public List<ContractVO> listContractVO(ContractQueryParam param) {
        return contractMapper.listContractVO(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean closeContract(Long uid, Long contractId) {
        ContractQueryParam param = new ContractQueryParam();
        param.setUid(uid);
        param.setContractId(contractId);
        Contract contract = contractMapper.getByUidAndContractId(param);
        if(contract == null){
            throw new CustomException("contract_does_not_exist");
        }
        if(!contract.getStatus().equals(ContractStatusEnum.OPEN.getType())){
            throw new CustomException("contract_status_has_been_cahnged");
        }
        calContract(contract, null);
        contract.setStatus(ContractStatusEnum.PERSONAL_CLOSE.getType());
        contractService.closeContract(contract);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean geneContractByPlan(ContractPlan plan, BigDecimal price) {
        Contract contract = new Contract();
        contract.setContractPlanId(plan.getId());
        contract.setUid(plan.getUid());
        contract.setMoney(plan.getMoney());
        contract.setSymbol(plan.getSymbol());
        contract.setCurrency(plan.getCurrency());
        //价格做点差
        contract.setBuyPrice(CoinUtils.getSlipPrice(price, plan.getSymbol(), plan.getTradeType()));
        //持仓
        contract.setTradeType(plan.getTradeType());
        //兼容旧版本
        contract.setStopLoss(plan.getStopLoss());
        contract.setStopSurplus(plan.getStopSurplus());
        //计算止盈止损价
        ContractStopParam stopInfo = stopInfoMapper.getByContractPlanId(plan.getId());
        calStopInfo(contract, stopInfo);
        contract.setContractMultiple(plan.getContractMultiple());
        openContract(contract);
        stopInfo.setContractId(contract.getId());
        stopInfoMapper.insert(stopInfo);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAndDealContract(Contract contract) {
        BigDecimal curPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        int stop = needTriggerClose(contract, curPrice);
        //先判断是否止盈止损
        if (stop > 0) {
            if(stop == 1){
                calContract(contract, contract.getStopSurplus());
            }else{
                calContract(contract, contract.getStopLoss());
            }
            contract.setStatus(ContractStatusEnum.TRIGGER_CLOSE.getType());
            //如果该合同正在补仓，则跳过
            if(redisCache.getCacheObject("contract_"+contract.getId()) != null){
                throw new CustomException("合约：+" + contract.getId() + "+ 正在补仓");
            }
            contractService.closeContract(contract);
            //发出合约状态变更通知
            WebSocketServer.noticeStatusChange("Contract", contract.getUid());
        }else if (needForceClose(contract, curPrice)) {
            calContract(contract, contract.getPredictPrice());
            contract.setStatus(ContractStatusEnum.FORCE_CLOSE.getType());
            //如果该合同正在补仓，则跳过
            if(redisCache.getCacheObject("contract_"+contract.getId()) != null){
                throw new CustomException("合约：+" + contract.getId() + "+ 正在补仓");
            }
            contractService.closeContract(contract);
            //发出合约状态变更通知
            WebSocketServer.noticeStatusChange("Contract", contract.getUid());
        }
    }

    @Override
    public Boolean updateStopInfo(Contract contract, ContractStopParam stopInfo) {
        calStopInfo(contract, stopInfo);
        stopInfo.setContractId(contract.getId());
        stopInfoMapper.deleteByContractId(contract.getId());
        stopInfoMapper.insert(stopInfo);
        return contractMapper.updateStopInfo(contract);
    }

    @Override
    public List<Contract> listContract(List<Long> uidList, Date startTime, Date endTime) {
        if(CollectionUtils.isEmpty(uidList)){
            return new ArrayList<>();
        }
        Map<String, Object> param = new HashMap<>(3);
        param.put("uidList", uidList);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        return contractMapper.listContractByMap(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean replenishContract(ContractBuyParam param) {
        try {
            redisCache.setCacheObject("contract_" + param.getContractId(), "1");
            ContractQueryParam query = new ContractQueryParam();
            query.setUid(param.getUid());
            query.setContractId(param.getContractId());
            if(param.getReplenish().compareTo(BigDecimal.ZERO) < 1){
                throw new CustomException("illegal_replenish_quantity");
            }
            Contract oldContract = contractMapper.getByUidAndContractId(query);
            if (oldContract == null) {
                throw new CustomException("contract_does_not_exist");
            }
            if (!oldContract.getStatus().equals(ContractStatusEnum.OPEN.getType())) {
                throw new CustomException("the_contract_has_been_closed");
            }
            //判断补仓费是否太小
            BigDecimal minRate = CoinUtils.getReplenishMinRate();
            BigDecimal minQuantity = oldContract.getMoney().multiply(minRate).setScale(8, RoundingMode.HALF_UP);
            if(param.getReplenish().compareTo(minQuantity) < 0){
                throw new CustomException("illegal_replenish_quantity");
            }
            oldContract.setReplenish(oldContract.getReplenish().add(param.getReplenish()).setScale(8,RoundingMode.HALF_UP));
            //重新计算预计强平价
            calPredictPrice(oldContract);
            AssetChange change = AssetChange.builder().uid(param.getUid())
                    .currency(oldContract.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.REPLENISH_CONTRACT)
                    .walletType(WalletTypeEnum.CAPITAL_ACCOUNT.getType())
                    .amount(param.getReplenish())
                    .build();
            walletService.changeAsset(change, param.getContractId());
            return contractMapper.updateReplenish(oldContract) > 0;
        }finally {
            redisCache.deleteObject("contract_" + param.getContractId());
        }
    }

    @Override
    public List<Contract> listContract(Contract param) {
        return contractMapper.listContract(param);
    }

    @Override
    public void capitalContract(Contract contract, BigDecimal rate) {
        BigDecimal capital = contract.getMoney().multiply(rate).setScale(8,RoundingMode.HALF_UP);
        Contract updateContract = new Contract();
        updateContract.setId(contract.getId());
        updateContract.setCapitalFee(contract.getCapitalFee().add(capital));
        contractMapper.updateCapitalFee(updateContract);
    }

    @Override
    public Contract getByUIdAndContractId(Long uid, Long contractId) {
        ContractQueryParam param = new ContractQueryParam();
        param.setUid(uid);
        param.setContractId(contractId);
        return contractMapper.getByUidAndContractId(param);
    }

    @Override
    public Contract getById(Long id) {
        return contractMapper.getById(id);
    }

    @Override
    public List<ContractEntity> listByEntity(ContractEntity entity) {
        return contractMapper.listByEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean oneKeyClose(Contract contract, BigDecimal closePrice, Integer type, Long operatorId) {

        BigDecimal realPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        calContract(contract, closePrice);
        contractService.closeContract(contract);
        //添加插帧记录
        Frame frame = new Frame();
        frame.setUid(contract.getUid());
        frame.setContractId(contract.getId());
        frame.setRealPrice(realPrice);
        frame.setFramePrice(closePrice);
        frame.setType(type);
        frame.setOperatorId(operatorId);
        frameMapper.insert(frame);
        return Boolean.TRUE;
    }

    @Override
    public BigDecimal getTotalFeeByUid(Long uid, String currency) {
        return contractMapper.getTotalFeeByUid(uid, currency);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeContract(Contract contract) {
        //平仓
        contractMapper.closeContract(contract);
        //处理代理商分成
        agentService.calAgentShare(contract.getId(), false);
        /*if(!CurrencyEnum.VST.getCode().equals(contract.getCurrency())) {
            //处理代理商分成
            agentService.calAgentShare(contract.getId(), false);
        }*/
    }

    /**
     * 建仓
     * @param contract
     */
    private void openContract(Contract contract){
        //判断是否超过持仓上限
        BigDecimal totalLimit = CoinUtils.getContractTotalLimitByCoin(contract.getCurrency());
        BigDecimal totalAmout = contractMapper.getTotalAmountByUid(contract.getUid(),contract.getCurrency());
        totalAmout = totalAmout.add(contract.getMoney());
        if(totalAmout.compareTo(totalLimit) > 0){
            throw new CustomException("the_maximum_position_has_been_exceeded");
        }
        //生成订单号
        contract.setOrderNo(RandomNumberUtils.getRandom());
        //固定逐仓模式
        contract.setContractType(0);
        BigDecimal feeRate = userLevelService.getFeeByUid(contract.getUid());
        //计算手续费
        BigDecimal fee = contract.getMoney().multiply(new BigDecimal(contract.getContractMultiple())).multiply(feeRate).setScale(4, RoundingMode.HALF_DOWN);
        contract.setFee(fee);
        //持仓
        contract.setStatus(0);
        contract.setCoupon(0);
        //刚建仓时，补仓费为0
        contract.setReplenish(BigDecimal.ZERO);
        //计算预估强平价格
        calPredictPrice(contract);
        //插入合约
        contractMapper.insertContract(contract);
        //余额扣减
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(1)
                .type(AssetLogTypeEnum.CONTRACT_OPEN)
                .walletType(contract.getContractType())
                .amount(contract.getMoney())
                .build();
        walletService.changeAsset(change,contract.getId());
        //计算两级分佣
        dealRakeBack(contract.getFee(), contract.getUid(), contract.getCurrency(), contract.getId());
        /*if(!CurrencyEnum.VST.getCode().equals(contract.getCurrency())) {
            dealRakeBack(contract.getFee(), contract.getUid(), contract.getCurrency(), contract.getId());
        }*/
    }

    /**
     * 计算平仓时的相关数据
     * @param contract
     * @param curPrice
     */
    private void calContract(Contract contract, BigDecimal curPrice){
        if(curPrice == null) {
            curPrice = MarketSituationUtils.getCurrentPriceBySymbol(contract.getSymbol());
        }
        contract.setSellPrice(curPrice);
        int flag = curPrice.compareTo(contract.getBuyPrice());
        //收益绝对值
        BigDecimal profit = calProfit(contract, curPrice);
        contract.setProfit(profit);
        BigDecimal remainAmount = contract.getMoney().add(contract.getReplenish()).subtract(contract.getFee()).subtract(contract.getCapitalFee());
        AssetChange change = AssetChange.builder().uid(contract.getUid())
                .currency(contract.getCurrency())
                .dim(0)
                .type(AssetLogTypeEnum.CONTRACT_CLOSE)
                .walletType(contract.getContractType())
                .build();
        //价格跌了
        if(flag < 0){
            //做多 - 亏损
            if(contract.getTradeType() == 1){
                remainAmount = remainAmount.subtract(profit);
                contract.setProfitType(2);
            }
            //做空 -盈利
            else if(contract.getTradeType() == 2){
                remainAmount = remainAmount.add(profit);
                contract.setProfitType(1);
            }
        //价格涨了
        }else if(flag > 0){
            //做多 - 盈利
            if(contract.getTradeType() == 1){
                remainAmount = remainAmount.add(profit);
                contract.setProfitType(1);
            }
            //做空 -亏损
            else if(contract.getTradeType() == 2){
                remainAmount = remainAmount.subtract(profit);
                contract.setProfitType(2);
            }
        }else{
            //价格没变
            contract.setProfitType(3);
        }
        change.setAmount(remainAmount.setScale(8,RoundingMode.HALF_UP));
        if(curPrice.compareTo(contract.getPredictPrice()) == 0){
            //如果是强平，则亏损是100%
            contract.setProfit(contract.getMoney().add(contract.getReplenish()));
            contract.setYieldRate(BigDecimal.ONE);
            remainAmount = BigDecimal.ZERO;
        }else {
            BigDecimal yieldRate = profit.divide(contract.getMoney(), 4, RoundingMode.HALF_UP);
            contract.setYieldRate(yieldRate);
        }
        //剩余数量大于0，则返还给用户
        if( remainAmount.compareTo(BigDecimal.ZERO) > 0){
            walletService.changeAsset(change, contract.getId());
        }

    }

    /**
     * 计算收益
     * @param contract
     * @param curPrice
     * @return
     */
    private BigDecimal calProfit(Contract contract,  BigDecimal curPrice){
        BigDecimal buyTotal = contract.getMoney().multiply(new BigDecimal(contract.getContractMultiple())).setScale(8,RoundingMode.HALF_UP);
        //购买的数量 = （保证金*杠杆倍数）/购买时的价格
        BigDecimal buyAmount = buyTotal.divide(contract.getBuyPrice(),8,RoundingMode.HALF_UP);
        //平仓时的总价
        BigDecimal nowTotal = buyAmount.multiply(curPrice).setScale(8, RoundingMode.HALF_UP);
        //返回差价绝对值
        return nowTotal.subtract(buyTotal).abs().setScale(8, RoundingMode.HALF_UP);
    }

    /**
     * 计算预估强制平仓价格
     * @return
     */
    private void calPredictPrice(Contract contract){
        //获取强制平仓比例
        String rate = DictUtils.getDictValue("bigo_rate_config", "force_rate", "0.10");
        BigDecimal allowLoss = contract.getMoney().multiply(new BigDecimal("1").subtract(new BigDecimal(rate))).setScale(8,RoundingMode.HALF_UP);
        allowLoss = allowLoss.add(contract.getReplenish());
        BigDecimal buyTotal = contract.getMoney().multiply(new BigDecimal(contract.getContractMultiple())).setScale(8,RoundingMode.HALF_UP);
        //购买的数量 = （保证金*杠杆倍数）/购买时的价格
        BigDecimal buyAmount = buyTotal.divide(contract.getBuyPrice(),8,RoundingMode.HALF_UP);
        //平仓时的总价
        BigDecimal forcePrice = null;
        if(contract.getTradeType() == 1){
            forcePrice = contract.getBuyPrice().subtract(allowLoss.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }else{
            forcePrice = contract.getBuyPrice().add(allowLoss.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }
        if(forcePrice.compareTo(BigDecimal.ZERO) < 0){
            forcePrice = BigDecimal.ZERO;
        }
        contract.setPredictPrice(forcePrice);
    }

    /**
     * 处理返佣
     * @param fee
     * @param uid
     */
    private void dealRakeBack(BigDecimal fee, Long uid, String currency, Long contractId){
        BigoUser user = bigoUserService.getUserByUid(uid);
        if(user.getParentUid() != null){
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
            if(parent.getParentUid() != null){
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

    /**
     * 判断是否需要强制平仓
     * @param contract 合约信息
     * @param curPrice 当前价格
     * @return 是否需要强制平仓
     */
    private Boolean needForceClose(Contract contract, BigDecimal curPrice){
        Integer result = curPrice.compareTo(contract.getPredictPrice());
        //如果做多，且当前价格小于等于强制平仓价，则强制平仓
        if(contract.getTradeType() == 1 && result < 1){
            return Boolean.TRUE;
        }
        //如果做空，且当前价格大于等于强制平仓价，则强制平仓
        if(contract.getTradeType() == 2 && result >= 0){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 判断是否需要止盈止损
     * @param contract 合约信息
     * @param curPrice 当前价格
     * @return 0-不要止盈止损 1-止盈 2-止损
     */
    private int needTriggerClose(Contract contract, BigDecimal curPrice){
        //做多
        if(contract.getTradeType() == 1){
            //当前价大于等于止盈价，开始止盈
            if (contract.getStopSurplus() != null
                    && curPrice.compareTo(contract.getStopSurplus()) >= 0){
                return 1;
            }
            //当前价小于等于止损价，开始止损
            if (contract.getStopLoss() != null
                    && curPrice.compareTo(contract.getStopLoss()) < 1){
                return 2;
            }
        }
        //做空
        if(contract.getTradeType() == 2){
            //当前价小于等于止盈价，开始止盈
            if(contract.getStopSurplus() != null
                    && curPrice.compareTo(contract.getStopSurplus()) < 1){
                return 1;
            }
            //当前价大于等于止损价，开始止损
            if(contract.getStopLoss() != null
                    && curPrice.compareTo(contract.getStopLoss()) >= 0){
                return 2;
            }
        }
        return 0;
    }

    /**
     * 计算止盈止损价格
     * @param contract
     * @param stopInfo
     */
    private void calStopInfo(Contract contract, ContractStopParam stopInfo){
        if(stopInfo == null){
            return;
        }
        BigDecimal stopSurplusPrice = stopInfo.getStopSurplus();
        BigDecimal stopLossPrice = stopInfo.getStopLoss();
        //如果是止盈是百分比，需要计算止盈价格
        if(stopInfo.getStopSurplusType() == 2 && stopInfo.getStopSurplus() != null){
            stopSurplusPrice = getStopSurplusPrice(contract, formatPercent(stopInfo.getStopSurplus()));
        }
        //如果是止盈是百分比，需要计算止盈价格
        if(stopInfo.getStopLossType() == 2 && stopInfo.getStopLoss() != null){
            stopLossPrice = getStopLossPrice(contract, formatPercent(stopInfo.getStopLoss()));
        }
        //参数校验
        if(stopLossPrice != null){
            if(contract.getTradeType() == 1
                    && stopLossPrice.compareTo(contract.getBuyPrice()) > 0){
                throw new CustomException("illegal_stop_loss_quantity");
            }
            if(contract.getTradeType() == 2
                    && stopLossPrice.compareTo(contract.getBuyPrice()) < 0){
                throw new CustomException("illegal_stop_loss_quantity");
            }
        }
        if(stopSurplusPrice != null){
            if(contract.getTradeType() == 1
                    && stopSurplusPrice.compareTo(contract.getBuyPrice()) < 0){
                throw new CustomException("illegal_stop_surplus_quantity");
            }
            if(contract.getTradeType() == 2
                    && stopSurplusPrice.compareTo(contract.getBuyPrice()) > 0){
                throw new CustomException("illegal_stop_surplus_quantity");
            }
        }
        contract.setStopSurplus(stopSurplusPrice);
        contract.setStopLoss(stopLossPrice);
    }

    /**
     * 计算止盈价格
     * @param contract 合约信息
     * @param rate 止盈比例
     */
    private BigDecimal getStopSurplusPrice(Contract contract, BigDecimal rate){
        if(rate.compareTo(BigDecimal.ZERO) < 1 ){
            throw new CustomException("illegal_param");
        }
        BigDecimal allowSurplus = contract.getMoney().multiply(rate).setScale(8,RoundingMode.HALF_UP);
        BigDecimal buyTotal = contract.getMoney().multiply(new BigDecimal(contract.getContractMultiple())).setScale(8,RoundingMode.HALF_UP);
        //购买的数量 = （保证金*杠杆倍数）/购买时的价格
        BigDecimal buyAmount = buyTotal.divide(contract.getBuyPrice(),8,RoundingMode.HALF_UP);
        //止损时的总价
        BigDecimal stopSurplusPrice = null;
        if(contract.getTradeType() == 1){
            stopSurplusPrice = contract.getBuyPrice().add(allowSurplus.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }else{
            stopSurplusPrice = contract.getBuyPrice().subtract(allowSurplus.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }
        if(stopSurplusPrice.compareTo(BigDecimal.ZERO) < 0){
            stopSurplusPrice = BigDecimal.ZERO;
        }
        return stopSurplusPrice;
    }

    /**
     * 计算止损价格
     * @param contract 合约信息
     * @param rate 止损比例
     */
    private BigDecimal getStopLossPrice(Contract contract, BigDecimal rate){
        if(rate.compareTo(BigDecimal.ZERO) < 1 || rate.compareTo(BigDecimal.ONE) > -1){
            throw new CustomException("illegal_param");
        }
        BigDecimal allowLoss = contract.getMoney().multiply(rate).setScale(8,RoundingMode.HALF_UP);
        BigDecimal buyTotal = contract.getMoney().multiply(new BigDecimal(contract.getContractMultiple())).setScale(8,RoundingMode.HALF_UP);
        //购买的数量 = （保证金*杠杆倍数）/购买时的价格
        BigDecimal buyAmount = buyTotal.divide(contract.getBuyPrice(),8,RoundingMode.HALF_UP);
        //止损时的总价
        BigDecimal stopLossPrice = null;
        if(contract.getTradeType() == 1){
            stopLossPrice = contract.getBuyPrice().subtract(allowLoss.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }else{
            stopLossPrice = contract.getBuyPrice().add(allowLoss.divide(buyAmount,8,RoundingMode.HALF_UP)).setScale(8,RoundingMode.HALF_UP);
        }
        if(stopLossPrice.compareTo(BigDecimal.ZERO) < 0){
            stopLossPrice = BigDecimal.ZERO;
        }
        return stopLossPrice;
    }

    private BigDecimal formatPercent(BigDecimal value){
        return value.divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
    }

}
