package com.bigo.project.bigo.config.util;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.RedisUtils;
import com.bigo.project.bigo.config.entity.ConfigSetting;
import com.bigo.project.bigo.enums.CurrencyEnum;

import java.math.BigDecimal;
import java.util.Optional;


public class ConfigSettingUtil {

    /**
     * 获取最低数量
     * @param currency 币种
     * @param type 1-内转  2-外提
     * @return 最低提币数量
     */
    public static BigDecimal getWithdrawMinByCurrency(String currency, Integer type){
       /* String key = type+"_" + currency.toUpperCase()+"_MIN";
        String minStr = DictUtils.getDictValue("withdraw_config", key);
        if(minStr == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(minStr);*/
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        BigDecimal min = new BigDecimal(0.05);
        if(CurrencyEnum.USDT.getCode().equals(currency.toUpperCase())) {
            min =  config.getUsdtWithdrawMin();
        }/*else if(CurrencyEnum.ETH.getCode().equals(currency.toUpperCase())) {
            min =  config.getEthWithdrawMin();
        }else if(CurrencyEnum.BTC.getCode().equals(currency.toUpperCase())) {
            min =  config.getBtcWithdrawMin();
        }*/
        return min;
    }

    /**
     * 获取最低充值数量
     * @param currency 币种
     * @return 最低提币数量
     */
    public static BigDecimal getRechargeMinByCurrency(String currency){
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        BigDecimal min = BigDecimal.ZERO;
        if(CurrencyEnum.USDT.getCode().equals(currency.toUpperCase()) || "USDT_TRC20".equals(currency.toUpperCase())) {
            min =  config.getUsdtRechargeMin();
        }/*else if(CurrencyEnum.ETH.getCode().equals(currency.toUpperCase())) {
            min =  config.getEthRechargeMin();
        }else if(CurrencyEnum.BTC.getCode().equals(currency.toUpperCase())) {
            min =  config.getBtcRechargeMin();
        }*/
        return min;
    }

    /**
     * 获取每日提现次数
     * @return
     */
    public static Integer getWithdrawCount() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getWithdrawCount();
    }

    /**
     * 获取提现手续费
     * @return
     */
    public static BigDecimal getWithdrawFee() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getWithdrawFee() == null?BigDecimal.ONE:config.getWithdrawFee();
    }

    /**
     * ERC-20充币状态
     */
    public static int getRecharge(String currency) {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        int status = 0;
        if(currency.equals(CurrencyEnum.USDT.getCode()) || currency.equals("USDT_TRC20")) {
            status = Optional.ofNullable(config.getUsdtRecharge()).orElse(0);

        } else if(currency.equals(CurrencyEnum.ETH.getCode())) {
            status = Optional.ofNullable(config.getEthRecharge()).orElse(0);
        } else  {
            status = Optional.ofNullable(config.getBtcRecharge()).orElse(0);
        }
        return status;
    }
    /**
     * 人工充值状态
     */
    public static int getManualRecharge(String currency) {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        int status = 0;
        if(currency.equals(CurrencyEnum.USDT.getCode()) || currency.equals("USDT_TRC20")) {
            status = config.getUsdtManualRecharge();
        } else if(currency.equals(CurrencyEnum.ETH.getCode())) {
            status = config.getEthManualRecharge();
        } else {
            status = config.getBtcManualRecharge();
        }
        return status;
    }
    /**
     * 外部提现状态
     * @return
     */
    public static int getExternalWithdrawStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getExternalWithdrawStatus();
    }


    /**
     * 邮箱注册状态
     * @return
     */
    public static int getEmailRegisterStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getEmailRegisterStatus();
    }

    /**
     * 短信注册状态
     * @return
     */
    public static int getSmsRegisterStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getSmsRegisterStatus();
    }

    /**
     * 分销开关
     */
    public static int getRebateStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getRebateStatus();
    }

    /**
     * 最高分销层级
     */
    public static int getMostRebateLevel() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getMostRebateLevel();
    }

    /**
     * 分销比例
     */
    public static BigDecimal getLevelRebate(int level) {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        BigDecimal rebate = BigDecimal.ZERO;
        if(level == 1) {
            rebate = config.getFirstLevelRebate();
        }else  if(level == 2) {
            rebate = config.getTwoLevelRebate();
        }else  if(level == 3) {
            rebate = config.getThreeLevelRebate();
        }else {
            rebate = config.getExtraLevelRebate();
        }
        return rebate;
    }

    /**
     * 延迟上分状态
     * @return
     */
    public static int getDelayedScoreStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getDelayedScoreStatus();
    }

    /**
     * 延迟上分时间
     * @return
     */
    public static int getDelayedTime() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getDelayedTime();
    }

    /**
     * 币彩下单状态
     * @return
     */
    public static int getLoanStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getLoanStatus();
    }

    /**
     * 期权合约下单状态
     * @return
     */
    public static int getTimeContractStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getTimeContractStatus();
    }


    /**
     *
     * @return
     */
    public static int getTradeFeeRebate() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getTradeFeeRebate();
    }


    /**
     *
     * @return
     */
    public static int getAuthRewardStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getAuthRewardStatus();
    }

    /**
     *
     * @return
     */
    public static BigDecimal getAuthRewardNum() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getAuthRewardNum();
    }

    public static int getFirstRechargeStatus() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getFirstRechargeStatus();
    }

    public static BigDecimal getFirstRechargeComplyAmount() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getFirstRechargeComplyAmount();
    }

    public static BigDecimal getFirstRechargeRewards() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getFirstRechargeRewards();
    }

    public static BigDecimal getFirstRechargeSuperRewards() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getFirstRechargeSuperRewards();
    }

    public static Long getIpRegisterCount() {
        String jsonStr = RedisUtils.getCacheObject("config_setting");
        ConfigSetting config = JSONObject.parseObject(jsonStr, ConfigSetting.class);
        return config.getIpRegisterCount();
    }

}
