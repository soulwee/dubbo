package org.apache.dubbo.samples.generic.call;


import com.alibaba.dubbo.common.utils.PojoUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.dubbo.samples.generic.call.api.GenericType;
import org.apache.dubbo.samples.generic.call.api.Person;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HelloServiceIT {
    private static GenericService genericService;
    private static String zookeeperHost = System.getProperty("zookeeper.address", "127.0.0.1");

    @BeforeClass
    public static void setUp() throws Exception {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("generic-call-consumer");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://" + zookeeperHost + ":2181");
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterface("org.apache.dubbo.samples.generic.call.api.HelloService");
        applicationConfig.setRegistry(registryConfig);
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setGeneric("true");
        referenceConfig.setAsync(true);
        referenceConfig.setTimeout(7000);
        genericService = referenceConfig.get();
    }

    @Test
    public void invokeSayHello() throws InterruptedException {
        Object result = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            System.err.println("invokeSayHello(whenComplete): " + value);
            Object o = PojoUtils.realize(value, String.class);
            Assert.assertEquals("sayHello: world", o);
            latch.countDown();
        });

        System.err.println("invokeSayHello(return): " + result);
        Assert.assertNull(result);

        latch.await(7000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void invokeSayHelloAsync() throws InterruptedException {
        Object result = genericService.$invoke("sayHelloAsync", new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        CompletableFuture<String> future = RpcContext.getContext().getCompletableFuture();
        future.whenComplete((value, t) -> {
            System.err.println("invokeSayHelloAsync(whenComplete): " + value);
            Object o = PojoUtils.realize(value, String.class);
            Assert.assertEquals("sayHelloAsync: world", o);
            latch.countDown();
        });

        System.err.println("invokeSayHelloAsync(return): " + result);
        Assert.assertNull(result);

        latch.await(7000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void invokeAsyncSayHelloAsync() throws Exception {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsync",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            System.err.println("invokeAsyncSayHelloAsync(whenComplete): " + value);
            Object o = PojoUtils.realize(value, String.class);
            Assert.assertEquals("sayHelloAsync: world", o);
            latch.countDown();
        });

        latch.await(7000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void invokeAsyncSayHello() throws Exception {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHello",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);
        future.whenComplete((value, t) -> {
            System.err.println("invokeAsyncSayHello(whenComplete): " + value);
            Object o = PojoUtils.realize(value, String.class);
            Assert.assertEquals("sayHello: world", o);
            latch.countDown();
        });

        latch.await(7000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void asyncInvokeSayHelloAsyncComplex() throws Exception {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsyncComplex",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            System.err.println("asyncInvokeSayHelloAsyncComplex(whenComplete): " + value);
            Person p = (Person) PojoUtils.realize(value, Person.class);
            Assert.assertEquals("sayHelloAsyncComplex: world", p.getName());
            latch.countDown();
        });

        latch.await(7000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void asyncInvokeSayHelloAsyncGenericComplex() throws Exception {
        CompletableFuture<Object> future = genericService.$invokeAsync("sayHelloAsyncGenericComplex",
                new String[]{"java.lang.String"}, new Object[]{"world"});
        CountDownLatch latch = new CountDownLatch(1);

        future.whenComplete((value, t) -> {
            System.err.println("asyncInvokeSayHelloAsyncGenericComplex(whenComplete): " + value);
            GenericType g = (GenericType) PojoUtils.realize(value, GenericType.class);
            Assert.assertTrue(g.getType() instanceof Person);
            Person p = (Person)g.getType();
            Assert.assertEquals("sayHelloAsyncGenericComplex: world", p.getName());
            latch.countDown();
        });

        latch.await(7000, TimeUnit.MILLISECONDS);
    }
}
