尚硅谷dubbo学习笔记

需求：
某个电商系统，订单服务(消费者)需要调用用户服务(生产者)获取某个用户的所有地址

我们现在 需要创建两个服务模块进行测试 
模块	                功能
订单服务web模块	    创建订单等
用户服务service模块	查询用户地址等

测试预期结果：订单服务web模块在A服务器，用户服务模块在B服务器，A可以远程调用B的功能。

用户服务(生产者)启动成功后可以看到zookeeper注册中心出现了一个应用服务提供者
![zookeeper](./img/zookeeper.png "zookeeper")


启动消费者订单服务，调用接口成功
![zookeeper](./img/zk1.png "zookeeper")
