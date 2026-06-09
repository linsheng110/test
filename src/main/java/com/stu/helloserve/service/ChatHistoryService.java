package com.stu.helloserve.service;

import com.stu.helloserve.model.entity.ChatRecord;

import java.util.List;

public interface ChatHistoryService {
    /**
     * 保存聊天记录
     * @param sessionId 会话ID
     * @param userMessage 用户消息
     * @param assistantMessage 助手回复
     */
    void saveChatRecord(String sessionId, String userMessage, String assistantMessage);

    /**
     * 获取会话的聊天记录
     * @param sessionId 会话ID
     * @return 聊天记录列表
     */
    List<ChatRecord> getChatHistory(String sessionId);

    /**
     * 清除会话历史
     * @param sessionId 会话ID
     */
    void clearChatHistory(String sessionId);
}