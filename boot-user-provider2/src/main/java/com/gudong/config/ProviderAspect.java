package com.gudong.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.atguigu.gmall.bean.UserAddress;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Aspect
@Component
public class ProviderAspect {

    // 定义切入点：拦截Dubbo服务接口的实现类方法
    @Pointcut("execution(* com.gudong.service.impl.*Impl.*(..))")
    public void dubboServicePointcut() {}

    // 前置通知
    @Before("dubboServicePointcut()")
    public void beforeInvoke(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("[AOP] 方法调用前: " + methodName);
    }
    @Resource
    private ApplicationConfig applicationConfig;
    // 环绕通知（示例：记录方法耗时）
    @Around("dubboServicePointcut()")
    public Object aroundInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标对象
        Object target = joinPoint.getTarget();

        // 获取目标对象实现的所有接口
        Class<?>[] interfaces = target.getClass().getInterfaces();

        // 遍历接口并输出包路径
        Arrays.stream(interfaces).forEach(intf -> {
            String packageName = Optional.ofNullable(intf.getPackage())
                    .map(Package::getName)
                    .orElse("默认包或无包");
            System.out.println("接口 " + intf.getName() + " 的包路径: " + packageName);
        });
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        System.out.println(Arrays.toString(args));
        long start = System.currentTimeMillis();
        try {
//提供者被消费 去掉其他服务
          /*  ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("generic-call-consumer");
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("zookeeper://127.0.0.1:2181");
            applicationConfig.setRegistry(registryConfig);
            */
            ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
            referenceConfig.setInterface("org.apache.dubbo.samples.generic.call.api.HelloService");
            referenceConfig.setApplication(applicationConfig);
            referenceConfig.setGeneric("true");
//            referenceConfig.setAsync(true);
            referenceConfig.setTimeout(7000);
            GenericService genericService = ReferenceConfigCache.getCache().get(referenceConfig);

//            genericService = referenceConfig.get();
            return  invokeSayHello(genericService);




//            return joinPoint.proceed();
        } finally {
            long cost = System.currentTimeMillis() - start;
            System.out.println("[AOP] 方法执行耗时: " + cost + "ms");
        }
    }

    /*dubbo%3A%2F%2F192.168.2.4%3A20882%2Forg.apache.dubbo.samples.generic.call.api.HelloService%3Fanyhost%3Dtrue%26application%3Dgmall-user2%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dorg.apache.dubbo.samples.generic.call.api.HelloService%26methods%3DsayHello%2CsayHelloAsync%2CsayHelloAsyncGenericComplex%2CsayHelloAsyncComplex%26pid%3D27344%26side%3Dprovider%26timestamp%3D1743302418309*/

    public static Object invokeSayHello( GenericService genericService ) throws InterruptedException {
        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
        System.err.println("invokeSayHello(return): " + result);
        UserAddress address2 = new UserAddress(2, result+"深圳市宝安区西部硅谷大厦B座3层（深圳分校）", "1", "王老师", "010-56253825", "N");
		/*try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		/*if (Math.random() > 0.5) {
			throw new RuntimeException("Exception to show hystrix enabled.");
		}*/
        return Arrays.asList(address2);
      /*  CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            System.err.println("invokeSayHello(whenComplete): " + value);
            latch.countDown();
        });

        System.err.println("invokeSayHello(return): " + result);
        latch.await();*/
    }
}
