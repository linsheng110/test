package com.stu.helloserve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stu.helloserve.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper 接口
 * 继承 BaseMapper 获得强大的单表增删改查能力
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    // 无需手写基础 SQL，BaseMapper 已经提供了常用的 CRUD 方法
}