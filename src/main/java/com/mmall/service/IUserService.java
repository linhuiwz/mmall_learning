package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by cancan on 2017/7/22.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

}
