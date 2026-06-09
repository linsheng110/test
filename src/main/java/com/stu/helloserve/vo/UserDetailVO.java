package com.stu.helloserve.vo;

import lombok.Data;

@Data
public class UserDetailVO {
    private Long userId;
    private String username;
    private String realname;
    private String phone;
    private String address;
}
