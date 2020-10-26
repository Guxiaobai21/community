package com.yingteng.community;


import com.yingteng.community.util.SensitiveFillter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFillter sensitiveFillter;


    @Test
    public void testSensitiveFilter(){
        String text = "测试程序是否可以过滤赌博、过滤嫖娼、过滤吸毒，过滤开票等关键字!";
//        text = sensitiveFillter.filter(text);
//        System.out.println(text);

        text = "测试程序是否可以过滤赌☆☆博、过滤嫖☆☆娼、过滤吸☆☆毒，过滤开☆☆票等关键字!";
        text = sensitiveFillter.filter(text);
        System.out.println(text);
    }
}
