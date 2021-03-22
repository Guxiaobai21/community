package com.yingteng.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect //方面组件
public class AlphaAspect {

    @Pointcut("execution(* com.yingteng.community.service.*.*(..))")
    public void pointcut(){

    }

    //  五种通知

    //  在切点之前织入代码
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    //  在切点之后织入代码
    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    //  在切点有返回值以后织入代码
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    //  在切点抛异常的时候织入代码
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    //  在切点前后都织入代码
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{ //joinPoint代表连接点（切点）,置入的部位
        System.out.println("around before");
        Object obj = joinPoint.proceed();        //  调用目标组件的返回值
        System.out.println("around after");
        return obj;
    }
}
