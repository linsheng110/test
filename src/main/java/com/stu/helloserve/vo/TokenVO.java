package com.stu.helloserve.vo;

import lombok.Data;

@Data
public class TokenVO {
    private String token;
    private Long userId;
    private String username;

    public TokenVO(String token, Long userId, String username) {
        this.token = token;
        this.userId = userId;
        this.username = username;
    }
}
