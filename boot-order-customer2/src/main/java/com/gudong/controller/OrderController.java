package com.gudong.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.OrderService;

@Controller
public class OrderController {

	@Autowired
	OrderService orderService;


	//http://localhost:8081/initOrder?uid=1
	@ResponseBody
	@RequestMapping("/initOrder")
	public List<UserAddress> initOrder(@RequestParam("uid")String userId) {
		return orderService.initOrder(userId);
	}

}
