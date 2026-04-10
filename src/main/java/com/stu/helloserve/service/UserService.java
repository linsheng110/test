package com.stu.helloserve.service;
import com.stu.helloserve.common.Result;
import com.stu.helloserve.dto.UserDTO;

public interface UserService {
    /**
     * 用户注册
     * @param userDTO 用户信息
     * @return 操作结果
     */
    Result<String> register(UserDTO userDTO);

    /**
     * 用户登录
     * @param userDTO 用户信息
     * @return 操作结果
     */
    Result<String> login(UserDTO userDTO);
}