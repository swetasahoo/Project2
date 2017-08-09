package com.stackroute.activitystream.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserLogger {


    
	 long start;
	 Object proceed;

     long executionTime;
    
@Before("execution(public * getAllUser())")
    public void logStartTime() throws Throwable {
        start= System.currentTimeMillis();
        // proceed = joinPoint.proceed();      
         System.out.println("Start time:"+start);   
    }

@After("execution(public * getAllUser())")
public Object logEndTime() throws Throwable {
	executionTime = System.currentTimeMillis() - start;
    System.out.println(" executed in " + executionTime + "ms");
    return proceed;

}


}
