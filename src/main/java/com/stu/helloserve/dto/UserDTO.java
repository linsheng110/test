package com.stu.helloserve.dto;

/**
 * 用户数据传输对象 (DTO)
 * 专门用于接收前端传递的 JSON 数据
 */
public class UserDTO {

    private String username;

    private String password;

    // Getter 和 Setter 方法

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}