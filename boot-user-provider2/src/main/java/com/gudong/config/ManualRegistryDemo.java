package com.gudong.config;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.registry.NotifyListener;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ManualRegistryDemo {

    public static void main(String[] args) throws Exception {
        // 获取 RegistryFactory 实例
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension("zookeeper");
        // 2. 创建注册中心URL
        URL registryUrl = URL.valueOf("zookeeper://127.0.0.1:2181");
        // 3. 获取Registry实例
        Registry registry = registryFactory.getRegistry(registryUrl);

        // 4. 定义要订阅的服务信息
        URL subscribeUrl = new URL("dubbo", "127.0.0.1", 0)
                .setServiceInterface("org.apache.dubbo.samples.generic.call.api.HelloService") // 服务接口全限定名
//                .addParameter("version", "1.0.0") // 版本号
//                .addParameter("group", "prod");    // 分组
;
        //dubbo%3A%2F%2F192.168.2.4%3A20882%2Forg.apache.dubbo.samples.generic.call.api.HelloService%3Fanyhost%3Dtrue%26application%3Dgeneric-provider%26default.token%3Dtrue%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dorg.apache.dubbo.samples.generic.call.api.HelloService%26methods%3DsayHello%2CsayHelloAsync%2CsayHelloAsyncGenericComplex%2CsayHelloAsyncComplex%26pid%3D35852%26side%3Dprovider%26timestamp%3D1743423995772
        // 5. 创建通知监听器
        NotifyListener listener = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                System.out.println("=== 收到服务列表变更通知 ===");
                urls.forEach(url -> System.out.println("服务URL: " + url));
            }
        };
        // 6. 订阅服务
        registry.subscribe(subscribeUrl, listener);
        // 阻塞主线程保持监听
        new CountDownLatch(1).await();
    }
}
