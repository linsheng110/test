package com.stu.helloserve.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ChatRecord {
    /**
     * 会话编号
     */
    private String sessionId;

    /**
     * 用户问题
     */
    private String userMessage;

    /**
     * 大模型回答
     */
    private String assistantMessage;

    /**
     * 记录时间
     */
    private Date createTime;
}