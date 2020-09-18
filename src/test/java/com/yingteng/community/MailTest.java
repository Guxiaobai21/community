package com.yingteng.community;


import com.yingteng.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        //发送邮件
        mailClient.sendMail("guguangfu@qq.com","TEST JavaMailSender", "comgratulations on you send email success!");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "goodfood");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        //发送邮件
        mailClient.sendMail("guguangfu@qq.com","TEST JavaMailSender", content);
    }
}
