package com.bigo.project.bigo.wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.btc.BTCWalletUtils;
import com.bigo.common.utils.google.GoogleAuthenticator;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.security.LoginUser;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.agent.domain.Agent;
import com.bigo.project.bigo.agent.service.IAgentService;
import com.bigo.project.bigo.api.domain.ProductParam;
import com.bigo.project.bigo.enums.AmountTypeEnum;
import com.bigo.project.bigo.enums.AssetLogTypeEnum;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.enums.WalletTypeEnum;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.service.IProductInfoService;
import com.bigo.project.bigo.product.service.IProductOrderService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.BgUserDayBalanceService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.wallet.domain.AssetChange;
import com.bigo.project.bigo.wallet.domain.Wallet;
import com.bigo.project.bigo.wallet.entity.WalletEntity;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.system.domain.SysUser;
import com.bigo.project.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description: 后台钱包查询controller
 * @author: wenxm
 * @date: 2020/7/2 16:43
 */
@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController extends BaseController {


    @Autowired
    private IWalletService walletService;

    @Autowired
    private IBigoUserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IAgentService agentService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private IProductInfoService productInfoService;

    @Autowired
    private IProductOrderService productOrderService;

    @Value("${config.trxService}")
    private String trxService;



    @PreAuthorize("@ss.hasPermi('bigo:wallet:list')")
    @GetMapping("/list")
    public TableDataInfo list(WalletEntity entity, HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser sysUser = loginUser.getUser();
        Agent agent = agentService.getByUserId(sysUser.getUserId());
        //代理商只能看到自己的信息
        if(agent != null){
            entity.setAgentId(agent.getAgentCode());
        }
        startPage();
        List<WalletEntity> list = walletService.listByEntity(entity);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('bigo:wallet:balance')")
    @Log(title = "内部充币", businessType = BusinessType.INSIDE_RECHARGE)
    @PutMapping("/addBalance")
    public AjaxResult addBalance(@RequestBody WalletEntity entity) {
        if(entity.getIds().length < 1) {
            return AjaxResult.error("请选择ID");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }


        for (Long id : entity.getIds()) {
            Wallet wallet = walletService.getById(id);
            if(wallet == null){
                return AjaxResult.error("钱包不存在");
            }
            BigoUser user = userService.getUserByUid(wallet.getUid());
            if(entity.getAmount().compareTo(BigDecimal.ZERO) <= 0){
                return AjaxResult.error("数量必须大于0");
            }
            if(entity.getEditType() == null) {
                return AjaxResult.error("请选择操作类型");
            }
            Boolean addStatus = redisTemplate.opsForValue().setIfAbsent("addBalance_"+wallet.getId(),"", 5, TimeUnit.SECONDS);
            if(!addStatus) {
                return AjaxResult.error("操作过于频繁，请稍后再试！");
            }
            walletService.addBalance(user, entity, wallet );
            redisTemplate.delete("addBalance_"+wallet.getId());
        }
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('bigo:wallet:buyProduct')")
    @Log(title = "购买理财", businessType = BusinessType.INSIDE_RECHARGE)
    @PostMapping("/buyProductAll")
    public AjaxResult buyProductAll(@RequestBody WalletEntity entity) {
        if(entity.getIds().length < 1) {
            return AjaxResult.error("请选择ID");
        }
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
       /* SysUser sysUser = loginUser.getUser();
        if (sysUser.getUserId() != 1) return AjaxResult.error("请使用超管账号操作");
        sysUser = sysUserService.selectUserById(sysUser.getUserId());
        if (entity.getGoogleCaptcha() == null || !GoogleAuthenticator.checkGoogleCode(sysUser.getGoogleAuthSecretKey(), entity.getGoogleCaptcha())) {
            return AjaxResult.error("谷歌验证码不正确");
        }*/

        ProductInfo productInfo = productInfoService.selectProductInfoById(entity.getProductId());
        if(productInfo == null) return AjaxResult.error("理财产品不存在");
        ProductParam productParam = null;
        for (Long id : entity.getIds()) {
            Wallet wallet = walletService.getById(id);
            if(wallet == null){
                return AjaxResult.error("钱包不存在");
            }
            if(wallet.getBalance().compareTo(productInfo.getPurchaseAmountMin()) < 0) {
                continue;
            }
            if(!wallet.getCurrency().equals(CurrencyEnum.USDT.getCode())) {
                continue;
            }
            productParam = new ProductParam();
            productParam.setProductId(productInfo.getId());
            productParam.setBuyProducts(new BigDecimal(wallet.getBalance().longValue()));
            productParam.setUid(wallet.getUid());
            productOrderService.buyProducts(productParam, productInfo);
        }
        return AjaxResult.success();
    }



    @PreAuthorize("@ss.hasPermi('bigo:wallet:balance')")
    @Log(title = "内部充币", businessType = BusinessType.INSIDE_RECHARGE)
    @PutMapping("/addInsideBalance")
    public AjaxResult addInsideBalance(@RequestBody WalletEntity entity) {
        BigoUser user = userService.getUserByUid(entity.getUid());
        if(user == null) {
            return AjaxResult.error("用户不存在");
        }
        Wallet paramsWallet = new Wallet();
        paramsWallet.setCurrency(entity.getCurrency());
        paramsWallet.setUid(entity.getUid());
        paramsWallet.setType(0);
        Wallet wallet = walletService.getWallet(paramsWallet);
        if(wallet == null){
            return AjaxResult.error("钱包不存在");
        }
        if( user.getStatus() != 2 ){
            return AjaxResult.error("非内部账号无法充值该币种");
        }

        if(entity.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            return AjaxResult.error("数量必须大于0");
        }
        if(entity.getEditType() == null) {
            return AjaxResult.error("请选择操作类型");
        }
        Boolean addStatus = redisTemplate.opsForValue().setIfAbsent("addBalance_"+wallet.getId(),"", 5, TimeUnit.SECONDS);
        if(!addStatus) {
            return AjaxResult.error("操作过于频繁，请稍后再试！");
        }
        if(entity.getEditType() == 1) {
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.INSIDE_RECHARGE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            walletService.changeAsset(change);
        } else if(entity.getEditType() == 19) {
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.INTERNAL_DEDUCTION)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            walletService.changeAsset(change);
        }
        redisTemplate.delete("addBalance_"+wallet.getId());
        return AjaxResult.success();
    }



    @PreAuthorize("@ss.hasPermi('bigo:wallet:balance')")
    @Log(title = "获取btc余额")
    @GetMapping("/getBtcBalance")
    public AjaxResult getBtcBalance() throws IOException {
        Double balance =  (Double) BTCWalletUtils.getBalance();
        return AjaxResult.success(balance);
    }

    @PreAuthorize("@ss.hasPermi('bigo:wallet:collection')")
    @Log(title = "归集")
    @PutMapping("/collection")
    public AjaxResult collection() throws IOException {
        String collectionUrl = trxService+"/check/startCollectMin/1";
        String result = OkHttpUtil.post(collectionUrl,null);
        JSONObject resultJson = JSONObject.parseObject(result);
        Integer code = resultJson.getInteger("code");
        if(code != 0){
            return AjaxResult.error("执行归集失败");
        }
        return AjaxResult.success("执行归集成功");
    }

    @PreAuthorize("@ss.hasPermi('bigo:wallet:scheduling')")
    @Log(title = "调仓", businessType = BusinessType.INSIDE_RECHARGE)
    @PutMapping("/scheduling")
    public AjaxResult scheduling(@RequestBody WalletEntity entity) {
        Wallet wallet = walletService.getById(entity.getId());
        if(wallet == null){
            return AjaxResult.error("钱包不存在");
        }
        BigoUser user = userService.getUserByUid(wallet.getUid());
//        if( user.getStatus() != 2 ){
//            return AjaxResult.error("非内部账号无法充值该币种");
//        }
        /*if(!CurrencyEnum.VST.getCode().equals(wallet.getCurrency()) && user.getStatus() != 2){
            return AjaxResult.error("非内部账号无法充值该币种");
        }*/
        if(entity.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            return AjaxResult.error("数量必须大于0");
        }
        if(entity.getEditType() == null) {
            return AjaxResult.error("请选择操作类型");
        }
        Boolean addStatus = redisTemplate.opsForValue().setIfAbsent("addBalance_"+wallet.getId(),"", 5, TimeUnit.SECONDS);
        if(!addStatus) {
            return AjaxResult.error("操作过于频繁，请稍后再试！");
        }
        if(entity.getEditType() == 1) {// 冻结
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(0)
                    .type(AssetLogTypeEnum.INSIDE_RECHARGE)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            walletService.changeAsset(change);
        }else {//释放
            AssetChange change = AssetChange.builder().uid(wallet.getUid())
                    .currency(wallet.getCurrency())
                    .dim(1)
                    .type(AssetLogTypeEnum.INTERNAL_DEDUCTION)
                    .walletType(wallet.getType())
                    .amount(entity.getAmount())
                    .build();
            walletService.changeAsset(change);
        }
        redisTemplate.delete("addBalance_"+wallet.getId());
        return AjaxResult.success();
    }



}
