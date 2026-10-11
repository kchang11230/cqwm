package com.sky.aspect;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.reflect.MethodSignature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

//切面类 实现公共字段自动填充
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")//mapper下所有的类所有方法 同时满足方法上加了AutoFill注解
    public void autoFillPointCut(){

    }

    @Before("autoFillPointCut()")//前置通知，在通知中进行公共字段赋值(在目标方法执行前运行)
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //获取当前被拦截的方法sql语句的操作类型
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill= signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解
        OperationType operationType=autoFill.value();// 获得操作类型

        //获取到当前被拦截方法的实体对象
        Object[] args=joinPoint.getArgs();//获得所有参数
        if(args==null||args.length==0){
            return;
        }
        Object entity=args[0];//得到第一个参数--实体对象

        //准备赋值数据
        LocalDateTime now= LocalDateTime.now();
        Long currentId= BaseContext.getCurrentId();

        //根据当前操作类型，给对应的属性通过反射来赋值
        if(operationType==OperationType.INSERT){
            Method setCreateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            Method setCreateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
            Method setUpdateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

            //反射
            setCreateTime.invoke(entity,now);
            setCreateUser.invoke(entity,currentId);
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }else if(operationType==OperationType.UPDATE){
            Method setUpdateTime= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
            Method setUpdateUser= entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);

            //反射
            setUpdateTime.invoke(entity,now);
            setUpdateUser.invoke(entity,currentId);
        }
    }
}
