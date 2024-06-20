package com.bigo.project.bigo.agent.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.constant.UserConstants;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.SecurityUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.agent.domain.*;
import com.bigo.project.bigo.agent.mapper.AgentMapper;
import com.bigo.project.bigo.agent.service.IAgentAssetLogService;
import com.bigo.project.bigo.agent.service.IAgentRelationService;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.agent.service.IAgentWithdrawService;
import com.bigo.project.bigo.agent.vo.UserChildrenVo;
import com.bigo.project.bigo.agent.vo.UserSumVo;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.domain.TimeContract;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.contract.service.ITimeContractService;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.SymbolEnum;
import com.bigo.project.bigo.ico.service.IIcoSpotService;
import com.bigo.project.bigo.otc.entity.AppealEntity;
import com.bigo.project.bigo.otc.service.IAppealService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.Withdraw;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.system.domain.SysDept;
import com.bigo.project.system.domain.SysRole;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.domain.vo.IndexInfoVO;
import com.bigo.project.system.mapper.SysIndexMapper;
import com.bigo.project.system.service.ISysDeptService;
import com.bigo.project.system.service.ISysIndexService;
import com.bigo.project.system.service.ISysRoleService;
import com.bigo.project.system.service.ISysUserService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/7/29 15:23
 */
@Service
public class AgentServiceImpl implements IAgentService {

    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IAgentRelationService relationService;

    @Autowired
    private IAgentAssetLogService logService;

    @Autowired
    private IContractService contractService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private IAgentWithdrawService agentWithdrawService;

    @Autowired
    private ITimeContractService timeContractService;

    @Resource
    ISysIndexService indexService;

    @Autowired
    private IIcoSpotService icoSpotService;

    @Autowired
    private SysIndexMapper sysIndexMapper;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private IWalletService walletService;


