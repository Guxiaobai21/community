package com.yingteng.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration//注释含义：表示当前类为配置类。
public class AlphaConfig {

    @Bean//注释含义：被注释的这个方法返回值，将被装配到Spring容器中。
    public SimpleDateFormat simpleDateFormat () {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
