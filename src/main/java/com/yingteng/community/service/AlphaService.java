package com.yingteng.community.service;

import com.yingteng.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//在Spring容器中，bean默认是单例的，只会创建一次，但是把注释添加后，在创建对象时将不是单例，每次创建，内存地址会改变。一般很少用这种注释
@Scope("prototype")
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct//注释用于初始化方法，会在构造器之后执行。
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy//Destory方法，对象销毁前调用。
    public void destory() {
        System.out.println("销毁AlphaService");
    }

    public String find(){
        return alphaDao.select();
    }
}
