package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
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

import java.util.List;

/**
 * 管理端 - 问答内容审核控制器
 * 管理员负责：内容审核（查看/删除违规问题和回答）
 * 讲师回答学生问题由讲师端 /api/teacher/question 负责
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-问答内容管理")
@RestController
@RequestMapping("/api/admin/question")
public class AdminQuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @ApiOperation("分页查询全平台问题（内容审核）")
    @GetMapping("/page")
    @RequireRole({ UserRole.ADMIN })
    public Result<Page<QuestionVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("是否有讲师回答") @RequestParam(required = false) Integer hasTeacherAnswer) {
        Page<QuestionVO> result = questionService.pageAllQuestions(page, size, courseId, hasTeacherAnswer);
        return Result.success(result);
    }

    @ApiOperation("查询问题详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<QuestionDetailVO> getDetail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        QuestionDetailVO vo = questionService.getQuestionDetail(id, userId);
        return Result.success(vo);
    }

    @ApiOperation("获取某回答下的二级评论列表")
    @GetMapping("/answer/replies/{answerId}")
    public Result<List<AnswerVO>> getAnswerReplies(@PathVariable Long answerId) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        List<AnswerVO> replies = answerService.getAnswerReplies(answerId, userId);
        return Result.success(replies);
    }

    @ApiOperation("删除违规问题")
    @DeleteMapping("/delete/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestionByAdmin(id);
        return Result.success();
    }

    @ApiOperation("删除违规回答或评论")
    @DeleteMapping("/answer/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswerByAdmin(id);
        return Result.success();
    }
}
