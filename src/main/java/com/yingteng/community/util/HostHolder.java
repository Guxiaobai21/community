package com.yingteng.community.util;

import com.yingteng.community.dao.AlphaDao;
import com.yingteng.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于代替session对象。
 */
@Component
public class HostHolder {

    //实现了线程隔离，存值
    private ThreadLocal<User> users = new ThreadLocal<>();


    public void setUser(User user){
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
