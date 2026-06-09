package com.stu.helloserve.service;

import com.stu.helloserve.model.dto.Chat.ChatRequestDTO;
import com.stu.helloserve.model.vo.ChatResponseVO;

public interface ChatService {
    /**
     * 聊天
     * @param requestDTO 聊天请求DTO
     * @return 聊天响应VO
     */
    ChatResponseVO chat(ChatRequestDTO requestDTO);
}