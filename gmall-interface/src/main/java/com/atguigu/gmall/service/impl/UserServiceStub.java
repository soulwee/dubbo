package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserService;

import java.util.List;

public class UserServiceStub implements UserService {


    public final UserService userService;

    public UserServiceStub(UserService userService) {
        this.userService = userService;
    }

    public List<UserAddress> getUserAddressList(String userId) {
        System.out.println("-----------UserServiceStub--------------");
        List<UserAddress> userAddressList = null;
        if (userId != null && !"".equals(userId)) {
            userAddressList = userService.getUserAddressList(userId);
        }
        return userAddressList;
    }
}