    @Autowired
    private RedisCache redisCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addAgent(AgentRegisterParam param) throws IOException {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(param, user);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            throw new CustomException("新增代理商'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            throw new CustomException("新增代理商'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            throw new CustomException("新增代理商'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPhonenumber(param.getPhoneNumber());
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        //获取代理商部门和角色ID
        SysRole role = roleService.getByRoleKey("agent");
        user.setRoleIds(new Long[]{role.getRoleId()});
        SysDept dept = deptService.getByDeptName("代理商");
        user.setDeptId(dept.getDeptId());
        Long userId = userService.insertUser(user);
        //新建用来推荐的用户
        BigoUser bigoUser = new BigoUser();
        bigoUser.setNickName(param.getNickName());
        bigoUser.setPhone(param.getPhoneNumber());
        bigoUser.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        Long agentCode = bigoUserService.register(bigoUser);
        Agent agent = new Agent();
        agent.setUserId(userId);
        agent.setAgentCode(agentCode);
        agent.setStatus(0);
        agent.setAgentName(user.getNickName());
        agent.setFeeShareRate(param.getFeeShareRate());
        agent.setProfitShareRate(param.getProfitShareRate());
        agent.setCashDeposit(param.getCashDeposit());
        agent.setEthBalance(BigDecimal.ZERO);
        agent.setUsdtBalance(BigDecimal.ZERO);
        agent.setBigoBalance(BigDecimal.ZERO);
        return agentMapper.insert(agent);
    }

    @Override
    public int updateAgent(Agent agent) {
        return agentMapper.update(agent);
    }

    @Override
    public int updateStatus(Agent agent) {
        return agentMapper.updateStatus(agent);
    }

    @Override
    public Agent getByAgentId(Long agentId) {
        return agentMapper.getByAgentId(agentId);
    }

    @Override
    public List<Agent> listByEntity(Agent agent) {
        return agentMapper.listByEntity(agent);
    }

    @Override
    public Long getAgentIdByAgentCode(Long agentCode) {
        return agentMapper.getAgentIdByAgentCode(agentCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calAgentShare(Long contractId, Boolean isTimeContract) {
        Long uid = null;
        BigDecimal profit, contractFee;
        Integer profitType;
        String currency;
        if (isTimeContract) {
            TimeContract timeContract = timeContractService.getById(contractId);
            uid = timeContract.getUid();
            profit = timeContract.getProfit();
            contractFee = timeContract.getFee();
            profitType = timeContract.getProfitType();
            currency = timeContract.getCurrency();
        } else {
            Contract contract = contractService.getById(contractId);
            uid = contract.getUid();
            profit = contract.getProfit();
            contractFee = contract.getFee();
            profitType = contract.getProfitType();
            currency = contract.getCurrency();
        }
        Long agentId = relationService.getAgentIdByUserId(uid);
        if (agentId == null) return;
        Agent agent = agentMapper.getByAgentId(agentId);
        BigDecimal amount = profit.multiply(agent.getProfitShareRate()).setScale(8, RoundingMode.HALF_UP);
        BigDecimal fee = contractFee.multiply(agent.getFeeShareRate()).setScale(8, RoundingMode.HALF_UP);
        Integer dim = profitType == 1 ? 1 : 0;
        AgentAssetChange change = AgentAssetChange.builder().agentId(agentId)
                .amount(amount)
                .coin(currency)
                .contractId(contractId)
                .dim(dim)
                .fee(fee)
                .type(0)
                .build();
        agentService.changeAsset(change);
    }

    @Override
    @Transactional
    public void changeAsset(AgentAssetChange change) {
        Agent agent = agentMapper.getAgentForUpdate(change.getAgentId());
        Agent changeAgent = new Agent();
        changeAgent.setAgentId(change.getAgentId());
        AgentAssetLog log = new AgentAssetLog();
        BeanUtils.copyProperties(change, log);
        if (CurrencyEnum.USDT.getCode().equals(change.getCoin())) {
            if (change.getDim() == 1) {
                changeAgent.setUsdtBalance(change.getAmount().multiply(new BigDecimal("-1")));
            } else {
                changeAgent.setUsdtBalance(change.getAmount());
            }
            if (change.getFee() != null) {
                changeAgent.setUsdtBalance(changeAgent.getUsdtBalance().add(change.getFee()));
            }
            changeAgent.setEthBalance(BigDecimal.ZERO);
            log.setBefore(agent.getUsdtBalance());
            log.setAfter(agent.getUsdtBalance().add(changeAgent.getUsdtBalance()));
        } else if (CurrencyEnum.ETH.getCode().equals(change.getCoin())) {
            if (change.getDim() == 1) {
                changeAgent.setEthBalance(change.getAmount().multiply(new BigDecimal("-1")));
            } else {
                changeAgent.setEthBalance(change.getAmount());
            }
            if (change.getFee() != null) {
                changeAgent.setEthBalance(changeAgent.getEthBalance().add(change.getFee()));
            }
            changeAgent.setUsdtBalance(BigDecimal.ZERO);
            log.setBefore(agent.getEthBalance());
            log.setAfter(agent.getEthBalance().add(changeAgent.getEthBalance()));
        } else {
            return;
        }
        //更改余额并记录日志
        agentMapper.addBalance(changeAgent);
        logService.insertLog(log);
    }

    @Override
    @Transactional
    public Boolean withdraw(AgentWithdraw withdraw) {
        Agent agent = agentMapper.getAgentForUpdate(withdraw.getAgentId());
        if (CurrencyEnum.USDT.getCode().equals(withdraw.getCoin())) {
            if (agent.getUsdtBalance().compareTo(withdraw.getAmount()) < 0) {
                throw new CustomException("USDT余额不足！");
            }
        } else if (CurrencyEnum.ETH.getCode().equals(withdraw.getCoin())) {
            if (agent.getEthBalance().compareTo(withdraw.getAmount()) < 0) {
                throw new CustomException("ETH余额不足！");
            }
        } /*else if (CurrencyEnum.DIEM.getCode().equals(withdraw.getCoin())) {
            if (agent.getBigoBalance().compareTo(withdraw.getAmount()) < 0) {
                throw new CustomException("DIEM余额不足！");
            }
        }*/ else if (CurrencyEnum.BTC.getCode().equals(withdraw.getCoin())) {
            if (agent.getBigoBalance().compareTo(withdraw.getAmount()) < 0) {
                throw new CustomException("BTC余额不足！");
            }
        } else {
            throw new CustomException("币种错误！");
        }
        AgentAssetChange change = AgentAssetChange.builder().agentId(withdraw.getAgentId())
                .amount(withdraw.getAmount())
                .coin(withdraw.getCoin())
                .dim(1)
                .type(1)
                .build();
        agentService.changeAsset(change);
        agentWithdrawService.insert(withdraw);
        return Boolean.TRUE;
    }

    @Override
    public Agent getByUserId(Long userId) {
        return agentMapper.getByUserId(userId);
    }

    /**
     *分组统计
     */
    @Override
    public List<UserChildrenVo> statistics(WalletEntity entity) {
        List<UserChildrenVo> userChildrenVoList = new ArrayList<>();
        if(entity.getParentUid() == null) {
            //没有上级，查询顶级用户
            userChildrenVoList = userService.topUserList(entity);
        }else {
            userChildrenVoList = userService.lowLevelUserList(entity);
        }
        if(userChildrenVoList ==null || userChildrenVoList.size() == 0) return userChildrenVoList;
        for (UserChildrenVo userChildrenVo : userChildrenVoList) {
            //累计充值
            List<Long> uidList = new ArrayList<>();
            uidList = bigoUserService.getLowerUids(userChildrenVo.getUid());
            if(uidList !=null && uidList.size() > 0) userChildrenVo.setHasChildren(true);
            uidList.add(userChildrenVo.getUid());

            Withdraw param = new Withdraw();
            param.setStatus(1);
            param.setCoin(entity.getCurrency());
            param.setUidList(uidList);
            param.setType(4);
            BigDecimal totalRecharge = withdrawService.rechargeAndWithdrawAmount(param);
            userChildrenVo.setTotalRecharge(totalRecharge);
            //累计提现
            param.setType(2);
            BigDecimal totalWithdraw = withdrawService.rechargeAndWithdrawAmount(param);
            userChildrenVo.setTotalWithdraw(totalWithdraw);

            //用户余额
            BigDecimal userBalance = getTeamAsset(uidList);
            userChildrenVo.setUserBalance(userBalance);
        }

        return userChildrenVoList;
        /*Map params = new HashMap();
        if (agentId != null) {
            params.put("agentId", agentId);
        }
        params.put("type", 4);
        List<UserSumVo> allList = new ArrayList<>();
        // 顶级用户
        List<UserSumVo> parentList = userService.getParentUser(params);

        // 二级用户底下的用户
        List<UserSumVo> childrenList = new ArrayList<>();
        for (UserSumVo userSumVo : parentList) {
            // 递归查询底下用户
            // 二级用户
            List<UserSumVo> parent2List = new ArrayList<>();
            params.put("parentUid", userSumVo.getUid());
            childrenList = userService.getChildrenList(params);
            for (UserSumVo userSumVo2 : childrenList) { // 筛选完二级用户，剩下都是下级用户了
                if (userSumVo2.getParentUid().equals(userSumVo.getUid())) {
                    parent2List.add(userSumVo2);
//                    childrenList.remove(userSumVo2);
                }
            }

        *//*    for (UserSumVo parent2 : parent2List) {
                BigDecimal usdt = BigDecimal.ZERO;
                BigDecimal eth = BigDecimal.ZERO;
                BigDecimal btc = BigDecimal.ZERO;
                JSONObject jsonObject = new JSONObject();
                tj(parent2, childrenList, usdt, eth, btc, jsonObject);
                usdt = jsonObject.getBigDecimal("usdt");
                eth = jsonObject.getBigDecimal("eth");
                btc = jsonObject.getBigDecimal("btc");

                parent2.setUsdt(parent2.getUsdt().add(usdt));
                parent2.setEth(parent2.getEth().add(eth));
                parent2.setBtc(parent2.getBtc().add(btc));
                userSumVo.setUsdt(userSumVo.getUsdt().add(parent2.getUsdt()));
                userSumVo.setEth(userSumVo.getEth().add(parent2.getEth()));
                userSumVo.setBtc(userSumVo.getBtc().add(parent2.getBtc()));
                allList.add(parent2);
            }*//*

            for (UserSumVo parent2 : parent2List) {
                tj2(parent2, childrenList);
            }

            for (UserSumVo parent2 : parent2List) {
                List<BigDecimal> usdtList = new ArrayList<>();
                List<BigDecimal> ethList = new ArrayList<>();
                List<BigDecimal> btcList = new ArrayList<>();
                tj2(parent2, usdtList, ethList, btcList);
                BigDecimal usdt = BigDecimal.ZERO;
                for (BigDecimal u : usdtList) {
                    usdt = usdt.add(u);
                }
                BigDecimal eth = BigDecimal.ZERO;
                for (BigDecimal e : ethList) {
                    eth = eth.add(e);
                }
                BigDecimal btc = BigDecimal.ZERO;
                for (BigDecimal b : btcList) {
                    btc = btc.add(b);
                }
                parent2.setUsdt(parent2.getUsdt().add(usdt));
                parent2.setEth(parent2.getEth().add(eth));
                parent2.setBtc(parent2.getBtc().add(btc));

                userSumVo.setUsdt(userSumVo.getUsdt().add(parent2.getUsdt()));
                userSumVo.setEth(userSumVo.getEth().add(parent2.getEth()));
                userSumVo.setBtc(userSumVo.getBtc().add(parent2.getBtc()));
            }

            userSumVo.setChildren(parent2List);
//            allList.add(userSumVo);
//            for (UserSumVo children : childrenList) {
//                allList.add(children);
//            }
        }
        return parentList;*/
    }

    private BigDecimal getTeamAsset(List<Long> uidList) {
        List<Map> userInfoList = walletService.countUserInfo(uidList,null);
        Map<Long, List<Map>> walletUserMap = userInfoList.stream().collect(Collectors.groupingBy(t -> Long.valueOf(t.get("userId") + "")));
        BigDecimal total = BigDecimal.ZERO;
        for (Long uid : uidList) {
            BigDecimal balanceTotal = BigDecimal.ZERO;
            List<Map> walletList = walletUserMap.get(uid);
            if(walletList == null || walletList.size() == 0) continue;
            for (Map map : walletList) {
                BigDecimal amount = (BigDecimal) map.get("balanceValue");
                if(amount.compareTo(BigDecimal.ZERO) <= 0) continue;
                String coin = (String) map.get("coin");
                if(CurrencyEnum.USDT.getCode().equals(coin)) {
                    balanceTotal = balanceTotal.add(amount);
                }else {
                    String symbol = SymbolEnum.getCodeByCoin(coin.toLowerCase());
                    BigDecimal price = redisCache.getCacheObject(symbol + "_price") == null ? BigDecimal.ZERO : redisCache.getCacheObject(symbol + "_price");
                    BigDecimal usdtAmount = amount.multiply(price);
                    balanceTotal = balanceTotal.add(usdtAmount);
                }
            }
            total = total.add(balanceTotal);
        }
        return total;
    }

    private void tj2(UserSumVo parent, List<BigDecimal> usdtList, List<BigDecimal> ethList, List<BigDecimal> btcList) {
        for (UserSumVo child : parent.getChildren()) {
            if (child.getUsdt().compareTo(BigDecimal.ZERO) > 0) usdtList.add(child.getUsdt());
            if (child.getEth().compareTo(BigDecimal.ZERO) > 0) ethList.add(child.getEth());
            if (child.getBtc().compareTo(BigDecimal.ZERO) > 0) btcList.add(child.getBtc());
            tj2(child, usdtList, ethList, btcList);
        }
    }

    private void tj2(UserSumVo parent, List<UserSumVo> childrenList) {
        List<UserSumVo> userSumVoList = new ArrayList<>();
        for (UserSumVo userSumVo : childrenList) {
            if (parent.getUid().equals(userSumVo.getParentUid())) {
                userSumVoList.add(userSumVo);
                tj2(userSumVo, childrenList);
            }
        }
        parent.setChildren(userSumVoList);
    }

    private void tj(UserSumVo parent, List<UserSumVo> childrenList, BigDecimal usdt, BigDecimal eth, BigDecimal btc, JSONObject jsonObject) {
        for (UserSumVo userSumVo : childrenList) {
            if (parent.getUid().equals(userSumVo.getParentUid())) {
                usdt = usdt.add(userSumVo.getUsdt());
                eth = eth.add(userSumVo.getEth());
                btc = btc.add(userSumVo.getBtc());
                tj(userSumVo, childrenList, usdt, eth, btc, null);
            }
        }
//        parent.setUsdt(parent.getUsdt().add(usdt));
//        parent.setEth(parent.getEth().add(eth));
//        parent.setBtc(parent.getBtc().add(btc));
        if (jsonObject != null) {
            jsonObject.put("usdt", usdt);
            jsonObject.put("eth", eth);
            jsonObject.put("btc", btc);
        }
    }


    @Override
    public Map totalStatistics(Map parms) {
        Map map = new HashMap();
        parms.put("uid",parms.get("agentId"));
        //总统计
        sumTotalAmount(map, parms);
        //今日统计
        todayTotalAmount(map, parms);
        //统计用户总余额
        sumUserBalance(map, parms);
        Long firstDepositNum = sysIndexMapper.getFirstDepositUserNum(parms);
        map.put("firstDepositNum",firstDepositNum);
        return map;
    }

    private void sumUserBalance(Map map, Map params) {
        indexService.sumUserBalance(map, params);
    }

/*    public Map totalStatisticsOld(Map parms) {
        Map map = new HashMap();
        parms.put("uid",parms.get("agentId"));

        sumTotalAmount(map, parms);
        todayTotalAmount(map, parms);
//        yesterdayTotalAmount(map, parms);

        Long timeOrderNum = sysIndexMapper.getTimeOrderNum(parms);
        BigDecimal timeOrderMoney = sysIndexMapper.getTimeOrderMoney(parms);
        BigDecimal timeOrderFee = sysIndexMapper.getTimeOrderFee(parms);
        //盈
        parms.put("profitType",1);
        BigDecimal timeProfit = sysIndexMapper.getTimeProfit(parms);
        //亏
        parms.put("profitType",2);
        BigDecimal timeLoss = sysIndexMapper.getTimeProfit(parms);

        map.put("timeOrderNum", timeOrderNum);
        map.put("timeOrderMoney", timeOrderMoney);
        map.put("timeOrderFee", timeOrderFee);
        map.put("timeProfit", timeProfit);
        map.put("timeLoss", timeLoss);
        return map;
    }*/

    //统计今日充值金额
    private void todayTotalAmount(Map map, Map params) {
        Date today = new Date();
        Date startTime = DateUtils.getStartTime(today, 0);
        Date endTime = DateUtils.getEndTime(today, 0);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

//        Integer type = (Integer) params.get("type");
        params.put("uid",params.get("uid"));
        IndexInfoVO indexInfoNew = indexService.getIndexInfoNew(params);
        map.put("todayRechargeUsdt", indexInfoNew.getUsdtRecharge());
        map.put("todayRechargeEth", indexInfoNew.getEthRecharge());
        map.put("todayRechargeBtc", indexInfoNew.getBtcRecharge());
        map.put("todayRegisterUser", indexInfoNew.getUserNum());
        map.put("todayWithdrawUsdt", indexInfoNew.getUsdtWithdraw());
        map.put("todayWithdrawEth", indexInfoNew.getEthWithdraw());
        map.put("todayWithdrawBtc", indexInfoNew.getBtcWithdraw());
        Long tradeUserNum = icoSpotService.sumTradeUserNum(params);
        map.put("todayTradeUserNum", tradeUserNum);
    }



    //统计充值金额
    private void sumTotalAmount(Map map, Map params) {
        IndexInfoVO indexInfoNew = indexService.getIndexInfoNew(params);
        map.put("rechargeUsdt", indexInfoNew.getUsdtRecharge());
        map.put("rechargeEth", indexInfoNew.getEthRecharge());
        map.put("rechargeBtc", indexInfoNew.getBtcRecharge());
        map.put("totalMember", indexInfoNew.getUserNum());
        map.put("withdrawUsdt", indexInfoNew.getUsdtWithdraw());
        map.put("withdrawEth", indexInfoNew.getEthWithdraw());
        map.put("withdrawBtc", indexInfoNew.getBtcWithdraw());
        //统计总充值人数
        Long rechargeNum = indexService.getRechargeNum(params);
        map.put("rechargeNum", rechargeNum);
    }
  /*  private void sumTotalAmount(Map map, Map params) {
        String startTime = (String) params.get("startTime");
        String endTime = (String) params.get("startTime");
        if(StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
            Calendar a = Calendar.getInstance();
            a.set(Calendar.DATE, 1);//把日期设置为当月第一天
            a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
            //当月有多少天
            int maxDate = a.get(Calendar.DATE);

            SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy-MM-");
            startTime = sdfTwo.format(new Date()) + "01 00:00:00";
            endTime = sdfTwo.format(new Date()) + maxDate + " 23:59:59";
            params.put("startTime", startTime);
            params.put("endTime", endTime);
        }
        //统计充值金额
        IndexInfoVO indexInfoNew = indexService.getIndexInfoNew(params);
        map.put("totalMember", indexInfoNew.getUserNum());

        map.put("rechargeUsdt", indexInfoNew.getUsdtRecharge());
        map.put("onlineRechargeUsdt", indexInfoNew.getUsdtOnlineRecharge());
        map.put("offlineRechargeUsdt", indexInfoNew.getUsdtOfflineRecharge());
        map.put("manualRechargeUsdt", indexInfoNew.getUsdtManualRecharge());

        map.put("withdrawUsdt", indexInfoNew.getUsdtWithdraw());
        map.put("onlineWithdrawUsdt", indexInfoNew.getUsdtOnlineWithdraw());
        map.put("offlineWithdrawUsdt", indexInfoNew.getUsdtOfflineWithdraw());
        map.put("manualWithdrawUsdt", indexInfoNew.getUsdtManualWithdraw());
        map.put("passageWithdrawUsdt", indexInfoNew.getUsdtPassageWithdraw());

        map.put("profitLoss", indexInfoNew.getUsdtRecharge().subtract(indexInfoNew.getUsdtWithdraw()));
    }*/

    //统计今日金额
/*    private void todayTotalAmount(Map map, Map params) {
        Date today = new Date();
        Date startTime = DateUtils.getStartTime(today, 0);
        Date endTime = DateUtils.getEndTime(today, 0);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("uid",params.get("uid"));
        //今日充值

        IndexInfoVO indexInfoNew = indexService.getIndexInfoNew(params);
        map.put("todayRegisterUser", indexInfoNew.getUserNum());

        map.put("todayRechargeUsdt", indexInfoNew.getUsdtRecharge());
        map.put("todayOnlineRechargeUsdt", indexInfoNew.getUsdtOnlineRecharge());
        map.put("todayOfflineRechargeUsdt", indexInfoNew.getUsdtOfflineRecharge());
        map.put("todayManualRechargeUsdt", indexInfoNew.getUsdtManualRecharge());

        map.put("todayWithdrawUsdt", indexInfoNew.getUsdtWithdraw());
        map.put("todayOnlineWithdrawUsdt", indexInfoNew.getUsdtOnlineWithdraw());
        map.put("todayOfflineWithdrawUsdt", indexInfoNew.getUsdtOfflineWithdraw());
        map.put("todayManualWithdrawUsdt", indexInfoNew.getUsdtManualWithdraw());
        map.put("todayPassageWithdrawUsdt", indexInfoNew.getUsdtPassageWithdraw());

        map.put("todayProfitLoss", indexInfoNew.getUsdtRecharge().subtract(indexInfoNew.getUsdtWithdraw()));
    }*/

    //统计昨日金额
/*    private void yesterdayTotalAmount(Map map, Map params) {

        Date today = new Date();
        Date startTime = DateUtils.getStartTime(today, -1);
        Date endTime = DateUtils.getEndTime(today, -1);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("uid",params.get("uid"));
        //今日充值

        IndexInfoVO indexInfoNew = indexService.getIndexInfoNew(params);

        map.put("yesterdayRechargeUsdt", indexInfoNew.getUsdtRecharge());
        map.put("yesterdayOnlineRechargeUsdt", indexInfoNew.getUsdtOnlineRecharge());
        map.put("yesterdayOfflineRechargeUsdt", indexInfoNew.getUsdtOfflineRecharge());
        map.put("yesterdayManualRechargeUsdt", indexInfoNew.getUsdtManualRecharge());

        map.put("yesterdayWithdrawUsdt", indexInfoNew.getUsdtWithdraw());
        map.put("yesterdayOnlineWithdrawUsdt", indexInfoNew.getUsdtOnlineWithdraw());
        map.put("yesterdayOfflineWithdrawUsdt", indexInfoNew.getUsdtOfflineWithdraw());
        map.put("yesterdayManualWithdrawUsdt", indexInfoNew.getUsdtManualWithdraw());
        map.put("yesterdayPassageWithdrawUsdt", indexInfoNew.getUsdtPassageWithdraw());

        map.put("yesterdayProfitLoss", indexInfoNew.getUsdtRecharge().subtract(indexInfoNew.getUsdtWithdraw()));

    }*/




}
