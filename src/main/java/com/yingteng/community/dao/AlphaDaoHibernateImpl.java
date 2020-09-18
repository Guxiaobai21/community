package com.yingteng.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")//通过自定义bean的类名，用于获取指定的bean
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
