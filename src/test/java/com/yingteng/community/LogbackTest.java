package com.yingteng.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LogbackTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void testLogback(){
        System.out.println(LOGGER.getName());
        LOGGER.warn("测试警告");//内容将在日志配置的指定路径中的指定文件内显示
    }
}
