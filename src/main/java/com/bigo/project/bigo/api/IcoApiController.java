package com.bigo.project.bigo.api;

import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.domain.ProductParam;
import com.bigo.project.bigo.api.dto.IcoExchangeHistoryDTO;
import com.bigo.project.bigo.api.dto.IcoProductDTO;
import com.bigo.project.bigo.api.dto.IcoSpotDTO;
import com.bigo.project.bigo.api.vo.ico.IcoProductVO;
import com.bigo.project.bigo.api.vo.ico.IcoSpotVO;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.ico.domain.IcoExchangeHistory;
import com.bigo.project.bigo.ico.domain.IcoProduct;
import com.bigo.project.bigo.ico.domain.IcoProductRecord;
import com.bigo.project.bigo.ico.domain.IcoSpot;
import com.bigo.project.bigo.ico.enums.SpotStatusEnum;
import com.bigo.project.bigo.ico.enums.SpotTypeEnum;
import com.bigo.project.bigo.ico.service.IIcoExchangeHistoryService;
import com.bigo.project.bigo.ico.service.IIcoProductRecordService;
import com.bigo.project.bigo.ico.service.IIcoProductService;
import com.bigo.project.bigo.ico.service.IIcoSpotService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/ico")
public class IcoApiController {


    @Autowired
    private TokenService tokenService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IIcoProductService icoProductService;

    @Autowired
    private IIcoProductRecordService icoProductRecordService;

    @Autowired
    private IIcoExchangeHistoryService icoExchangeHistoryService;

    @Autowired
    private IIcoSpotService icoSpotService;

    /**
     * 获取ico产品信息
     */
    @GetMapping("/icoProductList")
    public AjaxResult icoProductList(){
        List<IcoProductVO> voList = icoProductService.selectProductListVO(new IcoProductVO());
        return AjaxResult.success(voList);
    }


    /**
     * 获取ico产品信息
     */
    @GetMapping("/icoProductInfo/{id}")
    public AjaxResult getIcoProductInfo(@PathVariable Long id){
        List<IcoProductVO> voList = icoProductService.selectProductListVO(new IcoProductVO(id));
        IcoProductVO vo = new IcoProductVO();
        if(voList != null && voList.size() > 0) {
            vo = voList.get(0);
        }
        return AjaxResult.success(vo);
    }

