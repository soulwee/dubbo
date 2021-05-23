# 尚硅谷dubbo学习笔记

需求：
某个电商系统，订单服务(消费者)需要调用用户服务(生产者)获取某个用户的所有地址

我们现在 需要创建两个服务模块进行测试 
模块	                功能
订单服务web模块	    创建订单等
用户服务service模块	查询用户地址等

测试预期结果：订单服务web模块在A服务器，用户服务模块在B服务器，A可以远程调用B的功能。

## dubbo-admin是图形化的服务管理页面

用户服务(生产者)启动成功后可以看到zookeeper注册中心出现了一个应用服务提供者
![zookeeper](./img/zookeeper.png "zookeeper")


启动消费者订单服务，调用接口成功
![zookeeper1](./img/zk1.png "zookeeper22")

## dubbo-monitor-simple简单的监控中心

所有服务配置连接监控中心，进行监控统计，监控服务调用信息   
![监控中心](./img/monitor.png "监控中心")

## 整合springboot
配置都可以转移到properties中了，依赖也全整合到com.alibaba.boot.dubbo-spring-boot-starter
新增了两个注解用来替代之前的配置，@Service、@Reference
@Service：用在provider中暴露服务。
@Reference：在customer中引用暴露的服务。

其实和之前的差不多
![监控中心](./img/admin.png "监控中心")
加上之前的监控中心变成了3个应用
![监控中心](./img/admin2.png "监控中心")
消费者调用接口
![消费者](./img/controller.png "消费者")

## 配置原则

![配置](./img/setting.png "优先级")

JVM 启动 -D 参数优先(-Ddubbo.protocol.port=20880)，这样可以使用户在部署和启动时进行参数重写，比如在启动时需改变协议的端口。
XML (和springboot整合过就是application.properties)次之，如果在 XML 中有配置，则 dubbo.properties 中的相应配置项无效。
dubbo.properties 最后，相当于缺省值，只有 XML 没有配置时，dubbo.properties 的相应配置项才会生效，通常用于共享公共配置，比如应用名。

## 启动时检查

Dubbo缺省会在**启动时检查依赖的服务是否可用，不可用时会抛出异常**，阻止Spring初始化完成，以便上线时，能及早发现问题，**默认check="true“**。可以通过check="false”关闭检查，比如，测试时，有些服务不关心，或者出现了循环依赖，必须有一方先启动。另外，如果你的Spring容器是懒加载的，或者通过API编程延迟引用服务，请关闭check，否则服务临时不可用时，会抛出异常，拿到null引用，如果check= "false"”，总是会返回引用，当服务恢复时，能自动连上。

## 超时时间配置规则
由于网络或服务端不可靠，会导致调用出现一种不确定的中间状态（超时）。为了避免超时导致客户端资源（线程）挂起耗尽，必须设置超时时间。
![配置](./img/timeout.png "优先级")

1）精确优先（方法级优先，接口级次之，全局配置再次之)<br/>
2）消费者设置优先(如果级别一样，则消费方优先，提供方次之)

## 重试次数配置

失败自动切换，当出现失败，重试其它服务器，但重试会带来更长延迟。可通过 retries="2" 来设置重试次数(不含第一次)。

```xml
<-- 
    retries= "":重试次数，不包含第一次调用
	幂等（设置重试次数）【查询、删除、修改】、非幂等（不能设置重试次数）【新增】
    如果新增方法有延迟会导致数据被添加多次，而删改查方法是针对某个id的，不会被重试影响
-->
<dubbo:service  retries="2" />  或 
<dubbo:reference  retries="2" />  或  
<dubbo:reference>    
    <dubbo:method name="findFoo"  retries="2" />  
</dubbo:reference>  

```

## 多版本号

当一个接口实现，出现不兼容升级时，可以用版本号过渡，版本号不同的服务相互间不引用。
可以按照以下的步骤进行版本迁移：
在低压力时间段，先升级一半提供者为新版本
再将所有消费者升级为新版本
然后将剩下的一半提供者升级为新版本

```xml
老版本服务提供者配置：  
<dubbo:service interface="com.foo.BarService"  version="1.0.0" />   
新版本服务提供者配置： 
<dubbo:service  interface="com.foo.BarService" version="2.0.0" />   
老版本服务消费者配置：  
<dubbo:reference  id="barService" interface="com.foo.BarService"  version="1.0.0" />     
新版本服务消费者配置：  
<dubbo:reference  id="barService" interface="com.foo.BarService"  version="2.0.0" />     
如果不需要区分版本，可以按照以下的方式配置：  
<dubbo:reference  id="barService" interface="com.foo.BarService"  version="*" />  
```
当改成*号时，两个版本会随机被调用，这样可以实现灰度发布