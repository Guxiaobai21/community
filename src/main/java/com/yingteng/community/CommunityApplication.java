package com.yingteng.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class CommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}
	/*
	运行后，执行的操作：
	1、启动Tomcat容器
	2、自动创建Spring容器（不需要自己手动创建）
	3、自动进行组件扫描(扫描配置类所在的包和子包下的bean，配置类就是当前这个Application类)
	被扫描的包需要加上注解，如：controller、Service、Repository、Configuration（内部都包含Component，其实都是由Component实现的）
	*/

	//controller（处理请求）->service（处理业务）->Dao（访问数据库）
}
