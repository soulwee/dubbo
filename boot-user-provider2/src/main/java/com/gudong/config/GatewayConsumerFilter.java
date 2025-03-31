package com.gudong.config;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import org.apache.dubbo.samples.generic.call.api.HelloService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kalman03
 * @since 2021-11-20
 */
@Activate(group = "provider")
@Component
public class GatewayConsumerFilter implements Filter {
    @Resource
	private ApplicationConfig applicationConfig;

    @Resource
	RegistryConfig registryConfig;
	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
/*		for (Map.Entry<String, Object> entry : RpcThreadContext.getContextMap().entrySet()) {
			RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue());
		}*/
		System.out.println(invoker);
		System.out.println(invoker.getUrl().toString());
		System.out.println(invocation);
		System.out.println(invocation.getAttachment("interface"));
		String argument = invocation.getArguments()[0].toString();
		System.out.println(argument);
		System.out.println(applicationConfig);
		if(argument.equals("1")){
			//怎么获取要消费的服务的url invoker集合
			ReferenceConfig<HelloService> referenceConfig = new ReferenceConfig<>();
			referenceConfig.setInterface("org.apache.dubbo.samples.generic.call.api.HelloService");
			//todo 这里是null 有没有影响，为什么？ 测试是否影响，以及api提供者上下线先后消费，是否需要重启，怎么做到不重启
//			referenceConfig.setApplication(new ApplicationConfig());
//			referenceConfig.setRegistry(new RegistryConfig("127.0.0.1:2181"));
//			referenceConfig.setGeneric("tru97e");
//            referenceConfig.setAsync(true);
//			referenceConfig.setTimeout(7000);
			HelloService genericService = ReferenceConfigCache.getCache().get(referenceConfig);

//            genericService = referenceConfig.get();
			try {
				return  new RpcResult(genericService.sayHello("df"));
			} catch (Exception e) {
				e.printStackTrace();
				/*<dubbo:reference uniqueServiceName="org.apache.dubbo.samples.generic.call.api.HelloService" interface="org.apache.dubbo.samples.generic.call.api.HelloService" generic="false" timeout="7000" id="org.apache.dubbo.samples.generic.call.api.HelloService" />*/
				throw new RuntimeException(e);
			}
		}
		else{

			Result invoke = invoker.invoke(invocation);
			return invoke;
		}

	}

}
