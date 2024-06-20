package com.bigo.common.utils;
import com.bigo.common.exception.CustomException;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.BigoVersion;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.system.domain.SysDictData;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 数字货币工具类
 * @author wenxm
 */
public class CoinUtils {

    private CoinUtils(){}

    /**
     * 获取两个币种的交易对
     *
     * @param
     * @return
     */
    public static String getSymbolByCoins(String coin1, String coin2) {
        return coin1.toLowerCase() + coin2.toLowerCase();
    }

    /**
     * 获取两个币种的交易对
     *
     * @param
     * @return
     */
    public static String getUsdtSymbolByCoin(String coin) {
        return getSymbolByCoins(coin, CurrencyEnum.USDT.getCode());
    }

    /**
     * 获取提币手续费
     * @param currency 币种
     * @param money 提币数量
     * @param type 1-内转  2-外提
     * @return
     */
    public static BigDecimal getWithdrawFeeByCurrency(String currency, BigDecimal money, Integer type){
       /* if(money == null){
            money = BigDecimal.ZERO;
        }
        String key = type+"_" + currency.toUpperCase()+"_FEE";
        String feeStr = DictUtils.getDictValue("withdraw_config", key);
        if(StringUtils.isEmpty(feeStr)){
            throw new CustomException("withdraw_fee_does_not_config");
        }
        String[] feeArr = feeStr.split("_");
        BigDecimal fixed = new BigDecimal(feeArr[0]);
        BigDecimal percent = new BigDecimal(feeArr[1]);
        BigDecimal fee = money.multiply(percent).setScale(8, RoundingMode.HALF_UP);
        return fee.compareTo(fixed) > 0 ? fee : fixed;*/
        BigDecimal fee = ConfigSettingUtil.getWithdrawFee().divide(new BigDecimal(100));
        return money.multiply(fee);
    }

