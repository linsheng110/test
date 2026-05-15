package com.stu.helloserve.service;

import com.stu.helloserve.common.Result;
import com.stu.helloserve.dto.UserDTO;
import com.stu.helloserve.entity.UserInfo;
import com.stu.helloserve.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
    Result<UserDetailVO> getUserDetail(Long userId);
    Result<String> updateUserInfo(UserInfo userInfo);
    Result<String> deleteUser(Long userId);
}
