package com.bigo.project.bigo.api;

import com.alibaba.fastjson.JSON;
import com.bigo.common.exception.CustomException;
import com.bigo.common.utils.*;
import com.bigo.common.utils.file.FileUploadUtils;
import com.bigo.common.utils.ip.IpUtils;
import com.bigo.framework.config.RuoYiConfig;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.api.domain.AssetTransferParam;
import com.bigo.project.bigo.api.domain.CoinExchange;
import com.bigo.project.bigo.api.dto.TransDTO;
import com.bigo.project.bigo.api.dto.WithdrawAddressDTO;
import com.bigo.project.bigo.api.vo.AccountVO;
import com.bigo.project.bigo.api.vo.AssetLogVO;
import com.bigo.project.bigo.api.vo.ChildVO;
import com.bigo.project.bigo.config.util.ConfigSettingUtil;
import com.bigo.project.bigo.enums.AssetLogSubTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.OperateEnum;
import com.bigo.project.bigo.pay.domain.BankInfo;
import com.bigo.project.bigo.pay.service.IBankInfoService;
import com.bigo.project.bigo.pay.service.ITransOrderService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.*;
import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.mapper.WalletAddressMapper;
import com.bigo.project.bigo.wallet.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description 币高用户资产api
 * @Author wenxm
 * @Date 2020/6/17 16:29
 */
