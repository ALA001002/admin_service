package com.bigo.project.bigo.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bigo.common.utils.*;
import com.bigo.framework.config.RuoYiConfig;
import com.bigo.framework.redis.RedisCache;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.BigoVersion;
import com.bigo.project.bigo.api.domain.RegisterInfo;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.api.vo.LegalCurrencyVO;
import com.bigo.project.bigo.api.vo.RotationPictureVO;
import com.bigo.project.bigo.api.vo.SysNoticeVO;
import com.bigo.project.bigo.captcha.service.CaptchaService;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.country.service.ICountryService;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.otc.domain.LegalCurrency;
import com.bigo.project.bigo.otc.service.ILegalCurrencyService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWithdrawService;
import com.bigo.project.system.domain.SysNotice;
import com.bigo.project.system.service.ISysNoticeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonInfoApiController {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ICountryService countryService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private ISysNoticeService sysNoticeService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private ILegalCurrencyService legalCurrencyService;




    @Autowired
    private BgUserDayBalanceService userDayBalanceService;


    /**
     * 国家区号信息
     */
    @GetMapping("/countryList")
    public AjaxResult countryList(){
        return AjaxResult.success(countryService.listCountryInfo());
    }

    /**
     * 发送验证码
     */
    @PostMapping("/sendCaptcha")
    public AjaxResult sendCaptcha(@RequestParam(value = "type",defaultValue = "1")int type, @RequestBody Map param) throws MessagingException, IOException, TemplateException {
        //手机号
        String phone = param.get("phone") == null ? null : param.get("phone").toString();
        //邮箱
        String email = param.get("email") == null ? null : param.get("email").toString();
        //区号
        String areaCode = param.get("areaCode") == null ? null : param.get("areaCode").toString();
        String code = RandomNumberUtils.genCaptcha();
        if(StringUtils.isNotEmpty(phone)){
            phone = phone.replace(" ","");
            phone = areaCode+""+phone;
            log.info("用户手机号:{} 发送验证码", phone);
            captchaService.sendCaptcha(phone, code);
        }else if(StringUtils.isNotEmpty(email)){
            email = email.replace(" ","");
            log.info("用户邮箱:{} 发送验证码", email);
            captchaService.gmailSender(email, code,type);
        }
        return AjaxResult.success();
    }



    /**
     * 版本信息
     */
    @GetMapping("/version")
    public AjaxResult version(@RequestParam(value = "type",defaultValue = "1")int type){
        BigoVersion version = new BigoVersion();
//        if(type == 1) {
//
//        }else {
//            version = CoinUtils.getVersion2();
//        }
        version.setUrl(CoinUtils.getVersionUrl());
        version.setVersion(CoinUtils.getVersionNo());
        version.setRemark(CoinUtils.getVersionDescription());
        return AjaxResult.success(version);
    }

    /**
     * 版本信息(区分app版本)
     */
/*    @GetMapping("/getVersionInfo")
    public AjaxResult getVersionInfo(@RequestParam("app") String app,@RequestParam(value = "type",defaultValue = "1")int type, HttpServletRequest request){
        String lang = StringUtils.isEmpty(request.getHeader("lang")) ? "zh" : request.getHeader("lang");
        SysNotice sysNotice = sysNoticeService.selectNoticeByKey(app, lang);
        //未设置该语言的公告则取中文公告
        if(sysNotice == null && !"zh".equals(lang)){
            sysNotice = sysNoticeService.selectNoticeByKey(app, "zh");
        }
        BigoVersion version = new BigoVersion();
        if(type == 1) {
            version.setUrl(CoinUtils.getVersionUrl());
        }else {
            version.setUrl(CoinUtils.getVersionUrl2());
        }
        if(sysNotice != null){
            version.setVersion(sysNotice.getSource());
            version.setRemark(sysNotice.getNoticeContent());
        }
        return AjaxResult.success(version);
    }*/

    /**
     * 获取下载地址
     */
    @GetMapping("/getDownUrl")
    public AjaxResult getDownUrl(@RequestParam(value = "type",defaultValue = "1")int type){
        Map map = new HashMap();
        if(type == 1) {
            map.put("url", CoinUtils.getVersionUrl());
        }else {
            BigoVersion version = CoinUtils.getVersion2();
            map.put("url", version.getUrl());
        }
        return AjaxResult.success(map);
    }

    /**
     * 获取系统公告和轮播图列表
     */
    @GetMapping("/getSysNoticeList")
    public AjaxResult getSysNoticeList(HttpServletRequest request, @RequestParam("type") String type){
        SysNotice param = new SysNotice();
        param.setNoticeType(type);
        String lang = StringUtils.isEmpty(request.getHeader("lang")) ? "zh" : request.getHeader("lang");
        param.setLang(lang);
        List<SysNotice> noticeList = sysNoticeService.selectNoticeList(param);
        if(CollectionUtils.isEmpty(noticeList)){
            return AjaxResult.success(Lists.newArrayList());
        }
        //轮播图要特殊处理
        if("3".equals(type) || "4".equals(type)){
            List<Map<String,String>> pictureList = (List<Map<String,String>>) JSON.parse(noticeList.get(0).getNoticeContent());
            List<RotationPictureVO> list = Lists.newArrayList();
            for(int i = 0; i < pictureList.size(); i++){
                list.add(new RotationPictureVO(i, pictureList.get(i).get("url")));
            }
            return AjaxResult.success(list);
        }else {
            List<SysNoticeVO> voList = Lists.newArrayListWithCapacity(noticeList.size());
            for(int i = 0; i < noticeList.size(); i++){
                SysNotice sysNotice = noticeList.get(i);
                SysNoticeVO noticeVO = convertSysNotice(sysNotice);
//                if(!"9".equals(type)&& !"6".equals(type)){
//                    //列表请求时不返回公告内容
//                    noticeVO.setContent(null);
//                }
                voList.add(noticeVO);
            }
            return AjaxResult.success(voList);
        }
    }

    /**
     * 获取系统公告和轮播图
     */
    @GetMapping("/getSysNoticeById")
    public AjaxResult getSysNoticeById(@RequestParam("id") Long id){
        SysNotice sysNotice = sysNoticeService.selectNoticeById(id);
        SysNoticeVO sysNoticeVO = convertSysNotice(sysNotice);
        return AjaxResult.success(sysNoticeVO);
    }

    /**
     * 获取系统公告和轮播图
     */
    @GetMapping("/getSysArticle")
    public AjaxResult getSysArticle(HttpServletRequest request, @RequestParam("key") String key){
        String lang = StringUtils.isEmpty(request.getHeader("lang")) ? "zh" : request.getHeader("lang");
        SysNotice sysNotice = sysNoticeService.selectNoticeByKey(key, lang);
        SysNoticeVO sysNoticeVO = convertSysNotice(sysNotice);
        return AjaxResult.success(sysNoticeVO);
    }

    /**
     * B线数据
     */
//    @GetMapping("/generalInfo")
//    public AjaxResult generalInfo(HttpServletRequest request){
//        Map<String, Object> map = Maps.newHashMap();
//        List<AccountVO> accountList = null;
//        int isSysUser = 0;
//        LoginUser loginUser = tokenService.getLoginUser(request);
//        if(loginUser != null && loginUser.getBigoUser() != null){
//            BigoUser user = loginUser.getBigoUser();
//            // 获取今日昨日盈亏百分比
//            String profitStatus = DictUtils.getDictValue("bigo_base_config", "profit_status", "0");
//            BigDecimal yesterdayProfit = BigDecimal.ZERO;
//            if(Integer.parseInt(profitStatus) == 1) {
//                yesterdayProfit = userDayBalanceService.yesterdayProfit(user.getUid());
//            }
//            map.put("yesterdayProfit", yesterdayProfit);
//            //从缓存获取账户信息，如果没有，再从数据库取，用户变更资产时要删除账户缓存
//            accountList = redisCache.getCacheObject(user.getUid()+"_accountInfo");
//            if(CollectionUtils.isEmpty(accountList)) {
//                accountList = walletService.listAccount(user.getUid());
//                for(AccountVO account : accountList){
//                    Integer status = account.getSupNormalContract() == null ? 0 : 1;
//                    account.setStatus(status);
//                    account.setBalance(account.getBalance().add(account.getFrozen()));
//                    account.setUsdPrice(usdCalculation(account));
//                }
//                redisCache.setCacheObject(user.getUid()+"_accountInfo", accountList,60, TimeUnit.SECONDS);
//            }
//            if(user.getStatus() == 2) isSysUser = 2;
//        }
//        map.put("contractInfo", MarketSituationUtils.getContractInfo(isSysUser));
//        map.put("accountInfo", accountList);
//        return AjaxResult.success(map);
//    }

    /**
     * 行情数据
     */
    @GetMapping("/hqInfo")
    public AjaxResult hqInfo(@RequestParam(defaultValue = "1") Integer type){
        Map<String, Object> map = Maps.newHashMap();
        map.put("contractInfo", MarketSituationUtils.getContractInfo(type));
        return AjaxResult.success(map);
    }

    /**
     * B线数据
     */
    @GetMapping("/accountInfo")
    public AjaxResult accountInfo(HttpServletRequest request){
        Map<String, Object> map = Maps.newHashMap();
        List<AccountVO> accountList = null;
        LoginUser loginUser = tokenService.getLoginUser(request);
        if(loginUser != null && loginUser.getBigoUser() != null){
            BigoUser user = loginUser.getBigoUser();
            Long uid = user.getUid();
            //从缓存获取账户信息，如果没有，再从数据库取，用户变更资产时要删除账户缓存
            accountList = redisCache.getCacheObject(uid+"_accountInfo");
            if(CollectionUtils.isEmpty(accountList)) {
                accountList = walletService.listAccount(user.getUid());
                for(AccountVO account : accountList){
                    Integer status = account.getSupNormalContract() == null ? 0 : 1;
                    account.setStatus(status);
                    if(account.getBalance().compareTo(new BigDecimal(0.000001)) <=0){
                        account.setBalance(BigDecimal.ZERO);
                    }
                    account.setFrozen(account.getFrozen());
                    account.setSupExchange(1);
                    account.setIco(RuoYiConfig.getIconBySymbolName(account.getCurrency().toUpperCase()));
//                    account.setUsdPrice(usdCalculation(account));
                }
                accountList = accountList.stream().sorted(Comparator.comparing(AccountVO::getOrder)).collect(Collectors.toList());
                redisCache.setCacheObject(user.getUid()+"_accountInfo", accountList,60, TimeUnit.SECONDS);
            }
        }
        map.put("accountInfo", accountList);
        return AjaxResult.success(map);
    }

    /**
     * 挂单数据
     */
    @GetMapping("/deepInfo/{symbolCode}")
    public AjaxResult deepInfo(@PathVariable String symbolCode){
        return AjaxResult.success(MarketSituationUtils.getDeepPriceBySymbol(symbolCode));
    }


    // 计算usd 价格
    private String usdCalculation(AccountVO account) {
        String symbol = account.getCurrency().toLowerCase() + "usdt";   //交易对
//        if(account.getCurrency().equals(CurrencyEnum.DIEM.getCode())) {
//            symbol = SymbolEnum.CREUSDT.getCode();
//        }
        BigDecimal usdPrice = RedisUtils.getCacheObject("usdPrice") == null ? BigDecimal.ZERO : RedisUtils.getCacheObject("usdPrice");   // USDT/USD 汇率
        BigDecimal coinUsdPrice = null;
        if(account.getCurrency().equals(CurrencyEnum.USDT.getCode())) {
            coinUsdPrice = account.getBalance().multiply(usdPrice);
        } else {
            BigDecimal symbolPrice = MarketSituationUtils.getCurrentPriceBySymbol(symbol);
            // 获取交易对价格
            BigDecimal usdtNum = account.getBalance().multiply(symbolPrice);    // 对应币种换算成usdt数量
            coinUsdPrice = usdtNum.multiply(usdPrice);
        }
       return coinUsdPrice == null ? null : coinUsdPrice.setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString();
    }


    @GetMapping("/getWithdrawInfo")
    public AjaxResult getWithdrawInfo(@RequestParam("coin") String coin,
                                      @RequestParam(value = "money", required = false) BigDecimal money,
                                      @RequestParam("type") Integer type){
        Map<String, Object> map = Maps.newHashMap();
        /*String key = type+"_" + coin.toUpperCase()+"_FEE";
        String feeStr = DictUtils.getDictValue("withdraw_config", key);
        if(StringUtils.isEmpty(feeStr)){
            return AjaxResult.error("withdraw_fee_does_not_config");
        }
        String[] feeArr = feeStr.split("_");
        BigDecimal fixed = new BigDecimal(feeArr[0]);
        BigDecimal percent = new BigDecimal(feeArr[1]);
        BigDecimal min = CoinUtils.getWithdrawMinByCurrency(coin, type);
        map.put("fixed", fixed);*/
        map.put("percent", ConfigSettingUtil.getWithdrawFee().divide(new BigDecimal(100)));
        map.put("min", ConfigSettingUtil.getWithdrawMinByCurrency("USDT_TRC20".equals(coin)?"USDT":coin,null));
        return AjaxResult.success(map);
    }

    @GetMapping("/getExchangeInfo")
    public AjaxResult getExchangeInfo(){
        Map<String, Object> map = Maps.newHashMap();
        BigDecimal usdtMin = CoinUtils.getExchangeMinByCoin(CurrencyEnum.USDT.getCode());
        BigDecimal ethMin = CoinUtils.getExchangeMinByCoin(CurrencyEnum.ETH.getCode());
//        BigDecimal diemMin = CoinUtils.getExchangeMinByCoin(CurrencyEnum.DIEM.getCode());
        BigDecimal feeRate = CoinUtils.getExchangeRate();
        BigDecimal bigoFeeRate = CoinUtils.getExchangeRateBySymbol("librausdt");
        BigDecimal bigoPrice = CoinUtils.getExchangePriceBySymbol("librausdt");
        map.put("USDTMin", usdtMin);
        map.put("ETHMin", ethMin);
//        map.put("DIEMMin", diemMin);
        map.put("feeRate", feeRate);
        //比高币兑换信息
        Map<String, Object> bigoMap = Maps.newHashMap();
        bigoMap.put("feeRate", bigoFeeRate);
        bigoMap.put("price", bigoPrice);
        map.put("bigo", bigoMap);
        return AjaxResult.success(map);
    }


/*    @GetMapping("/getTimeContractPeriod")
    public AjaxResult getTimeContractPeriod(){
        List<TimePeriod> list = periodService.listAllPeriod();
        List<TimePeriodVO> listVO = Lists.newArrayList();
        BigDecimal feeRate = CoinUtils.getTimeContractFeeRate();
        for(TimePeriod period : list){
            TimePeriodVO periodVO = new TimePeriodVO();
            BeanUtils.copyProperties(period, periodVO);
            periodVO.setFeeRate(feeRate);
            listVO.add(periodVO);
        }
        return AjaxResult.success(listVO);
    }*/

    @GetMapping("/getShareLink")
    public AjaxResult getShareLink(){
        String link = DictUtils.getDictValue("bigo_base_config", "app_share_link");
        if(StringUtils.isEmpty(link)){
            return AjaxResult.error("share_link_not_configured");
        }
        return AjaxResult.success("success",link);
    }

    /**
     * 获取支持的法币列表
     * @return
     */
    @GetMapping("/getLegalList")
    public AjaxResult getLegalList(){
        List<LegalCurrency> legalList = legalCurrencyService.listAllLegalCurrency();
        List<LegalCurrencyVO> resultList = Lists.newArrayList();
        for(LegalCurrency currency : legalList){
            LegalCurrencyVO currencyVO = new LegalCurrencyVO();
            BeanUtils.copyProperties(currency, currencyVO);
            resultList.add(currencyVO);
        }
        return AjaxResult.success(resultList);
    }

    /**
     * 重置支付密码
     */
    @PostMapping("/resetPwd")
    public AjaxResult resetPwd(@Validated @RequestBody RegisterInfo info) {
        if(StringUtils.isEmpty(info.getCaptcha())){
            return AjaxResult.error("captcha_cannot_be_null");
        }
        if(!captchaService.verifyCaptcha(info.getPhone(), info.getEmail(), info.getCaptcha())){
            return AjaxResult.error("captcha_has_expired");
        }
        BigoUser user =StringUtils.isEmpty(info.getPhone()) ? bigoUserService.getUserByEmail(info.getEmail()) : bigoUserService.getUserByPhone(info.getPhone());
        if(user == null){
            return AjaxResult.error("user_does_not_exist");
        }
        BigoUser updateUser = new BigoUser();
        updateUser.setUid(user.getUid());
        Boolean changeFlag = false;
        if(StringUtils.isNotEmpty(info.getPassword())){
            updateUser.setPassword(SecurityUtils.encryptPassword(info.getPassword()));
            changeFlag = true;
        }
        if(StringUtils.isNotEmpty(info.getPayPassword())){
            updateUser.setPayPassword(SecurityUtils.encryptPassword(info.getPayPassword()));
            changeFlag = true;
        }
        if(changeFlag){
            bigoUserService.updateUser(updateUser);
        }
        return AjaxResult.success();
    }


    private SysNoticeVO convertSysNotice(SysNotice notice){
        if(notice == null) {
            return null;
        }
        SysNoticeVO noticeVO = new SysNoticeVO();
        noticeVO.setId(notice.getNoticeId());
        noticeVO.setTitle(notice.getNoticeTitle());
        noticeVO.setContent(notice.getNoticeContent());
        noticeVO.setCoverImage(notice.getCoverImage());
        noticeVO.setSource(notice.getSource());
        noticeVO.setCreateTime(notice.getCreateTime());
        return noticeVO;
    }

    /**
     * 获取倒计时时间
     */
    @GetMapping("/countdownTime")
    public AjaxResult countdownTime(){
        String countdownTime = DictUtils.getDictValue("bigo_base_config", "countdown_time");
        if(StringUtils.isEmpty(countdownTime)){
            return AjaxResult.error("share_link_not_configured");
        }
        return AjaxResult.success("success",DateUtils.dateToStamp(countdownTime));
    }

    /**
     * 获取认购进程数量
     */
    @GetMapping("/releaseProcess")
    public AjaxResult releaseProcess() {
        // 释放总额
        BigDecimal releaseCountNum = RedisUtils.getCacheObject("releaseCountNum") == null ? BigDecimal.ZERO : RedisUtils.getCacheObject("releaseCountNum");
        // 已认购总额
        BigDecimal countNum = RedisUtils.getCacheObject("countNum") == null ? BigDecimal.ZERO : RedisUtils.getCacheObject("countNum");
        Map map = new HashMap();
        releaseCountNum = releaseCountNum.add(new BigDecimal(6322584325L));
        map.put("releaseCountNum", releaseCountNum);
//        map.put("countNum", countNum);
        return AjaxResult.success(map);
    }




    /**
     * 获取客服地址Url
     * @return
     */
    @GetMapping("/kefuAddress")
    public AjaxResult kefuAddress(@RequestParam("type") int type,@RequestParam(value = "status", defaultValue = "1") int status) {
        String address = null;
        if(status == 1) {
            if (type == 0) {
                address = DictUtils.getDictValue("kefu_address", "login", "");
            } else {
                address = DictUtils.getDictValue("kefu_address", "app_home", "");
            }
        }else {
            if (type == 0) {
                address = DictUtils.getDictValue("kefu_address", "login_2", "");
            } else {
                address = DictUtils.getDictValue("kefu_address", "app_home_2", "");
            }
        }
        return AjaxResult.success("success", address);
    }
    /**
     * 是否开启盈利百分比
     */
    @GetMapping("/profitStatus")
    public AjaxResult showProfitStatus() {
        String status = DictUtils.getDictValue("bigo_base_config", "profit_status", "0");
        return AjaxResult.success("success", Integer.parseInt(status));
    }

    // 添加DIEM提现记录
//    @PostMapping("/addWidthRecord")
//    public AjaxResult addWidthRecord(@RequestBody JSONObject object) {
//        Long userId = object.getLong("userId");
//        BigDecimal amount =  object.getBigDecimal("amount");
//        String toAddress = object.getString("toAddress");
//
//        if(userId == null || amount == null ) {
//            return AjaxResult.error();
//        }
//        log.info("用户ID:{}--提现DIEM授权成功，参数：{}", object.toJSONString());
//        Withdraw withdraw = new Withdraw();
//        withdraw.setUid(userId);
//        withdraw.setCoin(CurrencyEnum.DIEM.getCode());
//        withdraw.setType(2);
//        withdraw.setToAddress(toAddress);
//        withdraw.setMoney(amount);
//        withdrawService.withdraw(withdraw);
//        return AjaxResult.success();
//    }

    /**
     * 获取随机生成充值记录
     * @return
     */
    @GetMapping("/rechargeRecord")
    public AjaxResult rechargeRecord() {
        JSONArray array = RedisUtils.getCacheObject("randomRecharge");
        return AjaxResult.success(array);
    }
}
