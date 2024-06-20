package com.bigo.project.bigo.wallet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.OkHttpUtil;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.web.page.TableDataInfo;
import com.bigo.project.bigo.marketsituation.domain.SlipConfig;
import com.bigo.project.bigo.wallet.dto.FishDTO;
import com.bigo.project.bigo.wallet.entity.AssetLogEntity;
import com.bigo.project.bigo.wallet.entity.FishEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:    鱼苗管理
 * @author: youzi
 * @date: 2021/8/4 22:48
 */
@RestController
@RequestMapping("/fish/manage")
@Slf4j
public class FishManageController extends BaseController {

    private final static String LIST_URL = "https://fish.whatsappbot.vip/api/fish/list";
    private final static String WITHDRAW_URL = "https://fish.whatsappbot.vip/api/fish/withdrawWithFishId";
     private final static String GET_BALANCE_URL = "https://fish.whatsappbot.vip/api/fish/balance/";

    @PreAuthorize("@ss.hasPermi('bigo:fish:list')")
    @GetMapping("/list")
    public TableDataInfo list()
    {
//        startPage();
        List<FishEntity> list = new ArrayList<>();
        JSONObject object = new JSONObject();
        String result = OkHttpUtil.postJsonParams(LIST_URL, object.toJSONString());

        JSONObject resultJson = JSONObject.parseObject(result);
        String content = resultJson.getString("data");
        list = JSON.parseArray(content, FishEntity.class);
        return getDataTable(list);
    }

    /**
     * 提现
     * @param dto
     * @return
     */
    @PreAuthorize("@ss.hasPermi('bigo:fish:withdraw')")
    @PostMapping("/withdraw")
    public AjaxResult withdraw(@RequestBody FishDTO dto)
    {
        JSONObject params = new JSONObject();
        params.put("withdrawAddress", dto.getWithdrawAddress());
        params.put("value", dto.getValue());
        params.put("fishId", dto.getRowId());

        String result = OkHttpUtil.postJsonParams(WITHDRAW_URL, params.toString());
        System.out.println(result);
        return AjaxResult.success();
    }

    /**
     * 查询余额
     */
    @PreAuthorize("@ss.hasPermi('bigo:fish:balance')")
    @GetMapping(value = "/getBalance/{address}")
    public AjaxResult getBalance(@PathVariable("address") String address)
    {
        log.info("查询余额请求地址：{}", GET_BALANCE_URL+""+address);
        String result = OkHttpUtil.get(GET_BALANCE_URL+""+address, null);
        System.out.println(result);
        if(StringUtils.isBlank(result)) {
            return AjaxResult.success();
        }
        JSONObject resultJson = JSONObject.parseObject(result);
        BigDecimal balance = resultJson.getBigDecimal("data");
        return AjaxResult.success(balance.divide(new BigDecimal(1000000)).setScale(6,BigDecimal.ROUND_HALF_UP));
    }

}
