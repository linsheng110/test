package com.stu.helloserve.controller;

import com.stu.helloserve.common.Result;
import com.stu.helloserve.common.ResultCode;
import com.stu.helloserve.model.dto.Chat.ChatRequestDTO;
import com.stu.helloserve.model.vo.ChatResponseVO;
import com.stu.helloserve.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        try {
            ChatResponseVO responseVO = chatService.chat(requestDTO);
            return Result.success(responseVO);
        } catch (IllegalArgumentException e) {
            Result<ChatResponseVO> errorResult = Result.error(ResultCode.PARAM_ERROR);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        } catch (Exception e) {
            e.printStackTrace();
            Result<ChatResponseVO> errorResult = Result.error(ResultCode.SYSTEM_ERROR);
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }
}