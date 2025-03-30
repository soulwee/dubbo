package com.gudong.config;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author kalman03
 * @since 2021-11-20
 */
@Activate(group = "provider")
public class GatewayConsumerFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
/*		for (Map.Entry<String, Object> entry : RpcThreadContext.getContextMap().entrySet()) {
			RpcContext.getContext().setAttachment(entry.getKey(), entry.getValue());
		}*/
		return invoker.invoke(invocation);
	}

}
