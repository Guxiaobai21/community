package com.yingteng.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮箱客户端工具类
 */
@Component
public class MailClient {

    //声明日志Logger对象
    private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

    //注入JavaMailSender对象
    @Autowired
    private JavaMailSender mailSender;

    //@Value(${配置的参数})注入mail配置的username
    @Value("${spring.mail.username}")
    private String from;

    //发送方法sendMail
    public void sendMail(String to, String subject, String content){
        try {
            //创建MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            //创建helper，传入message，构建内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            //通过helper设置发件人、收件人、主题、内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            //mailSender 发送构建的内容
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            LOGGER.error("发送邮件失败："+e.getMessage());
        }
    }

}
