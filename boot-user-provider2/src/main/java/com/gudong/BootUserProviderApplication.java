package com.gudong;

import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.gudong.config.DynamicClassGenerator;
import com.gudong.config.DynamicProxyFactory;
import com.gudong.config.InterfaceScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Set;

/**
 * 1、导入依赖；
 * 		1）、导入dubbo-starter
 * 		2）、导入dubbo的其他依赖
 * @author gudong
 *
 * SpringBoot与dubbo整合的三种方式：
 * 1）、导入dubbo-starter，在application.properties配置属性，使用@Service【暴露服务】使用@Reference【引用服务】
 * 2）、保留dubbo xml配置文件;
 * 		导入dubbo-starter，使用@ImportResource导入dubbo的配置文件即可
 * 3）、使用注解API的方式：
 * 		将每一个组件手动创建到容器中,让dubbo来扫描其他的组件
 */

//@EnableDubbo //开启基于注解的dubbo功能
//@ImportResource(locations="classpath:provider.xml")


@EnableDubbo(scanBasePackages={"com.gudong","org.apache.dubbo.samples.generic.call"})
@SpringBootApplication
public class BootUserProviderApplication implements ApplicationRunner{

	public static void main(String[] args) {
		SpringApplication.run(BootUserProviderApplication.class, args);
	}
	@Autowired
	private ApplicationContext context;


	@Override
	public void run(ApplicationArguments args) throws Exception {
		Set<Class<?>> interfaces = InterfaceScanner.scanInterfaces("org.apache.dubbo.samples.generic.call");
		for (Class<?> anInterface : interfaces) {
			Object proxy = DynamicProxyFactory.createProxy(anInterface);
			// 可以将代理对象保存起来或直接使用，例如打印信息或进行其他操作。
			// 3. 注册Bean定义到Spring容器
			//registerServiceBean(registry, interfaceClass, proxy);
			System.out.println("Created proxy for: " + anInterface.getName());
		}
		// 扫描接口
//		Set<Class<?>> interfaces = InterfaceScanner.scan("org.apache.dubbo.samples.generic.call");
//		System.out.println(interfaces);
		// 生成并注册服务
		/*for (Class<?> interfaceClass : interfaces) {
			Class<?> implClass = DynamicClassGenerator.generateImplClass(interfaceClass);
			Object serviceImpl = implClass.getDeclaredConstructor().newInstance();

			ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
			serviceConfig.setInterface(interfaceClass);
			serviceConfig.setRef(serviceImpl);
			serviceConfig.setRegistry(new RegistryConfig("zookeeper://localhost:2181"));
			serviceConfig.export();
		}*/
	}
}
