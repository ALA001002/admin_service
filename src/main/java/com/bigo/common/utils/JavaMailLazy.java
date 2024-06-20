package com.bigo.common.utils;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.parameters.P;

import java.util.Properties;

/**
 * @description:
 * @author: wenxm
 * @date: 2021/8/22 18:42
 */
public class JavaMailLazy {

    private static volatile JavaMailSenderImpl javaMailSender;

    private JavaMailLazy() {

    }

    public static JavaMailSenderImpl getInstance() {
        try {
            if (null == javaMailSender) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (JavaMailSenderImpl.class) {
                    if(null == javaMailSender) {
                        javaMailSender = new JavaMailSenderImpl();
                        javaMailSender.setHost("smtp.gmail.com");
                        Properties props = new Properties();
                        props.put("mail.smtp.auth","true");
                        props.put("mail.smtp.starttls.enable","true");
                        props.put("mail.smtp.EnableSSL.enable","true");
                        props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                        props.setProperty("mail.smtp.socketFactory.fallback","false");
                        props.setProperty("mail.smtp.port","465");
                        javaMailSender.setJavaMailProperties(props);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return javaMailSender;
    }
}