@Slf4j
@RestController
@RequestMapping("/api/asset")
public class AssetApiController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWithdrawService withdrawService;

    @Autowired
    private IAssetLogService assetLogService;

    @Resource
    WalletAddressMapper walletAddressMapper;

    @Autowired
    private IToAddressInfoService toAddressInfoService;


    @Autowired
    private IWithdrawAddressService withdrawAddressService;

    @Autowired
    private ITransOrderService transOrderService;

    @Autowired
    private IBankInfoService bankInfoService;

    @Value("${config.trxService}")
    private String trxService;



    /**
     * 钱包列表
     */
    @GetMapping("/myAccountList")
    public AjaxResult myAccountList(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<AccountVO> accountList = walletService.listAccount(user.getUid());
        return AjaxResult.success(accountList);
    }

    /**
     * 资金划转
     * @param transferParam
     * @return
     */
    //@PostMapping("/transferAsset")
    public AjaxResult transferAsset(@Validated @RequestBody AssetTransferParam transferParam){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        transferParam.setUid(user.getUid());
        if(transferParam.getAmount().compareTo(BigDecimal.ZERO) < 1){
            return AjaxResult.error("transfer_quantity_must_be_greater_than_0");
        }
        walletService.transferAsset(transferParam);
        return AjaxResult.success();
    }

    /**
     * 内转/提币
     * @param withdraw
     * @return
     */
    @PostMapping("/withdraw")
    public AjaxResult withdraw(@Validated @RequestBody Withdraw withdraw){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        user = bigoUserService.getUserByUid(user.getUid());


        if(user.getStatus() == 2){
            return AjaxResult.error("internal_account_cannot_withdraw_currency");
        }
        // 是否开启
       /* if(CoinUtils.getOpenWithdrawStatus().equals("true") && !withdraw.getCoin().toUpperCase().equals(CurrencyEnum.DIEM.getCode())) {
            return AjaxResult.error("unsupported_coin");
        }*/
        //先校验是否支持该币种
        CurrencyUtils.validateCurrency(withdraw.getCoin(), OperateEnum.WITHDRAW);
        if(StringUtils.isEmpty(user.getPayPassword())){
            return AjaxResult.error("payment_password_not_set");
        }
        //校验用户提现状态码
        if(user.getWithdrawStatus() == null || user.getWithdrawStatus() == 0) {
            return AjaxResult.error("feature_service_is_closed");
        }
        //校验用户身份验证状态
        if(user.getAuthStatus() != 2) {
            return AjaxResult.error("please_complete_real_name_authentication_first");
        }
        if(StringUtils.isEmpty(withdraw.getPayPassword()) || !SecurityUtils.matchesPassword(withdraw.getPayPassword(),user.getPayPassword())){
            throw new CustomException("pay_password_not_match");
        }
        withdraw.setUid(user.getUid());
        if (ConfigSettingUtil.getExternalWithdrawStatus() == 0) {   //是否开启外部提现
            return AjaxResult.error("feature_service_is_closed");
        }
        BigDecimal min = ConfigSettingUtil.getWithdrawMinByCurrency(withdraw.getCoin(), withdraw.getType());
        if (withdraw.getMoney().compareTo(min) < 0) {// 是否满足最低提现
            return AjaxResult.error("illegal_withdraw_quantity");
        }
        if(withdraw.getType() == 1) {
            return AjaxResult.error("the_operation_cannot_be_performed_at_this_time");
        }
        if (ConfigSettingUtil.getExternalWithdrawStatus() == 0) {   //是否开启外部提现
            return AjaxResult.error("feature_service_is_closed");
        }
        WithdrawAddress withdrawAddress = withdrawAddressService.selectWithdrawAddressById(withdraw.getWithdrawAddressId());
        if(withdrawAddress == null) {
            return AjaxResult.error("to_address_is_not_exist");
        }
        withdraw.setToAddress(withdrawAddress.getAddress());

        withdrawService.withdraw(withdraw);
        String adminUrl = DictUtils.getDictValue("admin_message_url", "url", "");
        OkHttpUtil.get(adminUrl+"?type="+WebSocketNotifyServer.NotifyType.WITHDRAW,null);
        return AjaxResult.success();
    }


    /**
     * 代付
     * @return
     */
    @PostMapping("/trans")
    public AjaxResult trans(@Validated @RequestBody TransDTO transDTO){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        user = bigoUserService.getUserByUid(user.getUid());

        transDTO.setCoin(CurrencyEnum.USDT.getCode());
        if(user.getStatus() == 2){
            return AjaxResult.error("internal_account_cannot_withdraw_currency");
        }

        //先校验是否支持该币种
//        CurrencyUtils.validateCurrency(withdraw.getCoin(), OperateEnum.WITHDRAW);
        if(StringUtils.isEmpty(user.getPayPassword())){
            return AjaxResult.error("payment_password_not_set");
        }
        //校验用户提现状态码
        if(user.getWithdrawStatus() == null || user.getWithdrawStatus() == 0) {
            return AjaxResult.error("feature_service_is_closed");
        }
        if(StringUtils.isEmpty(transDTO.getPayPassword()) || !SecurityUtils.matchesPassword(transDTO.getPayPassword(), user.getPayPassword())){
            throw new CustomException("pay_password_not_match");
        }

        if (ConfigSettingUtil.getExternalWithdrawStatus() == 0) {   //是否开启外部提现
            return AjaxResult.error("feature_service_is_closed");
        }
        BigDecimal min = ConfigSettingUtil.getWithdrawMinByCurrency(transDTO.getCoin(), 0);
        if (transDTO.getMoney().compareTo(min) < 0) {// 是否满足最低提现
            return AjaxResult.error("illegal_withdraw_quantity");
        }
        if (ConfigSettingUtil.getExternalWithdrawStatus() == 0) {   //是否开启外部提现
            return AjaxResult.error("feature_service_is_closed");
        }
        BankInfo bankInfo = bankInfoService.selectBankInfoById(transDTO.getBankCodeId());
        if(bankInfo == null) {
            return AjaxResult.error("feature_service_is_closed");
        }
        transDTO.setUid(user.getUid());
        transOrderService.transPay(transDTO, bankInfo);
        return AjaxResult.success();
    }

    /**
     * 充提币历史
     * @param coin
     * @return
     */
    @GetMapping("/withdrawHistory")
    public AjaxResult withdrawHistory(@RequestParam("coin") String coin,@RequestParam(value = "pageNo",required = false)Long pageNo){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        pageNo = Optional.ofNullable(pageNo).orElse(1L);
        if(2==pageNo){
            return AjaxResult.success(new Withdraw[0]);
        }
        Withdraw param = new Withdraw();
        param.setUid(user.getUid());
        param.setCoin(coin);
        List<Withdraw> list = withdrawService.listWithdraw(param);
        return AjaxResult.success(list);
    }

    /**
     * 获取钱包地址
     * @param currency
     * @return
     */
    @GetMapping("/getWalletAddress")
    public AjaxResult getWalletAddress(@RequestParam("currency") String currency){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(ConfigSettingUtil.getRecharge(currency) == 0) {
            return AjaxResult.success("success",null);
        }
        WalletAddress param = new WalletAddress();
        param.setUid(user.getUid());
        param.setCoin(currency);
        String address = walletService.getWalletAddress(param);
        logger.info("address={},walletAddress={}",address,param);
        if(StringUtils.isEmpty(address) || address.startsWith("0x")){
            String coin = param.getCoin();
            Long uid = param.getUid();
            if("USDT_TRC20".equals(coin) && StringUtils.isEmpty(address)) { //新增
                String result = OkHttpUtil.get(trxService+"/api/address/"+uid,new HashMap<>());
                TrxResponse trxResponse = JSON.parseObject(result, TrxResponse.class);
                logger.info("address={},coin={},trxResponse={}",address,coin,trxResponse);
                address = (String) trxResponse.getData();
                WalletAddress temp = new WalletAddress();
                temp.setUid(uid);
                temp.setAddress(address);
                temp.setCoin(coin);
                walletAddressMapper.saveAddress(temp);
            } else if("USDT_TRC20".equals(coin) && address.startsWith("0x")) {
                logger.info("address={},coin={}",address,coin);
                String result = OkHttpUtil.get(trxService+"/api/address/"+uid,new HashMap<>());
                TrxResponse trxResponse = JSON.parseObject(result, TrxResponse.class);
                logger.info("address={},coin={},trxResponse={}",address,coin,trxResponse);
                address = (String) trxResponse.getData();
//                List<WalletAddress> list = new ArrayList<>();
                WalletAddress temp = new WalletAddress();
                temp.setUid(uid);
                temp.setAddress(address);
                temp.setCoin(coin);
//                list.add(temp);
                walletAddressMapper.updateAddress(temp);
            }

        }
        return AjaxResult.success("success",address);
    }

    /**
     * 闪兑
     * @param exchange 闪兑参数
     * @return
     */
    @PostMapping("/quickExchange")
    public AjaxResult quickExchange(@Validated @RequestBody CoinExchange exchange){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        exchange.setUid(user.getUid());
        //先校验是否支持该币种
//        CurrencyUtils.validateCurrency(exchange.getFrom(), OperateEnum.EXCHANGE);
//        CurrencyUtils.validateCurrency(exchange.getTo(), OperateEnum.EXCHANGE);
        walletService.quickExchange(exchange);
        return AjaxResult.success();
    }

    /**
     * 资产变更记录
     * @param coin
     * @return
     */
    /**
     * 资产变更记录
     * @param coin
     * @return
     */
    @GetMapping("/getAssetHistory")
    public AjaxResult getAssetHistory(@RequestParam("coin") String coin,
                                      @RequestParam("pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        startPage(pageNo, pageSize);
        List<AssetLogVO> list = assetLogService.listAssetLogByCoin(user.getUid(), coin);
        return AjaxResult.success(list);
    }

    /**
     * 认购记录
     * @param coin
     * @return
     */
//    @GetMapping("/exchangeRecord")
//    public AjaxResult exchangeList(@RequestParam("coin") String coin) {
//        Map map = new HashMap();
//        startPage();
//        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
////        List<AssetLogVO> list = assetLogService.assetLogCoinList(null, coin, AssetLogTypeEnum.COIN_EXCHANGE.getType(), AssetLogSubTypeEnum.FROZEN.getType(), 0);
//        List<AssetLogVO> list = new ArrayList<>();
//        List<AssetLogVO> randomList = RedisUtils.getCacheList("subscriptionRecord");
//        logger.info("随机生成认购记录：{}条", randomList.size());
//
//        BigDecimal num = BigDecimal.ZERO;
//        List<AssetLogVO> subscribeList = assetLogService.assetLogCoinList(user.getUid(), coin, AssetLogTypeEnum.COIN_EXCHANGE.getType(), AssetLogSubTypeEnum.FROZEN.getType(), 0);
//        if(subscribeList == null || subscribeList.isEmpty()) subscribeList = new ArrayList<>();
//        for (AssetLogVO logVO : subscribeList) {
//            num = num.add(logVO.getAmount());
//        }
//        for (AssetLogVO randomVo : randomList) {
//            list.add(randomVo);
//        }
//        for (AssetLogVO assetLogVO : list) {
//            // 账号打码
//            assetLogVO.setUsername(PrivacyUtils.maskEmail(assetLogVO.getUsername()));
//        }
//        Collections.shuffle(list);
//        // 已认购数量
//        // 当前价格
//        BigDecimal currentPrice =  MarketSituationUtils.getCurrentPriceBySymbol(SymbolEnum.CREUSDT.getCode());
//        map.put("exchangeList", list);  // 平台认购记录
//        map.put("exchangeNum", num.setScale(8, BigDecimal.ROUND_HALF_UP));    // 已认购
//        map.put("currentPrice", currentPrice);  //当前价格
//        return AjaxResult.success(map);
//    }

    /**
     * 我的认购记录
     * @param coin
     * @return
     */
//    @GetMapping("/myExchangeList")
//    public AjaxResult myExchangeList(@RequestParam("coin") String coin) {
//        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
//        List<AssetLogVO> list = assetLogService.assetLogCoinList(user.getUid(), coin, AssetLogTypeEnum.COIN_EXCHANGE.getType(), AssetLogSubTypeEnum.FROZEN.getType(), 0);
//        return AjaxResult.success(list);
//    }

    /**
     * 邀请详情
     * @return
     */
    @GetMapping("inviteDetail")
    public AjaxResult inviteDetail(@RequestParam("timeStatus")int timeStatus, @RequestParam("coin") String coin) {
        Map map = new HashMap();
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        String timeStr = null;
        if(timeStatus == 0) {   //今日
            timeStr = DateUtils.getDate();
        } else {    //昨日
            timeStr = DateUtils.dateTime(DateUtils.getStartTime(new Date(), -1));
        }
        String beginTime = timeStr + " 00:00:00";
        String endTime = timeStr + " 23:59:59";
        Map params = new HashMap();
        params.put("uid", user.getUid());
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("coin", coin);
        // 新增邀请人数
        Long invitedNumber = bigoUserService.getInvitedNumber(params);
        // 认购人数
        Long exchangeNum = assetLogService.getExchangeNum(params);
        // 认购总额
        params.put("type", AssetLogTypeEnum.COIN_EXCHANGE.getType());
        params.put("subType", AssetLogSubTypeEnum.FROZEN.getType());
        BigDecimal exchangeCount = assetLogService.getExchangeCount(params) == null?BigDecimal.ZERO:assetLogService.getExchangeCount(params);
        String exCount = exchangeCount.setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        //总邀请人数
        params.put("beginTime", null);
        params.put("endTime", null);
        Long invitedNumberCount = bigoUserService.getInvitedNumber(params);
        //下级列表
        List<Long> uids = new ArrayList<>();
        uids.add(user.getUid());
        List<ChildVO> childList = bigoUserService.listMaskChild(uids);

        map.put("invitedNumber", invitedNumber);
        map.put("exchangeNum", exchangeNum);
        map.put("exchangeCount", exCount);
        map.put("invitedNumberCount", invitedNumberCount);
        map.put("childList", childList);

        return AjaxResult.success(map);
    }

    /**
     * 提币地址列表
     * @return
     */
    @GetMapping("withdrawAddressList")
    public AjaxResult withdrawAddressList(@RequestParam(value = "coin",defaultValue = "") String coin) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        WithdrawAddress param = new WithdrawAddress();
        param.setUid(user.getUid());
        if(!StringUtils.isEmpty(coin)) {
            param.setCoin(coin);
        }
        List<WithdrawAddress> list = withdrawAddressService.selectWithdrawAddressList(param);
        for (WithdrawAddress withdrawAddress : list) {
            withdrawAddress.setWalletId(null);
        }
        return AjaxResult.success(list);
    }

    /**
     * 绑定地址
     */
    @PostMapping("/bindWithdrawAddr")
    public AjaxResult bindWithdrawAddr(@RequestBody WithdrawAddressDTO dto) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        Wallet wallet = walletService.getWallet(new Wallet(user.getUid(),dto.getCoin().toUpperCase(),0));
        if(wallet == null) {
            return AjaxResult.error("account_does_not_exist");
        }
        WithdrawAddress param = new WithdrawAddress();
        param.setCoin(dto.getCoin());
        param.setUid(user.getUid());
        List<WithdrawAddress> withdrawAddressList = withdrawAddressService.selectWithdrawAddressList(param);
        if(withdrawAddressList.size() >= 2) {
            return AjaxResult.error("address_is_capped");
        }

        dto.setAddress(dto.getAddress().trim());
        param = new WithdrawAddress();
        param.setAddress(dto.getAddress());
        withdrawAddressList = withdrawAddressService.selectWithdrawAddressList(param);
        if(withdrawAddressList.size() >= 1) {
            return AjaxResult.error("the_withdrawal_address_has_been_bound");
        }

        withdrawAddressService.saveAddress(user, wallet, dto);
        return AjaxResult.success();
    }


    /**
     * 绑定地址
     */
    @GetMapping("/delWithdrawAddress/{id}")
    public AjaxResult delWithdrawAddress(@PathVariable Long id) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        withdrawAddressService.deleteWithdrawAddressById(id);
        return AjaxResult.success();
    }


    /**
     * 获取人工充值钱包地址
     * @param currency
     * @return
     */
    @GetMapping("/manualRechargeAddress")
    public AjaxResult manualRechargeAddress(@RequestParam("currency") String currency){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        Map map = new HashMap();
        if(0 == ConfigSettingUtil.getManualRecharge(currency)) {
            map.put("address","");
            map.put("rechargeMin", BigDecimal.ZERO);
            return AjaxResult.success("success", map);
        }
        List<ToAddressInfo> list = toAddressInfoService.selectToAddressInfoList(new ToAddressInfo(currency));
        if(list == null || list.size() <= 0){
            return AjaxResult.success();
        }
        Random random = new Random();
        int n = random.nextInt(list.size());
        ToAddressInfo address = list.get(n);
        map.put("address",address.getAddress());
        map.put("rechargeMin", ConfigSettingUtil.getRechargeMinByCurrency(currency));
        return AjaxResult.success("success", map);
    }

    /**
     * 人工充币
     * @param file
     * @param money 充值金额
     * @param toAddress 充值地址
     * @param request
     * @return
     */
    @PostMapping("/recharge")
    public AjaxResult recharge(MultipartFile file,
                               @RequestParam(required = false) BigDecimal money,
                               @RequestParam(required = false) String coin,
                               @RequestParam(required = false) String formAddress,
                               HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        user = bigoUserService.getUserByUid(user.getUid());
        if(file == null){
            return AjaxResult.error("files_cannot_be_null");
        }
        if(file.getSize() > FileUploadUtils.AUTH_IMG_MAX_SIZE){
            return AjaxResult.error("single_image_size_cannot_be_greater_than_10MB");
        }
        if(money.compareTo(BigDecimal.ZERO) <= 0) {
            return AjaxResult.error("illegal_recharge_quantity");
        }
        if(StringUtils.isEmpty(formAddress)) {
            return AjaxResult.error("address_cannot_be_null");
        }

        if(!checkCoinFormAddress(coin,formAddress)){
            return AjaxResult.error("illegal_recharge_address");
        }

        BigDecimal rechargeAmountMin =  ConfigSettingUtil.getRechargeMinByCurrency(coin);
        if(money.compareTo(rechargeAmountMin) < 0) {
            return AjaxResult.error("illegal_recharge_quantity");
        }
        try {
            // 判断充值地址是否存在
            List<ToAddressInfo> list = toAddressInfoService.selectToAddressInfoList(new ToAddressInfo(coin));
            if(list == null || list.size() <= 0) {
                return AjaxResult.error("illegal_coin");
            }
            // 判断是否存在待审核的充值订单
            Withdraw withdraw = new Withdraw();
            withdraw.setUid(user.getUid());
            withdraw.setStatus(0);
            withdraw.setCheckStatus(0);
            withdraw.setType(4);
            List<Withdraw> listWithdraw = withdrawService.listWithdraw(withdraw);
            if(listWithdraw.size() > 0) {
                return AjaxResult.error("under_review");
            }
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = RuoYiConfig.getFileUrl() + fileName;
            //先校验是否支持该币种
//            CurrencyUtils.validateCurrency(coin, OperateEnum.WITHDRAW);
            if("USDT_TRC20".equals(coin.toUpperCase())) {
                coin = CurrencyEnum.USDT.getCode();
            }
            List<ToAddressInfo> toList = toAddressInfoService.selectToAddressInfoList(new ToAddressInfo(coin));
            if(list == null || list.size() <= 0){
                return AjaxResult.error("illegal_recharge_address");
            }
            String toAddress = toList.get(0).getAddress();

            withdraw = new Withdraw();
            withdraw.setUid(user.getUid());
            withdraw.setCoin(coin.toUpperCase());
            withdraw.setPhoto(url);
            withdraw.setMoney(money);
            withdraw.setFrom(formAddress);
            withdraw.setToAddress(toAddress);
            withdraw.setStatus(0);
            withdraw.setCheckStatus(0);
            withdraw.setType(5);
            withdraw.setCreateTime(new Date());
            getPosition(withdraw, request);
            withdrawService.insert(withdraw);
        } catch (Exception ex) {
            log.error("用户：{} 上传充币凭证失败,错误信息：{}", user.getUid(), ex);
            return AjaxResult.error("image_upload_failed");
        }
        return AjaxResult.success();
    }

    private boolean checkCoinFormAddress(String coin, String formAddress) {
        coin = coin.toUpperCase();
        if(("USDT_TRC20".equals(coin) || CurrencyEnum.BTC.getCode().equals(coin)) && formAddress.length() < 34) {
            return false;
        }else if((CurrencyEnum.USDT.getCode().equals(coin) || CurrencyEnum.ETH.getCode().equals(coin))
                && formAddress.length() < 42) {
            return false;
        }else {
            return true;
        }
    }

    private void getPosition(Withdraw withdraw, HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        log.info("充值，提现下单IP：{}", ip);
        withdraw.setIpAddress(ip);
        if(ip.contains(",")) {
            String ips[] = ip.split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : ips) {
                String address = IpUtils.getAddress(s.trim());
                String position = IpUtils.getPosition(address);
                sb.append(position).append(",");
            }
            withdraw.setPosition(sb.substring(0, sb.length()-1).toString());
        } else {
            String address = IpUtils.getAddress(ip.trim());
            withdraw.setPosition(IpUtils.getPosition(address));
        }
    }

}