package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 课程监管控制器
 * 管理员负责：全平台课程查看、审核通过/拒绝、强制下架、删除
 * 课程创建/编辑/提交审核由讲师端 /api/teacher/course 负责
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-课程管理")
@RestController
@RequestMapping("/api/admin/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("分页查询全平台课程（可按讲师/分类/状态/是否精品/排序过滤）")
    @GetMapping("/page")
    @RequireRole({UserRole.ADMIN})
    public Result<Page<CourseVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程名称") @RequestParam(required = false) String courseName,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("状态（0草稿/1待审核/2已发布/3已拒绝/4已下架）") @RequestParam(required = false) Integer status,
            @ApiParam("是否精品：1=精品/0=普通") @RequestParam(required = false) Integer isRecommend,
            @ApiParam("排序：hot=热度, time=最新, 不传=综合质量") @RequestParam(required = false) String orderBy) {
        Page<CourseVO> result = courseService.page(page, size, courseName, categoryId, status, isRecommend, orderBy);
        return Result.success(result);
    }

    @ApiOperation("查询课程详情")
    @GetMapping("/detail/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<CourseVO> getDetail(@PathVariable Long id) {
        CourseVO vo = courseService.getDetail(id);
        return Result.success(vo);
    }

    @ApiOperation("审核通过（待审核 → 已发布）")
    @PutMapping("/approve/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> approve(@PathVariable Long id) {
        courseService.approve(id);
        return Result.success();
    }

    @ApiOperation("审核拒绝（待审核 → 审核拒绝）")
    @PutMapping("/reject/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> reject(
            @PathVariable Long id,
            @ApiParam("拒绝原因") @RequestParam String rejectReason) {
        courseService.reject(id, rejectReason);
        return Result.success();
    }

    @ApiOperation("强制下架违规课程")
    @PutMapping("/offline/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> forceOffline(@PathVariable Long id) {
        courseService.forceOffline(id);
        return Result.success();
    }

    @ApiOperation("删除课程（逻辑删除）")
    @DeleteMapping("/delete/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return Result.success();
    }

    @ApiOperation("设置/取消为精品课程")
    @PutMapping("/recommend/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> setRecommend(
            @PathVariable Long id,
            @ApiParam("是否精品：1=精品；0=取消/普通") @RequestParam Integer isRecommend) {
        courseService.setRecommend(id, isRecommend);
        return Result.success();
    }

}
