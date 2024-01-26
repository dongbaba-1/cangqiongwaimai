package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Now;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //在切面类里写重复逻辑的代码

    @Pointcut("@annotation(com.sky.annotation.AutoFill)")//抽取公共的切点表达式
    public void pt(){};

    @Before("pt()") //注入公共切点表达式,下面是要给加入注解的函数执行的公共函数（公共字段填充）
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始公共字段自动填充....");
        //获取当前被拦截方法的OperationType（UPDATE or INSERT）
            //获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            //获取该目标方法上的注解
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);//获取
            //获取注解的value INSERT or UPDATE
        OperationType operationType = autoFill.value();


        //获取当前被拦截方法的参数（实体对象）
        Object[] args = joinPoint.getArgs(); //args[0]为拿到的实体对象
        //准备赋值的数据


        //根据当前不同的操作类型，为对应的属于通过反射来赋值
        if(operationType == OperationType.INSERT){
          Method setCreateUser = args[0].getClass().getMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
          Method setUpdateUser = args[0].getClass().getMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
          Method setCreateTime = args[0].getClass().getMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
          Method setUpdateTime = args[0].getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
          setCreateUser.invoke(args[0],BaseContext.getCurrentId());
          setUpdateUser.invoke(args[0],BaseContext.getCurrentId());
          setCreateTime.invoke(args[0],LocalDateTime.now());
          setUpdateTime.invoke(args[0],LocalDateTime.now());
        } else if (operationType == OperationType.UPDATE) {
            Method setUpdateUser = args[0].getClass().getMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
            Method setUpdateTime = args[0].getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            setUpdateUser.invoke(args[0],BaseContext.getCurrentId());
            setUpdateTime.invoke(args[0],LocalDateTime.now());
        }

        //Object[] args = joinPoint.getArgs();//获取目标方法运行参数


    }






}
