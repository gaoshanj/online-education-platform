package com.example.onlineedu.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.TeacherStudentDetailVO;
import com.example.onlineedu.domain.vo.TeacherStudentListVO;
import com.example.onlineedu.service.TeacherStudentService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 讲师端 - 学员管理控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-我的学员")
@RestController
@RequestMapping("/api/teacher/students")
public class TeacherStudentController {

    @Autowired
    private TeacherStudentService teacherStudentService;

    /**
     * 1. 学员列表（用户维度）
     * GET /api/teacher/students
     */
    @ApiOperation("讲师端查询学员列表")
    @GetMapping
    @RequireRole({UserRole.TEACHER})
    public Result<Page<TeacherStudentListVO>> listStudents(
            @ApiParam("昵称模糊搜索") @RequestParam(required = false) String nickname,
            @ApiParam("最近学习时间-开始（yyyy-MM-dd HH:mm:ss）")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime learnStart,
            @ApiParam("最近学习时间-结束（yyyy-MM-dd HH:mm:ss）")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime learnEnd,
            @ApiParam("排序：latestLearn/courseCount/duration/completionRate")
            @RequestParam(defaultValue = "latestLearn") String sortBy,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<TeacherStudentListVO> result = teacherStudentService.listStudents(
                currentUser.getUserId(), nickname, learnStart, learnEnd, sortBy, page, size);
        return Result.success(result);
    }

    /**
     * 2. 学员详情（课程维度）
     * GET /api/teacher/students/{userId}
     */
    @ApiOperation("讲师端查询学员学习详情（课程维度）")
    @GetMapping("/{userId}")
    @RequireRole({UserRole.TEACHER})
    public Result<Page<TeacherStudentDetailVO>> listStudentCourses(
            @ApiParam("学员用户ID") @PathVariable Long userId,
            @ApiParam("课程名称模糊搜索") @RequestParam(required = false) String courseName,
            @ApiParam("完成状态：0-未完成，1-已完成（不传则查全部）") @RequestParam(required = false) Integer isCompleted,
            @ApiParam("排序：lastLearnTime/completionRate/duration/enrollTime")
            @RequestParam(defaultValue = "lastLearnTime") String sortBy,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<TeacherStudentDetailVO> result = teacherStudentService.listStudentCourses(
                currentUser.getUserId(), userId, courseName, isCompleted, sortBy, page, size);
        return Result.success(result);
    }
}
