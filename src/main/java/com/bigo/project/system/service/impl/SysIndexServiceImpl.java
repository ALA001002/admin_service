package com.bigo.project.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.system.domain.vo.IndexInfoVO;
import com.bigo.project.system.mapper.SysIndexMapper;
import com.bigo.project.system.service.ISysIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: wenxm
 * @date: 2020/8/18 20:10
 */
@Service
@Slf4j
public class SysIndexServiceImpl implements ISysIndexService {

    @Autowired
    private SysIndexMapper sysIndexMapper;

    @Override
    public IndexInfoVO getIndexInfo(Map<String, Object> params) {
        IndexInfoVO info = new IndexInfoVO();
        Long userNum = sysIndexMapper.getUserNum(params);
       /* Long orderNum = sysIndexMapper.getOrderNum(params);
        BigDecimal orderMoney = sysIndexMapper.getOrderMoney(params);
        BigDecimal orderFee = sysIndexMapper.getOrderFee(params);*/
/*        //盈
        params.put("profitType",1);
        BigDecimal profit = sysIndexMapper.getProfit(params);
        //亏
        params.put("profitType",2);
        BigDecimal loss = sysIndexMapper.getProfit(params);
        //充
        params.put("type",4);
        BigDecimal recharge = sysIndexMapper.getWithdrawInfo(params);
        //提
        params.put("type",2);
        BigDecimal withdraw = sysIndexMapper.getWithdrawInfo(params);
        BigDecimal withdrawFee = sysIndexMapper.getWithdrawFee(params);*/
        //期权
        Long timeOrderNum = sysIndexMapper.getTimeOrderNum(params);
        BigDecimal timeOrderMoney = sysIndexMapper.getTimeOrderMoney(params);
        BigDecimal timeOrderFee = sysIndexMapper.getTimeOrderFee(params);
        //盈
        params.put("profitType",1);
        BigDecimal timeProfit = sysIndexMapper.getTimeProfit(params);
        //亏
        params.put("profitType",2);
        BigDecimal timeLoss = sysIndexMapper.getTimeProfit(params);

        info.setUserNum(userNum);
      /*  info.setOrderNum(orderNum);
        info.setOrderMoney(orderMoney);
        info.setOrderFee(orderFee);*/
      /*  info.setProfit(profit);
        info.setLoss(loss);
        info.setRecharge(recharge);
        info.setWithdraw(withdraw);*/
//        info.setWithdrawFee(withdrawFee);
        info.setTimeLoss(timeLoss);
        info.setTimeProfit(timeProfit);
        info.setTimeOrderFee(timeOrderFee);
        info.setTimeOrderMoney(timeOrderMoney);
        info.setTimeOrderNum(timeOrderNum);
        return info;
    }

    @Override
    public IndexInfoVO getIndexInfoNew(Map<String, Object> params) {
        IndexInfoVO info = new IndexInfoVO();
        Long userNum = sysIndexMapper.getUserNum(params);
        info.setUserNum(userNum);
        Map<String, Map<String,Object>> typeMap = parse(params);
        log.info("=====typeMap={}",JSONObject.toJSONString(typeMap));
        info.setUsdtRecharge(getValue(typeMap,"USDT-4").add(getValue(typeMap,"USDT-5")));
        info.setUsdtWithdraw(getValue(typeMap,"USDT-3").add(getValue(typeMap,"USDT-2")));
        info.setBtcRecharge(getValue(typeMap,"BTC-4").add(getValue(typeMap,"BTC-5")));
        info.setBtcWithdraw(getValue(typeMap,"BTC-3").add(getValue(typeMap,"BTC-2")));
        info.setEthRecharge(getValue(typeMap,"ETH-4").add(getValue(typeMap,"ETH-5")));
        info.setEthWithdraw(getValue(typeMap,"ETH-3").add(getValue(typeMap,"ETH-2")));
//        info.setUsdtWithdrawFee(getValue(typeMap,"USDT-3-FEE"));
//        info.setEthWithdrawFee(getValue(typeMap,"ETH-3-FEE"));
//        info.setBtcWithdrawFee(getValue(typeMap,"BTC-3-FEE"));

        return info;
    }

