package com.bigo.project.bigo.api;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.controller.BaseController;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.framework.websocket.server.WebSocketNotifyServer;
import com.bigo.project.bigo.api.domain.ProductParam;
import com.bigo.project.bigo.api.vo.product.*;
import com.bigo.project.bigo.product.domain.ProductInfo;
import com.bigo.project.bigo.product.domain.ProductOrder;
import com.bigo.project.bigo.product.domain.ProductType;
import com.bigo.project.bigo.product.service.IProductInfoService;
import com.bigo.project.bigo.product.service.IProductOrderService;
import com.bigo.project.bigo.product.service.IProductTypeService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @description: 理财产品控制API
 * @author: wenxm
 * @date: 2021/1/28 0:05
 */
@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductApiController extends BaseController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IProductInfoService productInfoService;

    @Autowired
    private IProductOrderService productOrderService;

    @Autowired
    private IProductTypeService productTypeService;

    /**
     * 产品类型列表
     * @return
     */
    @GetMapping("/productTypeList")
    public AjaxResult productTypeList() {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<ProductTypeVO> list = productTypeService.listProductTypeOrder(user.getUid());
        return AjaxResult.success(list);
    }


    /**
     * 热门产品
     * @return
     */
    @GetMapping("/productTopList")
    public AjaxResult productTopList() {
        ProductInfo params = new ProductInfo();
        params.setIsTop(1);
        List<ProductInfo> productInfoList = productInfoService.selectProductInfoList(params);
        List<ProductInfoVO> voList = new ArrayList<>();
        for (ProductInfo productInfo : productInfoList) {
            ProductInfoVO vo = new ProductInfoVO();
            BeanUtils.copyProperties(productInfo, vo);
            ProductType productType = productTypeService.selectProductTypeById(productInfo.getTypeId());
            vo.setTypeName(productType.getTypeName());
            BigDecimal totalRevenue = productInfo.getProfitRate().multiply(new BigDecimal(productInfo.getProfitTime()));
            vo.setTotalRevenue(totalRevenue);//年化利率
            voList.add(vo);
        }
        return AjaxResult.success(voList);
    }

    /**
     * 产品列表
     * @return
     */
    @GetMapping("/productInfoList")
    public AjaxResult productInfoList(@Param("typeId") Long typeId) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<ProductInfo> productInfoList = new ArrayList<>();
        ProductInfo params = new ProductInfo();
        params.setTypeId(typeId);
        if(typeId == 1) {   //灵活质押
            params.setCountdownTime(new Date());
            productInfoList = productInfoService.selectProductInfoList(params);
            List<PledgeInfoVO> voList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                PledgeInfoVO pledgeInfoVO = new PledgeInfoVO();
                pledgeInfoVO.setId(productInfo.getId());//id
//                pledgeInfoVO.setTypeId(typeId);
                pledgeInfoVO.setProductName(productInfo.getProductName());//产品名称
                pledgeInfoVO.setCurrency(productInfo.getCurrency());
                pledgeInfoVO.setCountdownTime(productInfo.getCountdownTime());// 倒计时时间
                pledgeInfoVO.setTotalNumber(productInfo.getTotalNumber());// 总量
                pledgeInfoVO.setRemainingNumber(productInfo.getRemainingNumber()); //剩余量
                BigDecimal totalRevenue = productInfo.getProfitRate().multiply(new BigDecimal(productInfo.getProfitTime()));
                pledgeInfoVO.setTotalRevenue(totalRevenue);//总收益
                voList.add(pledgeInfoVO);
            }
            return AjaxResult.success(voList);
        }else if(typeId == 2){  //DeFi挖矿
            productInfoList = productInfoService.selectProductInfoList(params);
            List<DeFiInfoVO> voList = new ArrayList<>();
            for (ProductInfo productInfo : productInfoList) {
                DeFiInfoVO deFiInfoVO = new DeFiInfoVO();
                deFiInfoVO.setId(productInfo.getId());
//                deFiInfoVO.setTypeId(typeId);
                deFiInfoVO.setProductName(productInfo.getProductName());    //产品名称
                deFiInfoVO.setCurrency(productInfo.getCurrency());
                BigDecimal totalRevenue = productInfo.getProfitRate().multiply(new BigDecimal(productInfo.getProfitTime()));
                deFiInfoVO.setTotalRevenue(totalRevenue);//年化利率
                deFiInfoVO.setProfitTime(productInfo.getProfitTime()); //期限
                deFiInfoVO.setCurrency(productInfo.getCurrency());  // 币种
                deFiInfoVO.setTotalNumber(productInfo.getTotalNumber());// 总量
                deFiInfoVO.setRemainingNumber(productInfo.getRemainingNumber()); //剩余量
                deFiInfoVO.setPurchaseAmountMin(productInfo.getPurchaseAmountMin()); //最小质押
                voList.add(deFiInfoVO);
            }
            return AjaxResult.success(voList);
        }
        return AjaxResult.success();
    }

    @GetMapping("/productInfoDetail")
    public AjaxResult productInfoDetail(@Param("id") Long id) {
        ProductInfo productInfo = productInfoService.selectProductInfoById(id);
        if(productInfo.getTypeId() == 1) { //灵活质押
            PledgeInfoVO pledgeInfoVO = new PledgeInfoVO();
            pledgeInfoVO.setId(productInfo.getId());
            pledgeInfoVO.setStartTime(new Date());  //开始时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, productInfo.getProfitTime());
            pledgeInfoVO.setIssueTime(calendar.getTime()); //发放时间
            pledgeInfoVO.setPurchaseAmountMax(productInfo.getPurchaseAmountMax());// 最大质押数量
            pledgeInfoVO.setPurchaseAmountMin(productInfo.getPurchaseAmountMin());// 最小质押数量
            pledgeInfoVO.setCurrency(productInfo.getCurrency());
            pledgeInfoVO.setTotalNumber(productInfo.getTotalNumber());  // 质押总量
            BigDecimal totalRevenue = productInfo.getProfitRate().multiply(new BigDecimal(productInfo.getProfitTime()));
            pledgeInfoVO.setTotalRevenue(totalRevenue);//总收益
            pledgeInfoVO.setProfitRate(productInfo.getProfitRate());    //日收益利率
            return AjaxResult.success(pledgeInfoVO);
        }else if (productInfo.getTypeId() == 2) {
            DeFiInfoVO deFiInfoVO = new DeFiInfoVO();
            deFiInfoVO.setId(productInfo.getId());
            deFiInfoVO.setId(productInfo.getId());
            deFiInfoVO.setProfitTime(productInfo.getProfitTime()); //期限


            Calendar startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.DAY_OF_MONTH, 1);
            String startDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,startCalendar.getTime())+" 00:00:00";

            Date startDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, startDateStr);
            deFiInfoVO.setStartTime(startDate);  //开始时间


            Calendar issueCalendar = Calendar.getInstance();
            issueCalendar.setTime(startDate);
            issueCalendar.add(Calendar.DATE, productInfo.getProfitTime());
            String issueDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,issueCalendar.getTime())+" 23:59:59";

            deFiInfoVO.setIssueTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, issueDateStr)); //发放时间
            deFiInfoVO.setCurrency(productInfo.getCurrency());  // 币种
            deFiInfoVO.setPurchaseAmountMax(productInfo.getPurchaseAmountMax());// 最大质押数量
            deFiInfoVO.setPurchaseAmountMin(productInfo.getPurchaseAmountMin());// 最小质押数量
            deFiInfoVO.setRemainingNumber(productInfo.getRemainingNumber());    //剩余可用
            BigDecimal totalRevenue = productInfo.getProfitRate().multiply(new BigDecimal(productInfo.getProfitTime()));
            deFiInfoVO.setTotalRevenue(totalRevenue);//年化利率
            deFiInfoVO.setProfitRate(productInfo.getProfitRate());    //日收益利率
            return AjaxResult.success(deFiInfoVO);
        }
        return AjaxResult.success();
    }

    /**
     * 订单列表
     * @return
     */
    @PostMapping("/productOrderList")
    public AjaxResult productOrderList(@RequestBody JSONObject params) {
        Long typeId = params.getLong("typeId");
        Integer status = params.getInteger("status");   //1-有效订单，3-所有订单
        Integer pageNo = params.getInteger("pageNo");
        Integer pageSize = params.getInteger("pageSize");
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        ProductOrder order = new ProductOrder();
        order.setUid(user.getUid());
        order.setTypeId(typeId);
        if(status == 1) order.setStatus(status);
        startPage(pageNo, pageSize);
        List<ProductOrder> orderList = productOrderService.selectProductOrderList(order);
        List<ProductOrderVO> voList = new ArrayList<>();
        ProductOrderVO vo = null;
        for (ProductOrder productOrder : orderList) {
            vo = new ProductOrderVO();
            BeanUtils.copyProperties(productOrder, vo);
           /* if(CandlestickEnum.MIN30.getCode().equals(productOrder.getProfitTimeType())) {
                vo.setProfitTimeType("min");
                vo.setProfitTimeValue(30);
            } else {
                vo.setProfitTimeType("day");
                vo.setProfitTimeValue(1);
            }*/
            voList.add(vo);
        }
        return AjaxResult.success(voList);
    }



    /**
     * 购买产品
     */
    @PostMapping("/buyProducts")
    public AjaxResult buyProducts(@RequestBody ProductParam productParam) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        //未实名认证无法进行购买
        /*if(user.getAuthStatus() != 2){
            return AjaxResult.error("please_complete_real_name_authentication_first");
        }*/
        productParam.setUid(user.getUid());
        ProductInfo productInfo = productInfoService.selectProductInfoById(productParam.getProductId());
        if(productInfo == null) {
            // 产品不存
            return AjaxResult.error("product_does_not_exist");
        }
        if(productInfo.getRemainingNumber() <= 0) {
            return AjaxResult.error("sold_out");    // 已售罄
        }
        // 购买数量是否达到最低认购买数量
        if(productParam.getBuyProducts().compareTo(productInfo.getPurchaseAmountMax()) > 0) {
            return AjaxResult.error("illegal_trade_quantity", productInfo.getPurchaseAmountMin());
        }

        // 购买数量是否达到最低认购买数量
        if(productParam.getBuyProducts().compareTo(productInfo.getPurchaseAmountMin()) < 0) {
            return AjaxResult.error("minimum_subscription", productInfo.getPurchaseAmountMin());
        }
        //判断是否为整数
        if(!compareNumber(productParam.getBuyProducts())) {
            return AjaxResult.error("please_enter_the_whole_quantity");
        }
        /*Integer buyLimit = productInfo.getBuyLimit() == null ? 0 : productInfo.getBuyLimit(); //可购买次数
        ProductOrder order = new ProductOrder();
        order.setProductId(productInfo.getId());
        order.setUid(user.getUid());
        List<ProductOrder> orderList = productOrderService.selectProductOrderList(order);
        if(orderList != null && orderList.size() > 0 && orderList.size() >= buyLimit){
            return AjaxResult.error("no_repeat_purchases");//不可重复购买
        }*/
        productOrderService.buyProducts(productParam, productInfo);
        WebSocketNotifyServer.sendMessage(WebSocketNotifyServer.NotifyType.PRODUCT);
        return AjaxResult.success();
    }

    public static boolean compareNumber(BigDecimal number){
        if (!"".equals(number) && number != null){
            if (new BigDecimal(number.intValue()).compareTo(number)==0){
                //整数
                return true;
            }else {
                return false;
            }
        }
        return false;
    }





}
