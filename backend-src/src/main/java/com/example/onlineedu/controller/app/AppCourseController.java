package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.LearningProgressDTO;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.ChapterProgressVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.domain.vo.MyLearningVO;
import com.example.onlineedu.service.CourseEnrollmentService;
import com.example.onlineedu.service.CourseService;
import com.example.onlineedu.service.LearningProgressService;
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
 * 用户端课程控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程浏览")
@RestController
@RequestMapping("/api/app/course")
public class AppCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseEnrollmentService enrollmentService;

    @Autowired
    private LearningProgressService progressService;

    @ApiOperation("分页查询课程（支持搜索和排序）")
    @GetMapping("/page")
    public Result<Page<CourseVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("关键词") @RequestParam(required = false) String keyword,
            @ApiParam("排序方式：不传=综合(质量优先), hot=热度榜, time=最新发布") @RequestParam(required = false) String orderBy) {
        Page<CourseVO> result = courseService.pageForApp(page, size, categoryId, keyword, orderBy);
        return Result.success(result);
    }

    @ApiOperation("查询课程详情")
    @GetMapping("/detail/{id}")
    public Result<CourseVO> getDetail(@PathVariable Long id) {
        // 查询详情
        CourseVO vo = courseService.getDetail(id);
        // 增加浏览数
        courseService.increaseViewCount(id);
        return Result.success(vo);
    }

    @ApiOperation("报名课程")
    @PostMapping("/enroll/{courseId}")
    public Result<Void> enroll(@PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        enrollmentService.enroll(currentUser.getUserId(), courseId);
        return Result.success();
    }

    @ApiOperation("获取我的学习课程列表")
    @GetMapping("/my-learning")
    public Result<Page<MyLearningVO>> getMyLearning(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<MyLearningVO> result = enrollmentService.getMyLearningCourses(
                currentUser.getUserId(), page, size);
        return Result.success(result);
    }

    @ApiOperation("获取最近学习的一门课程")
    @GetMapping("/recent-learning")
    public Result<MyLearningVO> getRecentLearning() {
        LoginUser currentUser = UserContext.getUser();
        MyLearningVO vo = enrollmentService.getRecentLearningCourse(currentUser.getUserId());
        return Result.success(vo);
    }

    @ApiOperation("保存学习进度")
    @PostMapping("/progress")
    public Result<Void> saveProgress(@Valid @RequestBody LearningProgressDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        progressService.saveProgress(currentUser.getUserId(), dto);
        return Result.success();
    }

    @ApiOperation("获取章节学习进度")
    @GetMapping("/progress/{courseId}/{chapterId}")
    public Result<ChapterProgressVO> getChapterProgress(
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        LoginUser currentUser = UserContext.getUser();
        ChapterProgressVO vo = progressService.getProgress(
                currentUser.getUserId(), courseId, chapterId);
        return Result.success(vo);
    }

    @ApiOperation("获取课程所有章节的学习进度")
    @GetMapping("/progress/{courseId}")
    public Result<List<ChapterProgressVO>> getCourseProgress(@PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        List<ChapterProgressVO> progressList = progressService.getCourseProgress(
                currentUser.getUserId(), courseId);
        return Result.success(progressList);
    }

}