    @Override
    public void sumUserBalance(Map map, Map params) {
        for (CurrencyEnum value : CurrencyEnum.values()) {
            params.put("coin", value.getCode());
            BigDecimal sumBalance = sysIndexMapper.sumUserBalance(params);
            map.put("sumBalance"+""+value.getCode(), sumBalance);
        }
    }

    @Override
    public Long getRechargeNum(Map params) {
        Long rechargeNum = sysIndexMapper.getRechargeNum(params);
        return rechargeNum;
    }

/*    @Override
    public IndexInfoVO getIndexInfoNew(Map<String, Object> params) {
        IndexInfoVO info = new IndexInfoVO();
        Long userNum = sysIndexMapper.getUserNum(params);
        info.setUserNum(userNum);
        Map<String, Map<String,Object>> typeMap = parse(params);
        info.setUsdtOnlineRecharge(getValue(typeMap,"USDT-4"));
        info.setUsdtOfflineRecharge(getValue(typeMap,"USDT-5"));
        info.setUsdtManualRecharge(getValue(typeMap,"USDT-6"));
        BigDecimal totalRecharge = info.getUsdtOnlineRecharge().add(info.getUsdtOfflineRecharge().add(info.getUsdtManualRecharge()));
        info.setUsdtRecharge(totalRecharge);

        info.setUsdtManualWithdraw(getValue(typeMap,"USDT-1"));
        info.setUsdtOnlineWithdraw(getValue(typeMap,"USDT-2"));
        info.setUsdtOfflineWithdraw(getValue(typeMap,"USDT-3"));
        info.setUsdtPassageWithdraw(getValue(typeMap, "USDT-7"));
        BigDecimal totalWithdraw = info.getUsdtManualWithdraw().add(info.getUsdtOfflineWithdraw().add(info.getUsdtOnlineWithdraw()).add(info.getUsdtPassageWithdraw()));
        info.setUsdtWithdraw(totalWithdraw);
//        info.setUsdtWithdrawFee(getValue(typeMap,"USDT-2-FEE"));
        return info;
    }*/

    public Map<String,Map<String,Object>> parse(Map<String, Object> params){
        List<Map> withdrawInfoList = sysIndexMapper.getWithdrawInfoList(params);
//        List<Map> manualList = sysIndexMapper.getManualList(params);
//        if(manualList.size() > 0) {
//            if(withdrawInfoList == null) withdrawInfoList = new ArrayList<>();
//            for (Map map : manualList) {
//                withdrawInfoList.add(map);
//            }
//        }

        Map<String, Map<String,Object>> typeMap = withdrawInfoList.stream().collect(Collectors.toMap(t -> {
            String type = t.get("type")+"";
            String coin = t.get("coin")+"";
            if("USDT_TRC20".equals(coin)){
                coin = "USDT";
            }
            return String.format("%s-%s",coin,type);
        }, t -> {
//            Object money = t.get("money");
//            if(money==null || "null".equals(money)){
//                return BigDecimal.ZERO;
//            }
            return t;
        }, (t1, t2) -> t1));

        return typeMap;
    }

    public BigDecimal getValue(Map<String,Map<String,Object>> typeMap, String key){
        Map<String,Object> item = typeMap.get(key);
        BigDecimal result = BigDecimal.ZERO;
        Map<String, Object> value = typeMap.get(key);
        if(value!=null){
            String money = null;
            if(key.endsWith("FEE")) {
                money=value.get("fee") + "";
            }else{
                money=value.get("money") + "";
            }
            if(null!=money && !"null".equals(money)) {
                result = new BigDecimal(money);
            }
        }
        return result;
    }
}
