package com.stu.helloserve.service;
import com.stu.helloserve.common.Result;
import com.stu.helloserve.common.ResultCode;
import com.stu.helloserve.dto.UserDTO;
import com.stu.helloserve.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    // 模拟数据库：Key为用户名，Value为密码
    private static final Map<String, String> userMap = new HashMap<>();

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 校验用户名是否已存在
        if (userMap.containsKey(userDTO.getUsername())) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 存入模拟数据库
        userMap.put(userDTO.getUsername(), userDTO.getPassword());

        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 校验用户是否存在
        if (!userMap.containsKey(userDTO.getUsername())) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验密码是否正确
        String dbPassword = userMap.get(userDTO.getUsername());
        if (!dbPassword.equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        return Result.success("登录成功");
    }
}