package com.bigo.project.bigo.api;

import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.RedisUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.framework.security.service.TokenService;
import com.bigo.framework.web.domain.AjaxResult;
import com.bigo.project.bigo.api.vo.JackpotVo;
import com.bigo.project.bigo.luck.domain.Jackpot;
import com.bigo.project.bigo.luck.domain.LotteryCode;
import com.bigo.project.bigo.luck.domain.WinningRecord;
import com.bigo.project.bigo.luck.service.IJackpotService;
import com.bigo.project.bigo.luck.service.ILotteryCodeService;
import com.bigo.project.bigo.luck.service.IWinningRecordService;
import com.bigo.project.bigo.userinfo.domain.BigoUser;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import netscape.javascript.JSObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @description: 抽奖API
 * @author: wenxm
 * @date: 2020/12/31 17:16
 */
@RestController
@RequestMapping("/api/luck")
public class LuckApiController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private IBigoUserService bigoUserService;

    @Autowired
    private IJackpotService jackpotService;

    @Autowired
    private IWinningRecordService winningRecordService;

    @Autowired
    private ILotteryCodeService lotteryCodeService;

    @Autowired
    private StringRedisTemplate redisTemplate;



    /**
     * 奖池列表
     * @return
     */
    @GetMapping("/jackpotList")
    public AjaxResult jackpotList(){
        List<Jackpot> jackpotList = jackpotService.selectJackpotList(new Jackpot());
        List<JackpotVo> voList = new ArrayList<>();
        jackpotList.forEach(item ->{
            JackpotVo vo = new JackpotVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        });
        return AjaxResult.success(voList);
    }

    /**
     * 中奖记录
     */
    @GetMapping("/winningRecord")
    public AjaxResult winningRecord(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        WinningRecord record = new WinningRecord();
        record.setUid(user.getUid());
        List<WinningRecord> recordList = winningRecordService.selectWinningRecordList(record);
        return AjaxResult.success(recordList);
    }

    /**
     * 平台中奖记录
     */
    @GetMapping("/platformRecord")
    public AjaxResult platformRecord() {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<WinningRecord> list = RedisUtils.getCacheList("platformRecord");
        return AjaxResult.success(list);
    }

    /**
     * 我的抽奖码
     */
    @GetMapping("/myLotteryCode")
    public AjaxResult myLotteryCode(){
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        List<LotteryCode> lotteryCodes = lotteryCodeService.selectLotteryCodeList(new LotteryCode(user.getUid(),null, 1));
        List<LotteryCode> lotteryCodeListVo = new ArrayList<>();
        Date nowDate = nowDate();
        if(lotteryCodes.size() > 0) {
            for (LotteryCode lotteryCode : lotteryCodes) {
                if(nowDate.compareTo(lotteryCode.getOverdueTime()) > 0 ) {
                    continue;
                }
                lotteryCodeListVo.add(lotteryCode);
            }
        }
        return AjaxResult.success(lotteryCodeListVo);
    }

    /**
     * 抽奖
     * @param code 抽奖码
     * @return
     */
    @GetMapping("/luckDraw")
    public AjaxResult luckDraw(@RequestParam("code") String code) {
        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());
        if(StringUtils.isBlank(code)) {
            return AjaxResult.error("lottery_code_not_available");  // 抽奖码不可用
        }
        code = code.toUpperCase();
        // 查询抽奖码是否存在，状态是否可用
        Date nowDate = nowDate();
        LotteryCode lotteryCode = new LotteryCode(null, code, 1);
        lotteryCode = lotteryCodeService.getCode(lotteryCode);
        if(lotteryCode == null || lotteryCode.getOverdueTime().compareTo(nowDate) < 0) {
            return AjaxResult.error("lottery_code_not_available");  // 抽奖码不可用
        }
        // 抽奖
        Boolean status = redisTemplate.opsForValue().setIfAbsent(code,"", 10, TimeUnit.SECONDS);
        if(!status){
            return AjaxResult.error("lottery_code_not_available");  // 抽奖码不可用
        }
        Jackpot jackpot = jackpotService.luckDraw(lotteryCode, user);
        JackpotVo vo = new JackpotVo();
        BeanUtils.copyProperties(jackpot, vo);
        redisTemplate.delete(code);
        return AjaxResult.success(vo);
    }

 /*   @PostMapping("/giveLotteryCode")
    public AjaxResult giveLotteryCode(@RequestBody JSONObject jsonObject) {
        String code = jsonObject.getString("lotteryCode");
        // 接收人账号
        String username = jsonObject.getString("receiver");

        BigoUser user = tokenService.getBigoUser(ServletUtils.getRequest());

        // 查询抽奖码是否存在，状态是否可用
        LotteryCode lotteryCode = new LotteryCode(user.getUid(), code, 1);
        lotteryCode = lotteryCodeService.getCode(lotteryCode);
        Date nowDate = nowDate();
        if(lotteryCode == null || lotteryCode.getOverdueTime().compareTo(nowDate) < 0) {
            return AjaxResult.error("lottery_code_not_available");  // 抽奖码不可用
        }
        // 查询接收人账号是否存在
        BigoUser receiver = bigoUserService.getUserByEmail(username);
        if(receiver == null) {
            return AjaxResult.error("to_account_does_not_exist");
        }
        LotteryCode update = new LotteryCode();
        update.setId(lotteryCode.getId());
        update.setUid(receiver.getUid());
        lotteryCodeService.updateLotteryCode(update);
        return AjaxResult.success();
    }*/

    private Date nowDate(){
        String nowDateStr = DateUtils.getDate();
        return DateUtils.parseDate(nowDateStr);
    }

}
