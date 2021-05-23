package com.gudong.service.impl;

import java.util.Arrays;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Component;

//权重设置 @Service(weight = 2)，也可以在admin管理页面设置倍权和半权
@Service //暴露服务 注意是dubbo下的注解，如果用xml文件配置就不用再暴露了
@Component
public class UserServiceImpl implements UserService {

	@Override
	public List<UserAddress> getUserAddressList(String userId) {
		System.out.println("UserServiceImpl.....old...");

		UserAddress address1 = new UserAddress(1, "北京市昌平区宏福科技园综合楼3层", "1", "李老师", "010-56253825", "Y");
		UserAddress address2 = new UserAddress(2, "深圳市宝安区西部硅谷大厦B座3层（深圳分校）", "1", "王老师", "010-56253825", "N");

		return Arrays.asList(address1,address2);
	}

}
