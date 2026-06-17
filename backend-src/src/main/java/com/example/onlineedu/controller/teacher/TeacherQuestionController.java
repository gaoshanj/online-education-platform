package com.example.onlineedu.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.AnswerDTO;
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

import javax.validation.Valid;
import java.util.List;

/**
 * 讲师端 - 问答管理控制器
 * 讲师查看自己课程下的问题、回答学生问题
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-学生提问")
@RestController
@RequestMapping("/api/teacher/question")
public class TeacherQuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @ApiOperation("分页查询自己课程的问题列表")
    @GetMapping("/page")
    @RequireRole({ UserRole.TEACHER })
    public Result<Page<QuestionVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("是否有讲师回答") @RequestParam(required = false) Integer hasTeacherAnswer) {
        LoginUser currentUser = UserContext.getUser();
        Page<QuestionVO> result = questionService.pageQuestionsByTeacher(
                page, size, courseId, hasTeacherAnswer, currentUser.getUserId());
        return Result.success(result);
    }

    @ApiOperation("查询问题详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.TEACHER })
    public Result<QuestionDetailVO> getDetail(@PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        QuestionDetailVO vo = questionService.getQuestionDetail(id, currentUser.getUserId());
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

    @ApiOperation("讲师回答问题（isTeacher=true）")
    @PostMapping("/answer")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> answerQuestion(@Valid @RequestBody AnswerDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        answerService.submitAnswer(currentUser.getUserId(), dto, true);
        return Result.success();
    }
}
