package com.example.onlineedu.controller.app;

import com.example.onlineedu.common.Result;
import com.example.onlineedu.service.QwenClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公开AI聊天控制器（无需登录）
 * 供首页右下角聊天窗口使用
 *
 * @author feng
 */
@Slf4j
@Api(tags = "公开-AI智能问答（无需登录）")
@RestController
@RequestMapping("/api/app/public-chat")
public class PublicChatController {

    @Autowired
    private QwenClient qwenClient;

    @ApiOperation("发送消息并获取AI回复（同步，无需登录）")
    @PostMapping("/message")
    public Result<String> sendMessage(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");
            if (messages == null || messages.isEmpty()) {
                return Result.error("消息列表不能为空");
            }
            String reply = qwenClient.chat(messages);
            return Result.success(reply);
        } catch (Exception e) {
            log.error("公开AI聊天调用失败", e);
            return Result.error("AI服务暂时不可用，请稍后再试");
        }
    }
}
