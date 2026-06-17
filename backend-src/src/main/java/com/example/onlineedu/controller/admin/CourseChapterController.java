package com.example.onlineedu.controller.admin;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseChapterTreeVO;
import com.example.onlineedu.service.CourseChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 课程章节只读控制器
 * 管理员仅可查看课程章节结构，不参与章节增删改
 * 章节的增删改由讲师端 /api/teacher/chapter 负责
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-章节查看")
@RestController
@RequestMapping("/api/admin/chapter")
public class CourseChapterController {

    @Autowired
    private CourseChapterService chapterService;

    @ApiOperation("查询课程章节树（管理员只读）")
    @GetMapping("/tree/{courseId}")
    @RequireRole({ UserRole.ADMIN })
    public Result<List<CourseChapterTreeVO>> getChapterTree(@PathVariable Long courseId) {
        List<CourseChapterTreeVO> tree = chapterService.getChapterTree(courseId);
        return Result.success(tree);
    }
}
