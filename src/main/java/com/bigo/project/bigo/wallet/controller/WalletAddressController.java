package com.bigo.project.bigo.wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.eth.EthWalletUtils;
import com.bigo.common.utils.poi.ExcelUtil;
import com.bigo.framework.aspectj.lang.annotation.Log;
import com.bigo.framework.aspectj.lang.enums.BusinessType;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.wallet.domain.Currency;
import com.bigo.project.bigo.wallet.domain.TronTransaction;
import com.bigo.project.bigo.wallet.domain.WalletAddress;
import com.bigo.project.bigo.wallet.domain.WalletTransaction;
import com.bigo.project.bigo.wallet.service.ITronTransactionService;
import com.bigo.project.bigo.wallet.service.IWalletAddressService;
import com.bigo.project.bigo.wallet.service.IWalletService;
import com.bigo.project.bigo.wallet.service.IWalletTransactionService;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 钱包地址Controller
 * 
 * @author bigo
 * @date 2021-01-28
 */
@RestController
@RequestMapping("/wallet/walletAddress")
public class WalletAddressController extends BaseController
{
    @Autowired
    private IWalletAddressService walletAddressService;

    @Autowired
    private IWalletService walletService;

    @Autowired
    private IWalletTransactionService walletTransactionService;

    @Autowired
    private ITronTransactionService tronTransactionService;


    /**
     * 查询钱包地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:walletAddress:list')")
    @GetMapping("/list")
    public TableDataInfo list(WalletAddress walletAddress)
    {
        startPage();
        List<WalletAddress> list = walletAddressService.selectWalletAddressList(walletAddress);
        /*for (WalletAddress address : list) {
            if(address.getCoin().equals("ETH") && address.getBalance().compareTo(new BigDecimal(0.001)) >= 0) {
                address.setBalance(BigDecimal.ZERO);
            }
        }*/
        return getDataTable(list);
    }

    /**
     * 导出钱包地址列表
     */
    @PreAuthorize("@ss.hasPermi('wallet:walletAddress:export')")
    @Log(title = "钱包地址列表", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(WalletAddress entity)
    {
        startPage(1,99999);
        List<WalletAddress> list = walletAddressService.selectWalletAddressList(entity);
        ExcelUtil<WalletAddress> util = new ExcelUtil<WalletAddress>(WalletAddress.class);
        return util.exportExcel(list, "walletAddress");
    }

    /**
     * 获取钱包地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('wallet:walletAddress:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(walletAddressService.selectWalletAddressById(id));
    }

    /**
     * 获取手续费接口
     * @param id
     * @return
     */
    @GetMapping(value = "/getGasPrice/{id}/{uid}")
    public AjaxResult getGasPrice(@PathVariable("id") Long id, @PathVariable("uid") Long uid) {
        try {
            WalletAddress entity = new WalletAddress();
            entity.setCoin(CurrencyEnum.ETH.getCode());
            entity.setUid(uid);
            List<WalletAddress> walletAddressList = walletAddressService.selectWalletAddressList(entity);
            if(walletAddressList == null || walletAddressList.size() <= 0) {
                return AjaxResult.error("获取归集手续费失败");
            }
            BigDecimal ethBalance = walletAddressList.get(0).getBalance();

            JSONObject jsonObject = EthWalletUtils.getGasPrice(id);
            if(jsonObject == null) {
                return AjaxResult.error("获取归集手续费失败");
            }
            JSONObject data = jsonObject.getJSONObject("data");
            data.put("ethBalance", ethBalance);
            return AjaxResult.success(data);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("获取归集手续费失败");
        }
    }


    /**
     * 归集
     * @param jsonObject
     * @return
     */
    @PostMapping(value = "/collection")
    public AjaxResult getGasPrice(@RequestBody JSONObject jsonObject) {
        Long id = jsonObject.getLong("id");
        BigDecimal gasPrice = jsonObject.getBigDecimal("gasPrice");
        try {
            JSONObject object = EthWalletUtils.submitCollection(id, gasPrice);
            if(jsonObject == null) {
                return AjaxResult.error("提交归集失败");
            }
            return AjaxResult.success("归集成功");
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error();
        }
    }

    /**
     * 人工上分
     * @param jsonObject
     * @return
     */
    @PreAuthorize("@ss.hasPermi('wallet:walletAddress:manualScoring')")
    @PostMapping(value = "/manualScoring")
    public AjaxResult manualScoring(@RequestBody JSONObject jsonObject) {
        Long id = jsonObject.getLong("id");
        String fromAddress = jsonObject.getString("fromAddress");
        String hash = jsonObject.getString("hash");
        Long blockNum = jsonObject.getLong("blockNum");
        BigDecimal rechargeNum = jsonObject.getBigDecimal("rechargeNum");
        WalletAddress address = walletAddressService.selectWalletAddressById(id);
        if (address == null) return AjaxResult.error("请选择正确钱包地址");
        String coin = address.getCoin();
        if(coin.equals(CurrencyEnum.USDT.getCode())
                || coin.equals(CurrencyEnum.ETH.getCode())
                || coin.equals(CurrencyEnum.BTC.getCode())) {
            walletTransactionService.manualScoring(address,fromAddress, hash, blockNum,rechargeNum);
        }else if(coin.equals("USDT_TRC20")) {
            tronTransactionService.manualScoring(address,fromAddress, hash, blockNum,rechargeNum);
        }
        return AjaxResult.success();
    }
}
