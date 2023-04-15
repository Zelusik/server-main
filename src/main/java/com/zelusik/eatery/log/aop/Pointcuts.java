package com.zelusik.eatery.log.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.zelusik.eatery.controller..*(..))")
    public void controllerPoint(){}

    @Pointcut("execution(* com.zelusik.eatery.service..*(..))")
    public void servicePoint(){}

    @Pointcut("execution(* com.zelusik.eatery.repository..*(..))")
    public void repositoryPoint(){}
}