    @PostMapping("/buyIcoProduct")
    public AjaxResult buyIcoProduct(@RequestBody IcoProductDTO dto) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        /*IcoProduct product = icoProductService.selectIcoProductById(dto.getIcoProductId());
        if(product == null) {
            // 产品不存
            return AjaxResult.error("product_does_not_exist");
        }
        if(product.getTotalNum() - product.getBoughtNum() <= 0) {
            return AjaxResult.error("sold_out");    // 已售罄
        }
        if((product.getTotalNum() - product.getBoughtNum()) < dto.getBuyNum().longValue()) {
            return AjaxResult.error("abnormal_payment_amount");
        }*/
//        icoProductService.buyIcoProduct(user, dto);
        return AjaxResult.success();
    }

    @PostMapping("/buy")
    public AjaxResult buy(@RequestBody IcoProductDTO dto) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(dto.getBuyNum().compareTo(BigDecimal.ZERO) <=0){
            return AjaxResult.error("illegal_trade_quantity");
        }
        if(StringUtils.isBlank(dto.getSymbol())) {
            return AjaxResult.error("unsupported_coin");
        }
        icoProductService.buy(user, dto);
        return AjaxResult.success();
    }

    @PostMapping("/sell")
    public AjaxResult sell(@RequestBody IcoProductDTO dto) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(dto.getSellNum().compareTo(BigDecimal.ZERO) <=0){
            return AjaxResult.error("illegal_trade_quantity");
        }
        if(StringUtils.isBlank(dto.getSymbol())) {
            return AjaxResult.error("unsupported_coin");
        }
        icoProductService.sell(user, dto);
        return AjaxResult.success();
    }

    /**
     * 获取ico产品信息
     */
    @PostMapping("/exchangeHistory")
    public AjaxResult exchangeHistory(@RequestBody IcoExchangeHistoryDTO dto, HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(request);
        List<IcoExchangeHistory> historyList = icoExchangeHistoryService.selectIcoExchangeHistoryList(new IcoExchangeHistory(user.getUid(),dto.getCurrency(),dto.getType()));
        return AjaxResult.success(historyList);
    }

     /**
     * 获取ico产品信息
     */
    @GetMapping("/icoRecord")
    public AjaxResult icoRecord(HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(request);
        List<IcoProductRecord> voList = icoProductRecordService.selectIcoProductRecordList(new IcoProductRecord(user.getUid()));
        return AjaxResult.success(voList);
    }

    /**
     * 现货
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/place")
    public AjaxResult place(@RequestBody IcoSpotDTO dto,HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(SpotTypeEnum.LIMIT.getType().equals(dto.getOrderType())){
            if(dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) return AjaxResult.error("abnormal_payment_amount");
            if(dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) return AjaxResult.error("abnormal_payment_amount");
        }

        if(SpotTypeEnum.MARKET.getType().equals(dto.getOrderType())){
            if(dto.getQuantity() == null && dto.getQuoteOrderQty() == null) return AjaxResult.error("abnormal_payment_amount");
            if(dto.getQuantity() != null && dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) return AjaxResult.error("abnormal_payment_amount");
            if(dto.getQuoteOrderQty() != null && dto.getQuoteOrderQty().compareTo(BigDecimal.ZERO) <= 0) return AjaxResult.error("abnormal_payment_amount");
        }
        icoSpotService.place(dto,user);
        return AjaxResult.success();
    }

    /**
     * 撤单
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/revoke")
    public AjaxResult revoke(@RequestBody IcoSpotDTO dto,HttpServletRequest request){
//        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        for (String orderId : dto.getOrderIds()) {
            IcoSpot spot = icoSpotService.selectIcoSpot(new IcoSpot(orderId, SpotStatusEnum.NEW.getStatus()));
            if(spot == null) continue;
            icoSpotService.revokeOrder(spot);
        }
        return AjaxResult.success();
    }

    /**
     * 当前订单
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/newOrders")
    public AjaxResult newOrders(@RequestBody IcoSpotDTO dto,HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        IcoSpotVO param = new IcoSpotVO();
//        if(dto.getStartTime() !=null) param.setStartTime(dto.getStartTime());
//        if(dto.getEndTime() != null) param.setEndTime(dto.getEndTime());
        if(StringUtils.isNotBlank(dto.getSide())) param.setSide(dto.getSide());
        if(StringUtils.isNotBlank(dto.getOrderType())) param.setOrderType(dto.getOrderType());
        if(StringUtils.isNotBlank(dto.getSymbol())) param.setSymbol(dto.getSymbol().toUpperCase());
        param.setStatus(SpotStatusEnum.NEW.getStatus());
        param.setUid(user.getUid());
        List<IcoSpotVO> list = icoSpotService.selectIcoSpotVOList(param);
        return AjaxResult.success(list);
    }

    /**
     * 历史订单
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/tradeOrders")
    public AjaxResult tradeOrders(@RequestBody IcoSpotDTO dto,HttpServletRequest request){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        IcoSpotVO param = new IcoSpotVO();
        if(dto.getStartTime() !=null) param.setStartTime(dto.getStartTime());
        if(dto.getEndTime() != null) param.setEndTime(dto.getEndTime());
        if(StringUtils.isNotBlank(dto.getSide())) param.setSide(dto.getSide());
        if(StringUtils.isNotBlank(dto.getOrderType())) param.setOrderType(dto.getOrderType());
        if(StringUtils.isNotBlank(dto.getSymbol())) param.setSymbol(dto.getSymbol().toUpperCase());
        if(StringUtils.isNotBlank(dto.getStatus())) {
            param.setStatus(dto.getStatus().toUpperCase());
        }else {
            List<String> statusList = new ArrayList<>();
            statusList.add(SpotStatusEnum.FILLED.getStatus());
            statusList.add(SpotStatusEnum.CANCELED.getStatus());
            param.setStatusList(statusList);
        }
        param.setUid(user.getUid());
        List<IcoSpotVO> list = icoSpotService.selectIcoSpotVOList(param);
        return AjaxResult.success(list);
    }

}
