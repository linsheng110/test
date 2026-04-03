package com.stu.helloserve.controller;

import com.stu.helloserve.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/login")
    public Result<String> login() {
        return Result.success("登录成功");
    }

    @PostMapping
    public Result<String> addUser() {
        return Result.success("添加用户成功");
    }

    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable Integer id) {
        return Result.success("用户信息：" + id);
    }

    @PutMapping("/{id}")
    public Result<String> updateUser(@PathVariable Integer id) {
        return Result.success("更新用户成功：" + id);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        return Result.success("删除用户成功：" + id);
    }
}