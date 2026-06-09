# 聊天功能测试报告

## 测试环境
- 服务地址：http://localhost:8080
- API 端点：POST /api/chat
- 模型：DeepSeek (deepseek-v4-flash)

---

## 1. 单轮测试

### 请求
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "sessionId": "test001",
  "message": "这是服务端课程，你是谁？"
}
```

### 预期结果
- HTTP 状态码：200
- 返回格式：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "question": "这是服务端课程，你是谁？",
    "answer": "（DeepSeek 的中文回答）"
  }
}
```

---

## 2. 多轮测试

### 第一轮（建立上下文）
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "sessionId": "test001",
  "message": "这是服务端课程，你是谁？"
}
```

### 第二轮（验证上下文记忆）
```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{
  "sessionId": "test001",
  "message": "之前和你聊过什么？根据聊天历史，这是什么课程？"
}
```

### 预期结果
- HTTP 状态码：200
- 模型能结合上一轮"服务端课程"的语境继续回答
- 表现出连续上下文理解能力

---

## Postman 测试步骤

1. 打开 Postman
2. 创建新请求
3. 设置方法为 POST
4. 输入 URL：`http://localhost:8080/api/chat`
5. Headers 添加：`Content-Type: application/json`
6. Body 选择 raw，输入上述 JSON
7. 点击 Send
8. 截图保存结果

---

## 截图要求

请截取以下截图：
1. **单轮聊天成功截图**（第一轮请求和响应）
2. **相同 sessionId 连续两轮聊天成功截图**（第二轮请求和响应，展示上下文记忆）