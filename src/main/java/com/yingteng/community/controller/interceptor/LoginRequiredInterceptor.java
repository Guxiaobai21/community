package com.yingteng.community.controller.interceptor;

import com.yingteng.community.annotation.LoginRequired;
import com.yingteng.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //handler是拦截的目标
        //判断当前拦截的目标是不是一个方法
        if (handler instanceof HandlerMethod){
            //转成方法类型
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取method方法对象
            Method method = handlerMethod.getMethod();
            //获取指定的method对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //loginRequired不为空表示：当前被注解的方法需要登录
            //通过hostHolder获取用户信息，如果为空，表示没登录
            if (loginRequired != null && hostHolder.getUser() == null){
                //强制重定向到登录
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
