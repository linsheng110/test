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
    /**
     * 根据 id 查询用户
     * @param id 用户ID
     * @return 结果
     */
    Result<String> getUserById(Long id);

    /**
     * 获取核心用户分页列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 结果
     */
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
}