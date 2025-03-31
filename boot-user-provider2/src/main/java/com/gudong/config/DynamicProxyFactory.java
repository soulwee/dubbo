package com.gudong.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

public class DynamicProxyFactory {
    public static <T> T createProxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //调用inner服务
                System.out.println("Method " + method.getName() + " called on " + interfaceClass.getName());
                return null; // 实现逻辑可以根据需要添加
            }
        });
    }
}
