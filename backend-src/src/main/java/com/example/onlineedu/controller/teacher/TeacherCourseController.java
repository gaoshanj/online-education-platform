package com.example.onlineedu.controller.teacher;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.CourseDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 讲师端 - 课程管理控制器
 * 讲师对自己课程的全生命周期管理（草稿 → 提交审核 → 发布/拒绝 → 下架）
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-课程管理")
@RestController
@RequestMapping("/api/teacher/course")
public class TeacherCourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("创建草稿课程，返回课程ID（用于跳转章节编辑）")
    @PostMapping("/add")
    @RequireRole({ UserRole.TEACHER })
    public Result<Long> add(@RequestBody @Valid CourseDTO dto) {
        Long courseId = courseService.add(dto);
        return Result.success(courseId);
    }

    @ApiOperation("更新课程（草稿/已拒绝/已下架状态可编辑）")
    @PutMapping("/update")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> update(@RequestBody @Valid CourseDTO dto) {
        courseService.update(dto);
        return Result.success();
    }

    @ApiOperation("提交审核（草稿/已拒绝/已下架 → 待审核）")
    @PutMapping("/submit/{id}")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> submit(@PathVariable Long id) {
        courseService.submitForReview(id);
        return Result.success();
    }

    @ApiOperation("申请下架（已发布 → 已下架）")
    @PutMapping("/offline/{id}")
    @RequireRole({ UserRole.TEACHER })
    public Result<Void> offline(@PathVariable Long id) {
        courseService.offline(id);
        return Result.success();
    }

    @ApiOperation("查询自己的课程列表（分页）")
    @GetMapping("/page")
    @RequireRole({ UserRole.TEACHER })
    public Result<Page<CourseVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程名称") @RequestParam(required = false) String courseName,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("是否精品：1=精品/0=普通") @RequestParam(required = false) Integer isRecommend,
            @ApiParam("排序：hot=热度, time=最新, 不传=综合质量") @RequestParam(required = false) String orderBy) {
        Page<CourseVO> result = courseService.page(page, size, courseName, categoryId, status, isRecommend, orderBy);
        return Result.success(result);
    }

    @ApiOperation("查询课程详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.TEACHER })
    public Result<CourseVO> getDetail(@PathVariable Long id) {
        CourseVO vo = courseService.getDetail(id);
        return Result.success(vo);
    }
}
