package com.bigo.project.bigo.task;

import com.bigo.common.utils.*;
import com.bigo.project.bigo.api.vo.RechargeVo;
import com.bigo.project.bigo.config.dao.EmailConfigRepository;
import com.bigo.project.bigo.config.jpaEntity.EmailConfig;
import com.bigo.project.bigo.luck.service.IJackpotService;
import com.bigo.project.bigo.userinfo.service.IBigoUserService;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/1/9 22:21
 */
@Component("commonTask")
@Slf4j
public class CommonTask {

    @Value("${msg.sendName}")
    private String sendName;

    @Autowired
    IJackpotService jackpotService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private MailProperties properties;
    @Autowired
    private IBigoUserService bigoUserService;

    @Resource
    private EmailConfigRepository emailConfigRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    private static Integer SEND_COUNT = 0;


    /**
     * 随机生成充值记录
     */
    public void randomRecharge() {
        List<RechargeVo> list = new ArrayList<>();
        RechargeVo vo = null;
        // 随机生成平台认购记录
        String dateTime = DateUtils.dateTimeNowMD();
        int random = StringUtils.getNum(20, 40); //随机生成20-40条记录
        for (int i = 0; i < random; i++) {
            vo = new RechargeVo();
            // 随机生成email
            String email = PrivacyUtils.maskEmail(StringUtils.getEmail(8, 12));
            BigDecimal amount = RandomNumberUtils.randomBigdecimal(10000L, 2000L,0);
            vo.setEmail(email);
            vo.setAmount(amount);
            vo.setDateTime(dateTime);
            list.add(vo);
        }
        RedisUtils.deleteObject("randomRecharge");
        RedisUtils.setCacheObject("randomRecharge", list);
    }
    /**
     * 随机生成平台记录
     */
/*    public void platformRecord(){
        //随机生成平台中奖记录
        List<WinningRecord> winningRecordList = new ArrayList<>();
        List<Jackpot> jackpotList = jackpotService.selectJackpotList(new Jackpot());
        WinningRecord winningRecord = null;
        if(jackpotList.size() <=0 ) return;
        for (int i=0; i<20; i++) {
            winningRecord = new WinningRecord();
            // 随机生成email
            String email = PrivacyUtils.maskEmail(StringUtils.getEmail(8, 12));
            //生成随机中奖数额, 中奖币种
            Jackpot jackpot = randomNum(jackpotList);
            //生成随机中奖时间
            Date beginTime = DateUtils.getStartTime(new Date(), -4);
            Date endTime = DateUtils.getEndTime(new Date(), -1);
            Date date = DateUtils.randomDate(DateUtils.dateTime(beginTime),DateUtils.dateTime(endTime));
            winningRecord.setUsername(email);
            winningRecord.setCoin(jackpot.getCoin());
            winningRecord.setNum(jackpot.getNum());
            winningRecord.setCreateTime(date);
            winningRecordList.add(winningRecord);
        }
        RedisUtils.setCacheList("platformRecord", winningRecordList);

        // 随机生成平台认购记录
        int random = StringUtils.getNum(150, 300); //随机生成20-40条记录
        List<AssetLogVO> assetLogVOList = new ArrayList<>();
        AssetLogVO assetLogVO = null;
        for (int i = 0; i < random; i++) {
            assetLogVO = new AssetLogVO();
            // 生成随机账号
            assetLogVO.setUsername(StringUtils.getEmail(8, 12));
            // 生成认购数额
            BigDecimal num = RandomNumberUtils.randomBigdecimal(500L, 50000L, 6);
            assetLogVO.setAmount(num);
            // 随机生成时间
            Date beginTime = DateUtils.getStartTime(new Date(), -2);
            Date endTime = DateUtils.getEndTime(new Date(), -1);
            Date date = DateUtils.randomDate(DateUtils.dateTime(beginTime),DateUtils.dateTime(endTime));
            assetLogVO.setOperateTime(date);
            assetLogVOList.add(assetLogVO);
        }
        RedisUtils.deleteObject("subscriptionRecord");
        RedisUtils.setCacheList("subscriptionRecord", assetLogVOList);
    }*/

    //随机生成
/*    private Jackpot randomNum(List<Jackpot> jackpotList) {
        int index = (int)(Math.random() * jackpotList.size());
        Jackpot jackpot = jackpotList.get(index);
        // 随机金额
        BigDecimal decimal = null;
        if(jackpot.getCoin().equals(CurrencyEnum.BTC.getCode())) {
            decimal = RandomNumberUtils.randomBigdecimal(0.5, 0.01, 2);
        }  if(jackpot.getCoin().equals(CurrencyEnum.ETH.getCode())) {
            decimal = RandomNumberUtils.randomBigdecimal(30L, 1L, 2);
        }  if(jackpot.getCoin().equals(CurrencyEnum.USDT.getCode())) {
            decimal = RandomNumberUtils.randomBigdecimal(300L, 1L, 2);
        }  if(jackpot.getCoin().equals(CurrencyEnum.DIEM.getCode())) {
            decimal = RandomNumberUtils.randomBigdecimal(3000L, 100L, 2);
        }
        jackpot.setNum(decimal);
        return jackpot;
    }*/

