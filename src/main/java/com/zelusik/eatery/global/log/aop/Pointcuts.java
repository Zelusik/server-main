package com.zelusik.eatery.global.log.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.zelusik.eatery.app.controller..*(..))")
    public void controllerPoint(){}

    @Pointcut("execution(* com.zelusik.eatery.app.service..*(..))")
    public void servicePoint(){}

    @Pointcut("execution(* com.zelusik.eatery.app.repository..*(..))")
    public void repositoryPoint(){}
}
