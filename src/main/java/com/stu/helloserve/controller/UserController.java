package com.stu.helloserve.controller;

import com.stu.helloserve.common.Result;
import com.stu.helloserve.dto.UserDTO;
import com.stu.helloserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 新增用户（注册） - 路径为 POST /api/users
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 2. 用户登录 - 路径为 POST /api/users/login
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 3. 获取用户信息（查） - 用于测试拦截器放行
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return Result.success("查询成功，正在返回 ID 为 " + id + " 的用户信息");
    }
}