    /**
     * 随机生成认购进程
     */
    public void process() {
        // 释放总额
        BigDecimal releaseCountNum = RedisUtils.getCacheObject("releaseCountNum") == null ? BigDecimal.ZERO : RedisUtils.getCacheObject("releaseCountNum");
        // 已认购总额
        BigDecimal countNum = RedisUtils.getCacheObject("countNum") == null ? BigDecimal.ZERO : RedisUtils.getCacheObject("countNum");
        if(releaseCountNum.equals(releaseCountNum)) {
            releaseCountNum = new BigDecimal(56789);
        }
        // 释放金额：默认生成100~300之间
        Long releaseNum  = getRandom(100, 300);
        // 认购总额
        Long num = getRandom(500, 900);

        releaseCountNum = releaseCountNum.add(new BigDecimal(releaseNum));
        countNum = countNum.add(new BigDecimal(num));
        RedisUtils.deleteObject("releaseCountNum");
        RedisUtils.deleteObject("countNum");

        RedisUtils.setCacheObject("releaseCountNum", releaseCountNum);
        RedisUtils.setCacheObject("countNum", countNum);
    }

    private Long getRandom(int min, int max) {
        Random random = new Random();
        //生成64-128内的随机数
        int s = random.nextInt(max)%(max-min+1) + min;
        return Long.valueOf(s);
    }

    /**
     * 获取 usd 价格
     */
    public void getUsdPrice() {
       /* try {
            UsdResultInfo info = UsdPriceUtil.makeAPICall();
            if(info != null && info.getPrice() != null) {
                log.info("当前USDT/USD的汇率为：{}", info.getPrice());
//                RedisUtils.setCacheObject("usdPrice", info.getPrice());
                RedisUtils.setCacheObject("usdPrice", 1);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        RedisUtils.setCacheObject("usdPrice", new BigDecimal(1));
    }



    /**
     * 发送系统邮件
     */
    public void sendSystemEmail() throws InterruptedException {
        log.info("==============开始推送系统邮件=======================");
       List<String> emailList = bigoUserService.getRechargeUser();

        
            if(emailList == null || emailList.size() ==0) {
                return;
            }
            List<EmailConfig> emailConfigList = emailConfigRepository.selectStatusList();
            if(emailConfigList == null || emailConfigList.size() <= 0) {
                log.info("==========当前系统邮箱暂未配置=============");
                return;
            }
           /* int randomNum = (int)(Math.random()*emailConfigList.size());
            EmailConfig conf = emailConfigList.get(randomNum);


            String sendKey = "sendAccount_"+conf.getEmail();
            Object sendCountStr =  redisTemplate.opsForValue().get(sendKey);
            if(sendCountStr == null) sendCountStr = 0;
            Long sendCount = (Long) sendCountStr;
           if(sendCount > 499) {
               conf = getRandomEmail(emailConfigList);
           }
*/
            for (int i=0; i<emailConfigList.size();i++) {
                try {
                    String email = emailList.get(i);
                    if(StringUtils.isEmpty(email)) continue;
    
                    EmailConfig conf = emailConfigList.get(i);
                    Long sendCount = conf.getSendCount()+1L;
                    if(sendCount >=1001) {
                        continue;
                    }
                    String userName = conf.getEmail();
                    String password = conf.getPassword();
                    String sendName = conf.getSendName();
    
                    Properties javaMailProperties = new Properties();
                    javaMailProperties.put("mail.smtp.auth", "true");
                    javaMailProperties.put("mail.smtp.starttls.enable", "true");
                    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                    mailSender.setHost("smtp.gmail.com");
                    mailSender.setPort(587);
                    mailSender.setUsername(userName);
                    mailSender.setPassword(password);
                    mailSender.setJavaMailProperties(javaMailProperties);
    
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
    
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                    String templateName = "template3.ftl";
    
    
    
                    log.info("=========发送邮件：{},===接收邮件：{}==============", userName, email);
                    //获取模板信息
                    Template template = configurer.getConfiguration().getTemplate(templateName);
                    String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, null);
                    helper.setSentDate(DateUtils.getNowDate());
                    helper.setFrom(properties.getUsername(), sendName);
                    helper.setTo(email);
                    helper.setSubject(sendName);
                    //注意此处，sendText是个重载函数，也可以不带后面这个参数，为true时表示发送html格式的邮件，为false是为文本模式的邮件，默认为false，也就是文本模式的邮件，最开始就是没注意这一点儿导致发送出去的html格式的邮件全都显示不出来
                    helper.setText(text, true);
                    mailSender.send(mimeMessage);
    
                    bigoUserService.updateSendEmailStatus(email);
                    //发送成功，增加发送次数
                    emailConfigRepository.updateEmailCount(conf.getId());
//                SEND_COUNT += SEND_COUNT;
//                //增加账号发送次数
//                redisTemplate.delete(sendKey);
//                redisTemplate.opsForValue().set(sendKey, sendCount+1L, 86400, TimeUnit.SECONDS);
                log.info("==============推送系统邮件成功===用户账号：{}", email);
                } catch (Exception e) {
                    log.error("发送邮箱验证码异常，异常信息{}", e.getMessage(), e);
                }
            }
       

    }

    private EmailConfig getRandomEmail(List<EmailConfig> emailConfigList) {

        int randomNum = (int)(Math.random()*emailConfigList.size());
        EmailConfig conf = emailConfigList.get(randomNum);
        String userName = conf.getEmail();

        String sendKey = "sendAccount_"+userName;
        String sendCountStr = (String) redisTemplate.opsForValue().get(sendKey);
        if(sendCountStr == null) sendCountStr = "0";
        Long sendCount = Long.valueOf(sendCountStr);
        if(sendCount > 501) {
            conf = getRandomEmail(emailConfigList);
        }
        return conf;

    }

}
