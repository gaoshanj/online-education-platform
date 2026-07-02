package com.example.onlineedu.service;

import com.example.onlineedu.exception.QwenApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通义千问API客户端
 *
 * @author feng
 */
@Slf4j
@Service
public class QwenClient {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String apiUrl;

    @Value("${qwen.api.model:qwen-turbo}")
    private String model;

    @Value("${qwen.api.timeout:30000}")
    private long timeout;

    @Value("${qwen.api.max-tokens:1024}")
    private int maxTokens;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private OkHttpClient httpClient;

    /**
     * 初始化HTTP客户端
     */
    private OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                    .readTimeout(timeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
        }
        return httpClient;
    }

    /**
     * 调用千问API进行对话
     *
     * @param messages 消息列表
     * @return AI回复内容
     */
    public String chat(List<Map<String, String>> messages) {
        try {
            // 构造请求体（OpenAI兼容格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("max_completion_tokens", maxTokens);

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("千问API请求: {}", jsonBody);

            // 构造HTTP请求
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            // 发送请求
            try (Response response = getHttpClient().newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "未知错误";
                    log.error("千问API调用失败，状态码: {}, 错误信息: {}", response.code(), errorBody);
                    throw new QwenApiException("千问API调用失败: " + errorBody);
                }

                String responseBody = response.body().string();
                log.info("千问API响应: {}", responseBody);

                // 解析响应（OpenAI兼容格式）
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                // 检查是否有错误
                if (rootNode.has("error")) {
                    JsonNode errorNode = rootNode.get("error");
                    String errorMessage = errorNode.path("message").asText("未知错误");
                    log.error("千问API返回错误: {}", errorMessage);
                    throw new QwenApiException("千问API错误: " + errorMessage);
                }

                // 提取AI回复内容（OpenAI格式）
                JsonNode choicesNode = rootNode.path("choices");
                
                if (choicesNode.isArray() && choicesNode.size() > 0) {
                    JsonNode firstChoice = choicesNode.get(0);
                    JsonNode messageNode = firstChoice.path("message");
                    String content = messageNode.path("content").asText();
                    
                    if (content == null || content.isEmpty()) {
                        throw new QwenApiException("千问API返回内容为空");
                    }
                    
                    return content;
                } else {
                    throw new QwenApiException("千问API响应格式异常：未找到choices节点");
                }
            }
        } catch (IOException e) {
            log.error("千问API调用异常", e);
            throw new QwenApiException("千问API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 生成会话标题
     *
     * @param userQuestion 用户问题
     * @return 会话标题
     */
    public String generateTitle(String userQuestion) {
        try {
            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", "你是一个标题生成助手，请根据用户的问题生成一个简洁的标题，不超过10个字。只返回标题内容，不要有任何其他说明。"),
                    Map.of("role", "user", "content", "请为这个问题生成标题：" + userQuestion)
            );
            
            String title = chat(messages);
            // 去除可能的引号和多余空格
            title = title.replaceAll("^\"|\"$", "").trim();
            
            // 限制长度
            if (title.length() > 20) {
                title = title.substring(0, 20);
            }
            
            return title;
        } catch (Exception e) {
            log.warn("生成标题失败，使用默认标题", e);
            // 如果生成失败，返回问题的前10个字作为标题
            return userQuestion.length() > 10 ? userQuestion.substring(0, 10) + "..." : userQuestion;
        }
    }
}
