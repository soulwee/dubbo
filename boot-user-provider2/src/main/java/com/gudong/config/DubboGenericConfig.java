package com.gudong.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.alibaba.dubbo.rpc.service.GenericService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.function.Supplier;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_TYPE;

@Configuration
public class DubboGenericConfig implements BeanDefinitionRegistryPostProcessor {

    // ZooKeeper注册中心地址（从配置读取）
    @Value("${dubbo.registry.address}")
    private String zkAddress;

    @Autowired
    RegistryConfig registryConfig;
    // 要扫描的包路径
    private static final String SCAN_PACKAGE = "org.apache.dubbo.samples.generic.call";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
       /* // 1. 扫描指定包下的接口
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Class.class));

        Set<BeanDefinition> candidates = scanner.findCandidateComponents(SCAN_PACKAGE);

        // 2. 为每个接口创建动态代理并注册Bean
        candidates.forEach(beanDefinition -> {
            String className = beanDefinition.getBeanClassName();
            Class<?> interfaceClass = ClassUtils.resolveClassName(className, null);

            // 创建动态代理（实现GenericService）
            Object proxy = createGenericProxy(interfaceClass);

            // 3. 注册Bean定义到Spring容器
            registerServiceBean(registry, interfaceClass, proxy);
        });*/

        Set<Class<?>> interfaces = InterfaceScanner.scanInterfaces("com.atguigu.gmall.service");
//        Set<Class<?>> interfaces = InterfaceScanner.scanInterfaces("org.apache.dubbo.samples.generic.call");
        for (Class<?> anInterface : interfaces) {
            //动态代理方式
/*

            Object proxy = DynamicProxyFactory.createProxy(anInterface);
            // 可以将代理对象保存起来或直接使用，例如打印信息或进行其他操作。

            // 3. 注册Bean定义到Spring容器
            registerServiceBean(registry, anInterface, proxy);
            System.out.println("Created proxy for: " + anInterface.getName());
*/


            //字节码方式

            try {
                // 生成动态实现类
                Class<?> proxyClass = generateProxyClass(anInterface);
                Object a = proxyClass.getDeclaredConstructor().newInstance();
                // 注册Bean定义
                registerServiceBean(registry, anInterface, a);
                registerBeanDefinition(registry, anInterface, proxyClass);
                /*
                Class<?> implClass = DynamicClassGenerator.generateImplClass(anInterface);
                Object serviceImpl = implClass.getDeclaredConstructor().newInstance();
                ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
                serviceConfig.setInterface(anInterface);
                serviceConfig.setRef(serviceImpl);
                serviceConfig.setRegistry(new RegistryConfig("zookeeper://localhost:2181"));
                serviceConfig.export();*/
            } catch ( Exception e) {
                e.printStackTrace();
            }


        }
    }
    /**
     * 使用Byte Buddy生成代理类
     */
    private Class<?> generateProxyClass(Class<?> interfaceClass) {
        return new ByteBuddy()
                .subclass(Object.class)
                .implement(interfaceClass)
                .defineField("abc", interfaceClass, Modifier.PRIVATE)
                .annotateField(AnnotationDescription.Builder.ofType(Reference.class).build())
                .method(ElementMatchers.isDeclaredBy(interfaceClass))
                .intercept(MethodDelegation.toField("abc"))
                .make()
                .load(interfaceClass.getClassLoader())
                .getLoaded();
    }
    private Object createGenericProxy(Class<?> interfaceClass) {
        return Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class[]{interfaceClass, GenericService.class},
            (proxy, method, args) -> {
                // 方法拦截逻辑
                System.out.println("拦截方法调用: " + method.getName());
                // 这里添加自定义业务逻辑
                return null; // 实际返回结果
            }
        );
    }
    /**
     * 注册Bean到Spring容器
     */
    private void registerBeanDefinition(BeanDefinitionRegistry registry,
                                        Class<?> interfaceClass,
                                        Class<?> proxyClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(proxyClass)
                .setAutowireMode( AUTOWIRE_BY_TYPE);
//todo com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor
        registry.registerBeanDefinition(
                interfaceClass.getSimpleName() + "Impl3",
                builder.getBeanDefinition()
        );
    }
    public   void registerServiceBean(BeanDefinitionRegistry registry,
                                   Class<?> interfaceClass,
                                   Object proxyInstance) {
        // 创建Dubbo ServiceConfig
        ServiceConfig<Object> service = new ServiceConfig<>();
        service.setInterface(interfaceClass);
        service.setRef(proxyInstance);
        System.out.println(registryConfig);
        service.setRegistry(new RegistryConfig(zkAddress));
        service.setGeneric("true"); // 开启泛化调用

        /*dubbo%3A%2F%2F192.168.2.4%3A20881%2Forg.apache.dubbo.samples.generic.call.HelloService%3Fanyhost%3Dtrue%26application%3Dgmall-user%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dorg.apache.dubbo.samples.generic.call.HelloService%26methods%3DsayHello%2CsayHelloAsync%26pid%3D14816%26side%3Dprovider%26timestamp%3D1743419735029*/
        // 暴露服务
        service.export();

        // 注册Spring Bean
      /*  BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .genericBeanDefinition(ServiceBean.class, () -> {
                ServiceBean<Object> serviceBean = new ServiceBean<>();

                serviceBean.setInterface(interfaceClass);
                serviceBean.setRef(proxyInstance);
                serviceBean.setRegistry(new RegistryConfig(zkAddress));
                serviceBean.setGeneric("true");
                return serviceBean;
            });
        registry.registerBeanDefinition(interfaceClass.getSimpleName() + "Impl", builder.getBeanDefinition());
   */

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // 不需要额外
    }
/*
    // Dubbo配置
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig config = new ApplicationConfig();
        config.setName("generic-provider");
        return config;
    }*/

}
