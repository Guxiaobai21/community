package com.yingteng.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary//添加primary注释，可以设置bean在Spring容器中的优先级。
public class AlphaDaoMybatisImpl implements AlphaDao{

    @Override
    public String select() {
        return "Mybatis";
    }
}
