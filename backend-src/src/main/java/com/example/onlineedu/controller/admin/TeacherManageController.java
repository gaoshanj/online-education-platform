package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.domain.vo.TeacherVO;
import com.example.onlineedu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 讲师管理控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-讲师管理")
@RestController
@RequestMapping("/api/admin/teacher")
public class TeacherManageController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("分页查询讲师")
    @GetMapping("/page")
    @RequireRole({ UserRole.ADMIN })
    public Result<Page<TeacherVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("讲师名称") @RequestParam(required = false) String name) {
        Page<TeacherVO> result = teacherService.pageTeachers(page, size, name);
        return Result.success(result);
    }

    @ApiOperation("查询讲师详情")
    @GetMapping("/detail/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<TeacherVO> getDetail(@PathVariable Long id) {
        TeacherVO vo = teacherService.getTeacherDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("查询讲师的所有课程")
    @GetMapping("/courses/{id}")
    @RequireRole({ UserRole.ADMIN })
    public Result<List<CourseVO>> getCourses(@PathVariable Long id) {
        List<CourseVO> courses = teacherService.getTeacherCourses(id);
        return Result.success(courses);
    }

    @ApiOperation("设置讲师身份")
    @PutMapping("/set-role/{userId}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> setTeacherRole(@PathVariable Long userId) {
        teacherService.setTeacherRole(userId);
        return Result.success();
    }

    @ApiOperation("取消讲师身份")
    @PutMapping("/remove-role/{userId}")
    @RequireRole({ UserRole.ADMIN })
    public Result<Void> removeTeacherRole(@PathVariable Long userId) {
        teacherService.removeTeacherRole(userId);
        return Result.success();
    }
}
