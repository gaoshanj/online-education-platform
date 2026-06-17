package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.ChatMessageDTO;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.ChatResponseVO;
import com.example.onlineedu.domain.vo.ChatSessionDetailVO;
import com.example.onlineedu.domain.vo.ChatSessionVO;
import com.example.onlineedu.service.AiChatService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * AI聊天控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-AI智能问答")
@RestController
@RequestMapping("/api/app/chat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @ApiOperation("发送消息（异步处理，立即返回 sessionId）")
    @PostMapping("/message")
    public Result<Long> sendMessage(@Valid @RequestBody ChatMessageDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        Long sessionId = aiChatService.sendMessage(currentUser.getUserId(), dto);
        return Result.success(sessionId);
    }

    @ApiOperation("轮询获取AI回复（AI回复完成前返回 null）")
    @GetMapping("/result/{sessionId}")
    public Result<ChatResponseVO> getLatestReply(@PathVariable Long sessionId) {
        ChatResponseVO response = aiChatService.getLatestReply(sessionId);
        return Result.success(response);
    }

    @ApiOperation("获取会话列表（分页）")
    @GetMapping("/sessions")
    public Result<Page<ChatSessionVO>> listSessions(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<ChatSessionVO> result = aiChatService.listSessions(currentUser.getUserId(), page, size);
        return Result.success(result);
    }

    @ApiOperation("获取会话详情")
    @GetMapping("/session/{id}")
    public Result<ChatSessionDetailVO> getSessionDetail(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        ChatSessionDetailVO detail = aiChatService.getSessionDetail(currentUser.getUserId(), id);
        return Result.success(detail);
    }

    @ApiOperation("删除会话")
    @DeleteMapping("/session/{id}")
    public Result<Void> deleteSession(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        aiChatService.deleteSession(currentUser.getUserId(), id);
        return Result.success();
    }
}
