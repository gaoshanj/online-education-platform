package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.AnswerDTO;
import com.example.onlineedu.domain.dto.QuestionDTO;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.AnswerVO;
import com.example.onlineedu.domain.vo.QuestionDetailVO;
import com.example.onlineedu.domain.vo.QuestionVO;
import com.example.onlineedu.service.AnswerService;
import com.example.onlineedu.service.QuestionService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户端问答控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-互动问答")
@RestController
@RequestMapping("/api/app/question")
public class AppQuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    // ===== 问题 =====

    @ApiOperation("提问")
    @PostMapping("/ask")
    public Result<Void> askQuestion(@Valid @RequestBody QuestionDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        questionService.askQuestion(currentUser.getUserId(), dto);
        return Result.success();
    }

    @ApiOperation("更新问题（仅提问者）")
    @PutMapping("/update")
    public Result<Void> updateQuestion(@Valid @RequestBody QuestionDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        questionService.updateQuestion(currentUser.getUserId(), dto);
        return Result.success();
    }

    @ApiOperation("删除问题（仅提问者）")
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        questionService.deleteQuestion(currentUser.getUserId(), id);
        return Result.success();
    }

    @ApiOperation("查询课程问题列表（支持 onlyMine 过滤）")
    @GetMapping("/course/{courseId}")
    public Result<Page<QuestionVO>> getQuestionsByCourse(
            @PathVariable Long courseId,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("是否只看自己的问题") @RequestParam(defaultValue = "false") Boolean onlyMine) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        Page<QuestionVO> result = questionService.pageQuestionsByCourse(courseId, page, size, onlyMine, userId);
        return Result.success(result);
    }

    @ApiOperation("查询问题详情")
    @GetMapping("/detail/{id}")
    public Result<QuestionDetailVO> getQuestionDetail(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        QuestionDetailVO vo = questionService.getQuestionDetail(id, userId);
        return Result.success(vo);
    }

    // ===== 回答与评论 =====

    @ApiOperation("提交回答或评论（answerId 为 null/0 时为回答，>0 时为评论）")
    @PostMapping("/answer")
    public Result<Void> submitAnswer(@Valid @RequestBody AnswerDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        answerService.submitAnswer(currentUser.getUserId(), dto, false);
        return Result.success();
    }

    @ApiOperation("删除回答或评论（仅本人）")
    @DeleteMapping("/answer/{id}")
    public Result<Void> deleteAnswer(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        answerService.deleteAnswer(currentUser.getUserId(), id);
        return Result.success();
    }

    @ApiOperation("获取某回答下的二级评论列表")
    @GetMapping("/answer/replies/{answerId}")
    public Result<List<AnswerVO>> getAnswerReplies(@PathVariable Long answerId) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        List<AnswerVO> replies = answerService.getAnswerReplies(answerId, userId);
        return Result.success(replies);
    }

    @ApiOperation("点赞或取消点赞回答/评论")
    @PostMapping("/answer/like/{id}")
    public Result<Boolean> toggleLike(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        boolean liked = answerService.toggleLike(currentUser.getUserId(), id);
        return Result.success(liked);
    }
}
