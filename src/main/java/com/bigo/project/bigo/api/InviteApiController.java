package com.bigo.project.bigo.api;

import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.DictUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.vo.*;
import com.bigo.project.bigo.contract.domain.Contract;
import com.bigo.project.bigo.contract.service.IContractService;
import com.bigo.project.bigo.enums.CurrencyEnum;
import com.bigo.project.bigo.ico.service.IIcoExchangeHistoryService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import com.bigo.project.bigo.userinfo.service.IUserLevelService;
import com.bigo.project.bigo.wallet.service.IAssetLogService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 我的邀请信息api控制器
 * @author: wenxm
 * @date: 2020/7/1 13:50
 */
@RestController
@RequestMapping("/api/invite")
public class InviteApiController {

    @Autowired
    private IAssetLogService assetLogService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserLevelService levelService;

    @Autowired
    private IBigoUserService userService;

    @Autowired
    private IContractService contractService;

    @Autowired
    private IIcoExchangeHistoryService icoExchangeHistoryService;

    /**
     * 获取邀请收益
     * @param coin 币种
     * @return
     */
    @GetMapping("/myProfit")
    public AjaxResult myProfit(@RequestParam("coin") String coin){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        ProfitVO profitVO = assetLogService.getProfitInfo(user.getUid(), coin);
        return AjaxResult.success(profitVO);
    }

    /**
     * 我的邀请信息
     * @return
     */
    @GetMapping("/myInvitation")
    public AjaxResult myInvitation(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        MyInvitationVO invitation = new MyInvitationVO();
        //获取邀请记录和邀请人数
        Integer inviteNum = 0;
        Integer todayInvite = 0;
        Integer yesterdayInvite = 0;
        List<ChildVO> childList = userService.listMaskChild(Lists.newArrayList(user.getUid()));
        for(ChildVO childVO : childList){
            if(DateUtils.isYesterday(childVO.getInviteTime())){
                yesterdayInvite++;
            }
            if(DateUtils.isToday(childVO.getInviteTime())){
                todayInvite++;
            }
        }
        List<Long> uidList = childList.stream().map(ChildVO::getUid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(childList)){
            inviteNum += childList.size();
            List<ChildVO> nextList = userService.listMaskChild(uidList);
            if(!CollectionUtils.isEmpty(nextList)){
                inviteNum += nextList.size();
                childList.addAll(nextList);
            }
        }
        //获取下级交易(昨天和今天)
        Integer todaySize = 0;
        Integer yesterdaySize = 0;
        if(uidList !=null && uidList.size() >0) {
            List<Long> todayList = icoExchangeHistoryService.statisticTradeNumber(uidList, 0);
            if(todayList !=null && todayList.size() >0) todaySize = todayList.size();
            List<Long> yesterdayList = icoExchangeHistoryService.statisticTradeNumber(uidList, 1);
            if(yesterdayList !=null && yesterdayList.size() >0) yesterdaySize = yesterdayList.size();
        }
//        List<Long> todayList = Lists.newArrayList();
//        List<Long> yesterdayList = Lists.newArrayList();
//        Map<String, BigDecimal> todayTradeMap = createTradeMap();
//        Map<String, BigDecimal> yesterdayTradeMap = createTradeMap();
        /*List<Contract> contractList = contractService.listContract(uidList, DateUtils.getStartTime(new Date(),-1),DateUtils.getEndTime(new Date(),0));
        for(Contract contract : contractList){
            if(DateUtils.isToday(contract.getBuyTime())){
                if(!todayList.contains(contract.getUid())){
                    todayList.add(contract.getUid());
                }
                BigDecimal total = todayTradeMap.get(contract.getCurrency()).add(contract.getMoney());
                todayTradeMap.put(contract.getCurrency(), total);
            }else if(DateUtils.isYesterday(contract.getBuyTime())){
                if(!yesterdayList.contains(contract.getUid())){
                    yesterdayList.add(contract.getUid());
                }
                BigDecimal total = yesterdayTradeMap.get(contract.getCurrency()).add(contract.getMoney());
                yesterdayTradeMap.put(contract.getCurrency(), total);
            }
        }*/
        invitation.setTodayTrade(new ChildTradeVO(todayInvite, todaySize, null));
        invitation.setYesterdayTrade(new ChildTradeVO(yesterdayInvite, yesterdaySize, null));
        invitation.setTotalInviteNum(inviteNum);
        invitation.setChildList(childList);
        return AjaxResult.success(invitation);
    }

    /**
     * 我的邀请信息（PC端使用）
     * @return
     */
    @GetMapping("/myInviteInfo")
    public AjaxResult myInviteInfo(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        MyInviteInfoVO invitation = new MyInviteInfoVO();
        //获取邀请记录和邀请人数
        Integer inviteNum = 0;
        List<ChildVO> childList = userService.listMaskChild(Lists.newArrayList(user.getUid()));
        List<Long> uidList = childList.stream().map(ChildVO::getUid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(childList)){
            inviteNum += childList.size();
            List<ChildVO> nextList = userService.listMaskChild(uidList);
            if(!CollectionUtils.isEmpty(nextList)){
                inviteNum += nextList.size();
                childList.addAll(nextList);
            }
        }
        ProfitVO usdtProfit = assetLogService.getProfitInfo(user.getUid(), CurrencyEnum.USDT.getCode());
        ProfitVO ethProfit = assetLogService.getProfitInfo(user.getUid(), CurrencyEnum.ETH.getCode());
        String link = DictUtils.getDictValue("bigo_base_config", "pc_share_link");
        if(link != null){
            invitation.setInviteUrl(link +"&code="+ user.getUid());
        }
        invitation.setInviteNum(inviteNum);
        invitation.setChildList(childList);
        invitation.setUsdtProfit(usdtProfit.getTotalProfit());
        invitation.setEthProfit(ethProfit.getTotalProfit());
        return AjaxResult.success(invitation);
    }

    private Map<String, BigDecimal> createTradeMap(){
        Map<String, BigDecimal> map = Maps.newHashMapWithExpectedSize(2);
        map.put("USDT", BigDecimal.ZERO);
        map.put("ETH", BigDecimal.ZERO);
        return map;
    }
}