    /**
     * 获取最低提币数量
     * @param currency 币种
     * @param type 1-内转  2-外提
     * @return 最低提币数量
     */
    public static BigDecimal getWithdrawMinByCurrency(String currency, Integer type){
        String key = type+"_" + currency.toUpperCase()+"_MIN";
        String minStr = DictUtils.getDictValue("withdraw_config", key);
        if(minStr == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(minStr);
    }

    /**
     * 获取留仓费
     * @return
     */
    public static BigDecimal getCapitalRate(){
        String minStr = DictUtils.getDictValue("bigo_rate_config", "capital_rate", "0.0005");
        return new BigDecimal(minStr);
    }

    /**
     * 获取滑点率
     * @return
     */
    public static BigDecimal getSlipPoint(String symbol){
        String spStr = DictUtils.getDictValue("bigo_rate_config", "slip_point_"+symbol.toLowerCase(), "0.0001");
        return new BigDecimal(spStr);
    }

    /**
     * 获取留仓费收取周期
     * @return
     */
    public static String getCapitalPeriod(){
        return DictUtils.getDictValue("bigo_rate_config","capital_period" , "24");
    }

    /**
     * 获取做点差以后的价格
     * @param price
     * @param symbol
     * @param tradeType 1-做多 2-做空
     * @return
     */
    public static BigDecimal getSlipPrice(BigDecimal price, String symbol, Integer tradeType){
        BigDecimal slipPoint = CoinUtils.getSlipPoint(symbol);
        BigDecimal slipRate = null;
        if(tradeType == 1){
            slipRate = BigDecimal.ONE.add(slipPoint);
        }else{
            slipRate = BigDecimal.ONE.subtract(slipPoint);
        }
        return price.multiply(slipRate).setScale(8,RoundingMode.HALF_UP);
    }

    /**
     * 获取兑币费率
     * @return
     */
    public static BigDecimal getExchangeRate(){
        String rateStr = DictUtils.getDictValue("bigo_rate_config","exchange_rate" , "0.002");
        return new BigDecimal(rateStr);
    }

    /**
     * 获取指定交易对兑币价格
     * @return
     */
    public static BigDecimal getExchangePriceBySymbol(String symbol){
        String rateStr = DictUtils.getDictValue("bigo_rate_config",symbol.toLowerCase()+"_exchange_price" , "0.05");
        return new BigDecimal(rateStr);
    }

    /**
     * 获取指定交易对兑币费率
     * @return
     */
    public static BigDecimal getExchangeRateBySymbol(String symbol){
        String rateStr = DictUtils.getDictValue("bigo_rate_config",symbol.toLowerCase()+"_exchange_rate" , "0.003");
        return new BigDecimal(rateStr);
    }

    /**
     * 获取最小兑换数量
     * @return
     */
    public static BigDecimal getExchangeMinByCoin(String coin){
        String minStr = DictUtils.getDictValue("bigo_rate_config",coin.toLowerCase()+"_exchange_min" , "100");
        return new BigDecimal(minStr);
    }

    /**
     * 判断BIGO兑换是否开启 0-否 1-是
     * @return
     */
    public static Boolean canExchangeBIGO(){
        String value = DictUtils.getDictValue("bigo_rate_config","bigo_exchange_switch" , "0");
        return "1".equals(value);
    }

    /**
     * 获取最小补仓费比例
     * @return
     */
    public static BigDecimal getReplenishMinRate(){
        String minStr = DictUtils.getDictValue("bigo_rate_config","replenish_min_rate" , "0.1");
        return new BigDecimal(minStr);
    }

    /**
     * 获取一键强平差值
     * @return
     */
    public static BigDecimal getOneKeyCloseDiff(){
        String spStr = DictUtils.getDictValue("bigo_rate_config", "one_key_close_diff", "0.01");
        return new BigDecimal(spStr);
    }

    /**
     * 版本号
     * @return
     */
    public static String getVersionNo(){
        return DictUtils.getDictValue("bigo_version_info", "version_no");
    }

    /**
     * 下载链接
     * @return
     */
    public static String getVersionUrl(){
        return DictUtils.getDictValue("bigo_version_info", "update_url");
    }

    /**
     * 下载链接2
     * @return
     */
    public static BigoVersion getVersion2(){
        String url = DictUtils.getDictValue("bigo_version_info", "update_url_2");
        String no = DictUtils.getDictValue("bigo_version_info", "version_no_2");
        String remark = DictUtils.getDictValue("bigo_version_info", "description_2");
        return new BigoVersion(no, url, remark);
    }

    /**
     * 更新描述
     * @return
     */
    public static String getVersionDescription(){
        return DictUtils.getDictValue("bigo_version_info", "description");
    }

    /**
     * 获取购买合约的最小金额
     * @return
     */
    public static BigDecimal getContractMinByCoin(String coin){
        String minStr = DictUtils.getDictValue("bigo_rate_config",coin.toLowerCase() + "_contract_min" , "0.1");
        return new BigDecimal(minStr);
    }

    /**
     * 获取购买合约的最大金额
     * @return
     */
    public static BigDecimal getContractMaxByCoin(String coin){
        String maxStr = DictUtils.getDictValue("bigo_rate_config",coin.toLowerCase() + "_contract_max" , "100");
        return new BigDecimal(maxStr);
    }

    /**
     * 获取持仓的最大金额
     * @return
     */
    public static BigDecimal getContractTotalLimitByCoin(String coin){
        String totalStr = DictUtils.getDictValue("bigo_rate_config",coin.toLowerCase() + "_contract_total" , "1000");
        return new BigDecimal(totalStr);
    }

    /**
     * 获取限时合约的手续费
     * @return
     */
    public static BigDecimal getTimeContractFeeRate(){
        String rate = DictUtils.getDictValue("bigo_rate_config","time_contract_fee_rate" , "0.05");
        return new BigDecimal(rate);
    }

    /**
     * 获取otc的最小金额
     * @return
     */
    public static BigDecimal getOtcMinNum(String coin){
        String totalStr = DictUtils.getDictValue("bigo_otc_config",coin.toLowerCase() + "_min" , "50");
        return new BigDecimal(totalStr);
    }

    /**
     * 获取otc支付的超时时间
     * @return
     */
    public static Integer getPayExpireTime(){
        String totalStr = DictUtils.getDictValue("bigo_otc_config","pay_expire_time" , "30");
        return new Integer(totalStr);
    }

    /**
     * 获取otc确认的超时时间
     * @return
     */
    public static Integer getConfirmExpireTime(){
        String totalStr = DictUtils.getDictValue("bigo_otc_config","confirm_expire_time" , "60");
        return new Integer(totalStr);
    }

    /**
     * 获取注册赠送的体验金数量
     * @return
     */
    public static BigDecimal getGiveVstNum(){
        String rateStr = DictUtils.getDictValue("bigo_rate_config","give_vst_num" , "100000");
        return new BigDecimal(rateStr);
    }

    /**
     * 获取对应币种返利百分比
     * @return
     */
    public static BigDecimal getRebate(String symbol, String levelName) {
        String rateStr = DictUtils.getDictValue("rebate_config",symbol.toLowerCase()+"_"+levelName+"_rebate" , "5");
        BigDecimal rabate = new BigDecimal(rateStr);
        return rabate.divide(new BigDecimal(100));
    }

    /**
     * 最低多少个u起购
     * @return
     */
    public static BigDecimal getGiveLotteryCodeNum() {
        String rateStr = DictUtils.getDictValue("bigo_rate_config","libra_give_lottery_code" , "1000");
        return new BigDecimal(rateStr);
    }

    /**
     * 获取认购Libra币时高于 xxx数量时，赠送本次认购 x%
     * @return
     */
    public static BigDecimal getLibraGtNum() {
        String libraGtNum = DictUtils.getDictValue("bigo_rate_config","libra_gt_num" , "10000");
        return new BigDecimal(libraGtNum);
    }

    /**
     * 获取 赠送本次认购 x%
     * @return
     */
    public static BigDecimal getLibraGiveNumRate() {
        String libraGiveNumRate = DictUtils.getDictValue("bigo_rate_config","libra_give_num_rate" , "8");
        BigDecimal num = new BigDecimal(libraGiveNumRate);
        return num.divide(new BigDecimal(100));
    }


    /**
     * 获取释放的天数
     * @return
     */
    public static Integer getReleaseDayNum() {
        String releaseDayNum = DictUtils.getDictValue("bigo_wallet_config","frozen_day_num" , "90");
        return Integer.valueOf(releaseDayNum);
    }

    public static String getBtcRequestUrl() {
        return DictUtils.getDictValue("bigo_btc_config","request_url" , "");
    }

    public static String getBtcUser() {
        return DictUtils.getDictValue("bigo_btc_config","user" , "");
    }

    public static String getBtcPass() {
        return DictUtils.getDictValue("bigo_btc_config","password" , "");
    }

    public static void main(String[] args) {
//        String i = "1.926E-7";
//        BigDecimal s = new BigDecimal(i);
//        System.out.println(BigDecimal.ONE.divide(s, 8, BigDecimal.ROUND_HALF_UP));
//        System.out.println(s.toPlainString());
        System.out.println("librausdt".substring(0,5));
    }

    //获取发行时间时间戳
    public static String getTimeOfIssue() {
        return DictUtils.getDictValue("bigo_rate_config","time_of_issue" , "1609862400");
    }

    public static String getKlineConfigUrl() {
        return DictUtils.getDictValue("bigo_kline_config","request_url" , "http://127.0.0.1:7778");
    }

    public static Integer getSettlementType() {
        String type = DictUtils.getDictValue("time_settlement_type","type" , "3");
        return Integer.valueOf(type);
    }

    /**
     * 获取限时合约xx个USDT起下单
     * @return
     */
    public static BigDecimal getTimeMinAmountUsdt(){
        String amount = DictUtils.getDictValue("bigo_rate_config","time_min_amount" , "100");
        return new BigDecimal(amount);
    }

    public static String getOpenWithdrawStatus() {
        return DictUtils.getDictValue("open_withdraw_status","status" , "true");
    }
}
