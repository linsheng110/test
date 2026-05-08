package com.stu.helloserve.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserve.common.Result;
import com.stu.helloserve.common.ResultCode;
import com.stu.helloserve.dto.UserDTO;
import com.stu.helloserve.entity.User;
import com.stu.helloserve.entity.UserInfo;
import com.stu.helloserve.mapper.UserInfoMapper;
import com.stu.helloserve.mapper.UserMapper;
import com.stu.helloserve.vo.UserDetailVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String CACHE_KEY_PREFIX = "user:detail:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 查询用户名是否已存在（复用 MyBatis-Plus 的单表查询能力）
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 组装实体对象并插入数据库
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 查询用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验密码（沿用原有逻辑）
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        return Result.success("登录成功");
    }

    @Override
    public Result<String> getUserById(Long id) {
        // 直接复用 BaseMapper 提供的 selectById 方法查询
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success(user.getUsername()); // 示例返回用户名，可按需调整返回内容
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 这里可以添加核心用户的查询条件，例如按活跃度、等级等筛选
        // queryWrapper.eq(User::getIsCore, true);
        userMapper.selectPage(page, queryWrapper);
        return Result.success(page);
    }

    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isEmpty()) {
            try {
                UserDetailVO cacheVO = objectMapper.readValue(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删除脏数据，继续查询数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 查询数据库
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 写缓存
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(detail),
                    10,
                    TimeUnit.MINUTES
            );
        } catch (JsonProcessingException e) {
            // 缓存写入失败不影响业务
        }

        return Result.success(detail);
    }

    @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        // 参数校验：userInfo 不能为空，并且 userId 不能为空
        if (userInfo == null || userInfo.getUserId() == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 更新数据库
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userInfo.getUserId());
        int update = userInfoMapper.update(userInfo, queryWrapper);

        if (update > 0) {
            // 删除 Redis 缓存
            String key = CACHE_KEY_PREFIX + userInfo.getUserId();
            redisTemplate.delete(key);
            return Result.success("更新成功");
        }

        return Result.error(ResultCode.USER_NOT_EXIST);
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        // 参数校验
        if (userId == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }

        // 删除用户信息
        userInfoMapper.delete(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));

        // 删除用户
        int delete = userMapper.deleteById(userId);

        if (delete > 0) {
            // 删除 Redis 缓存
            String key = CACHE_KEY_PREFIX + userId;
            redisTemplate.delete(key);
            return Result.success("删除成功");
        }

        return Result.error(ResultCode.USER_NOT_EXIST);
    }
}