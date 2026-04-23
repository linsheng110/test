package com.stu.helloserve.controller;

import com.stu.helloserve.common.Result;
import com.stu.helloserve.dto.UserDTO;
import com.stu.helloserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 新增用户（注册）
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 2. 用户登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 3. 根据 id 查询用户
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // 4. 分页查询核心用户列表
    @GetMapping("/page")
    public Result<Object> getUserPage(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }
}