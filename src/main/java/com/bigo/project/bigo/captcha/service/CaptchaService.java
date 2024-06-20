package com.bigo.project.bigo.captcha.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigo.common.utils.DateUtils;
import com.bigo.common.utils.ServletUtils;
import com.bigo.common.utils.StringUtils;
import com.bigo.common.utils.http.HttpClientUtil;
import com.bigo.common.utils.ip.IpUtils;
import com.bigo.project.bigo.captcha.domain.Captcha;
import com.bigo.project.bigo.captcha.domain.MsgResult;
import com.bigo.project.bigo.captcha.enums.EmailConfEnum;
import com.bigo.project.bigo.captcha.enums.EmailConfEnum2;
import com.bigo.project.bigo.captcha.mapper.CaptchaMapper;
import com.bigo.project.bigo.config.dao.EmailConfigRepository;
import com.bigo.project.bigo.config.jpaEntity.EmailConfig;
import com.google.common.collect.Maps;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description: 手机验证码工具类
 * @author: wenxm
 * @date: 2020/6/28 21:07
 */
@Slf4j
@Component
public class CaptchaService {

    @Value("${msg.appkey}")
    private String appkey;

    @Value("${msg.secretkey}")
    private String secretkey;

    @Value("${msg.sendUrl}")
    private String sendUrl;

    @Value("${msg.queryUrl}")
    private String queryUrl;

    @Value("${msg.regTemplate}")
    private String regTemplate;

//    @Value("${msg.sendName}")
//    private String sendName;

    @Autowired
    private CaptchaMapper captchaMapper;

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties properties;


    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private EmailConfigRepository emailConfigRepository;

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public  Boolean sendCaptcha(String phone, String code){
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("appkey", appkey);
            map.put("secretkey", secretkey);
            map.put("phone", phone);
            map.put("content", URLEncoder.encode(StringUtils.format(regTemplate, code),"utf-8"));
            String resultJson = null;
            resultJson = HttpClientUtil.post(sendUrl, map);
            log.info("======短信通知结果：{}", resultJson);
            MsgResult result = JSONObject.toJavaObject(JSON.parseObject(resultJson), MsgResult.class);
            if(!result.getCode().equals("0")){
                log.error("手机号{},发送验证码失败,param：{}, 返回值：{}",phone, JSON.toJSONString(map), resultJson);
                return Boolean.FALSE;
            }
            redisTemplate.delete(phone);
            redisTemplate.opsForValue().set(phone, code, 1800, TimeUnit.SECONDS);
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            this.addCaptcha(phone, null, code, ip);
        }catch (Exception ex){
            log.error("手机号{},发送验证码异常，异常信息{}",phone, ex.getMessage(), ex);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public void gmailSender(String toMail, String code, int type) {
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            try {
                String userName = null;
                String password = null;
                String sendName = null;
                String templateName = null;
//                if(type == 1) {
//                    EmailConfEnum conf = EmailConfEnum.randomUser(EmailConfEnum.values());
//                    userName = conf.getUserName();
//                    password = conf.getPassword();
//                    sendName = conf.getSendName();
//                    templateName = "template.ftl";
//                }else {
//                    EmailConfEnum2 conf = EmailConfEnum2.randomUser(EmailConfEnum2.values());
//                    userName = conf.getUserName();
//                    password = conf.getPassword();
//                    sendName = conf.getSendName();
//                    templateName = "template2.ftl";
//                }
                List<EmailConfig> emailConfigList = emailConfigRepository.findAll();
                if(emailConfigList == null || emailConfigList.size() <= 0) {
                    log.info("==========当前系统邮箱暂未配置=============");
                    return;
                }
                int randomNum = (int)(Math.random()*emailConfigList.size());
                EmailConfig conf = emailConfigList.get(randomNum);
                userName = conf.getEmail();
                password = conf.getPassword();
                sendName = conf.getSendName();
                templateName = "template.ftl";

                log.info("系统邮箱{},发送验证码到用户邮箱:{}", userName, toMail);
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
                Map<String, Object> data = new HashMap<>(1);
                data.put("captcha", code);
                data.put("sendName", sendName);
                //获取模板信息
                Template template = configurer.getConfiguration().getTemplate(templateName);
                String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);
                helper.setSentDate(DateUtils.getNowDate());
                helper.setFrom(properties.getUsername(), sendName);
                helper.setTo(toMail);
                helper.setSubject(sendName);
                //注意此处，sendText是个重载函数，也可以不带后面这个参数，为true时表示发送html格式的邮件，为false是为文本模式的邮件，默认为false，也就是文本模式的邮件，最开始就是没注意这一点儿导致发送出去的html格式的邮件全都显示不出来
                helper.setText(text, true);
                mailSender.send(mimeMessage);
                redisTemplate.delete(toMail);
                redisTemplate.opsForValue().set(toMail, code, 1800, TimeUnit.SECONDS);
                this.addCaptcha(null, toMail, code, ip);
            } catch (Exception e) {
                log.error("发送邮箱验证码异常，异常信息{}", e.getMessage(), e);
            }
    }

    /**
     * 插入短信验证码
     * @param phone
     * @param email
     * @param code
     * @return
     */
    public Boolean addCaptcha(String phone, String email, String code, String ip){
        Captcha captcha = new Captcha();
        captcha.setPhone(phone);
        captcha.setEmail(email);
        captcha.setCaptcha(code);
        //设置为15分钟后过期
        captcha.setExpireTime(DateUtils.addMinutes(new Date(), 15));
        captcha.setIp(ip);
        captcha.setStatus(0);
        return captchaMapper.insert(captcha) > 0;
    }

    /**
     * 校验验证码
     * @param phone
     * @param email
     * @param code
     * @return
     */
    public Boolean verifyCaptcha(String phone, String email, String code){
        Captcha captcha = null;
        if(StringUtils.isNotEmpty(phone)){
            captcha = getByPhone(phone);
        }else if(StringUtils.isNotEmpty(email)){
            captcha = getByEmail(email);
        }
        if(captcha != null && captcha.getCaptcha().equals(code)){
            captchaMapper.updateStatus(captcha.getId());
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据电话号码获取验证码
     * @param phone
     * @return
     */
    public Captcha getByPhone(String phone){
        Captcha captcha = new Captcha();
        captcha.setPhone(phone);
        captcha.setStatus(0);
        captcha.setExpireTime(new Date());
        return captchaMapper.getByParam(captcha);
    }

    /**
     * 根据邮箱获取验证码
     * @param email
     * @return
     */
    public Captcha getByEmail(String email){
        Captcha captcha = new Captcha();
        captcha.setEmail(email);
        captcha.setStatus(0);
        captcha.setExpireTime(new Date());
        return captchaMapper.getByParam(captcha);
    }

    /**
     * 生成4位随机数验证码
     * @return
     */
    public static String genCaptcha(){
        String vcode = "";
        for (int i = 0; i < 4; i++) {
            vcode = vcode + (int)(Math.random() * 9);
        }
        return vcode;
    }

}
