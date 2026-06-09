package com.stu.helloserve.service.impl;

import com.stu.helloserve.model.entity.ChatRecord;
import com.stu.helloserve.service.ChatHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ChatHistoryServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;

    // 内存缓存（Redis 不可用时的降级方案）
    private final Map<String, List<ChatRecord>> memoryCache = new ConcurrentHashMap<>();

    // Redis key 前缀
    private static final String CHAT_SESSION_KEY = "chat:session:";
    // 会话过期时间（7天）
    private static final long SESSION_EXPIRE_DAYS = 7;

    @Autowired
    public ChatHistoryServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveChatRecord(String sessionId, String userMessage, String assistantMessage) {
        try {
            String key = CHAT_SESSION_KEY + sessionId;

            ChatRecord record = new ChatRecord();
            record.setSessionId(sessionId);
            record.setUserMessage(userMessage);
            record.setAssistantMessage(assistantMessage);
            record.setCreateTime(new Date());

            // 尝试保存到 Redis
            redisTemplate.opsForList().rightPush(key, record);
            redisTemplate.expire(key, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis 不可用，使用内存缓存: {}", e.getMessage());
            // 降级到内存缓存
            memoryCache.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(createRecord(sessionId, userMessage, assistantMessage));
        }
    }

    @Override
    public List<ChatRecord> getChatHistory(String sessionId) {
        try {
            String key = CHAT_SESSION_KEY + sessionId;

            List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
            List<ChatRecord> records = new ArrayList<>();

            if (objects != null) {
                for (Object obj : objects) {
                    if (obj instanceof ChatRecord) {
                        records.add((ChatRecord) obj);
                    }
                }
            }

            return records;
        } catch (Exception e) {
            log.warn("Redis 不可用，使用内存缓存: {}", e.getMessage());
            // 降级到内存缓存
            return memoryCache.getOrDefault(sessionId, new ArrayList<>());
        }
    }

    @Override
    public void clearChatHistory(String sessionId) {
        try {
            String key = CHAT_SESSION_KEY + sessionId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis 不可用，清除内存缓存: {}", e.getMessage());
            memoryCache.remove(sessionId);
        }
    }

    private ChatRecord createRecord(String sessionId, String userMessage, String assistantMessage) {
        ChatRecord record = new ChatRecord();
        record.setSessionId(sessionId);
        record.setUserMessage(userMessage);
        record.setAssistantMessage(assistantMessage);
        record.setCreateTime(new Date());
        return record;
    }
}