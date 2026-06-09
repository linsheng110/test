package com.stu.helloserve.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stu.helloserve.model.dto.Chat.ChatRequestDTO;
import com.stu.helloserve.model.entity.ChatRecord;
import com.stu.helloserve.model.vo.ChatResponseVO;
import com.stu.helloserve.service.ChatHistoryService;
import com.stu.helloserve.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ChatHistoryService chatHistoryService;
    private final StringRedisTemplate stringRedisTemplate;

    // 上下文轮数限制（最近3轮）
    private static final int CONTEXT_ROUNDS = 3;
    // Redis key 前缀
    private static final String CHAT_SESSION_KEY = "chat:session:";
    // 会话过期时间（7天）
    private static final long SESSION_EXPIRE_DAYS = 7;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Autowired
    public ChatServiceImpl(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           ChatHistoryService chatHistoryService,
                           StringRedisTemplate stringRedisTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.chatHistoryService = chatHistoryService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        String message = requestDTO.getMessage();

        log.info("接收到消息 - sessionId: {}, message: {}", sessionId, message);

        // sessionId 为空校验
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("sessionId 不能为空");
        }

        String redisKey = CHAT_SESSION_KEY + sessionId;

        // 1. 读取历史消息（从 ChatHistoryService 读取，支持 Redis/内存降级）
        List<ChatRecord> history = chatHistoryService.getChatHistory(sessionId);

        // 2. 调用模型
        String url = baseUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json;charset=UTF-8");

        // 构建消息列表（使用 messages 格式）
        List<Map<String, String>> messages = buildMessages(history, message);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String answer;
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, request, byte[].class);
            String responseBody = new String(response.getBody(), java.nio.charset.StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(responseBody);
            answer = root.get("choices").get(0).get("message").get("content").asText();
            log.info("模型回答: {}", answer);
        } catch (Exception e) {
            throw new RuntimeException("调用模型失败: " + e.getMessage(), e);
        }

        // 3. 保存本轮记录到 ChatHistoryService（支持 Redis/内存降级）
        chatHistoryService.saveChatRecord(sessionId, message, answer);

        // 4. 尝试保存到 Redis（如果可用）
        try {
            String recordText = "用户：" + message + "\n助手：" + answer;
            stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

            // 只保留最近 3 轮
            Long size = stringRedisTemplate.opsForList().size(redisKey);
            if (size != null && size > CONTEXT_ROUNDS) {
                stringRedisTemplate.opsForList().trim(redisKey, size - CONTEXT_ROUNDS, size - 1);
            }

            // 设置过期时间
            stringRedisTemplate.expire(redisKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis 不可用，跳过 Redis 缓存: {}", e.getMessage());
        }

        return new ChatResponseVO(message, answer);
    }

    /**
     * 构建消息列表（包含系统提示词 + 历史上下文 + 当前问题）
     */
    private List<Map<String, String>> buildMessages(List<ChatRecord> history, String currentMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 1. 添加系统提示词
        messages.add(Map.of("role", "system", "content", "你是一名专业、友好、简洁的中文智能助手，请结合历史上下文回答用户问题。"));

        // 2. 添加历史上下文（只取最近若干轮）
        int startIndex = Math.max(0, history.size() - CONTEXT_ROUNDS);
        List<ChatRecord> recentHistory = history.subList(startIndex, history.size());

        for (ChatRecord record : recentHistory) {
            // 用户问题
            messages.add(Map.of("role", "user", "content", record.getUserMessage()));
            // 助手回答
            messages.add(Map.of("role", "assistant", "content", record.getAssistantMessage()));
        }

        // 3. 添加当前问题
        messages.add(Map.of("role", "user", "content", currentMessage));

        return messages;
    }
}