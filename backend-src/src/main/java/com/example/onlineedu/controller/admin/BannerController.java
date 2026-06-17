package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.BannerDTO;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.BannerVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.BannerService;
import com.example.onlineedu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 轮播图管理控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-轮播图管理")
@RestController
@RequestMapping("/api/admin/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CourseService courseService;

    @ApiOperation("分页查询轮播图列表")
    @GetMapping("/page")
    @RequireRole({UserRole.ADMIN})
    public Result<Page<BannerVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(bannerService.pageBanners(page, size));
    }

    @ApiOperation("新增轮播图")
    @PostMapping
    @RequireRole({UserRole.ADMIN})
    public Result<Void> add(@Validated @RequestBody BannerDTO dto) {
        bannerService.addBanner(dto);
        return Result.success();
    }

    @ApiOperation("修改轮播图")
    @PutMapping("/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> update(
            @ApiParam("轮播图ID") @PathVariable Long id,
            @Validated @RequestBody BannerDTO dto) {
        bannerService.updateBanner(id, dto);
        return Result.success();
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/{id}")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> delete(@ApiParam("轮播图ID") @PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.success();
    }

    @ApiOperation("启用轮播图")
    @PutMapping("/{id}/enable")
    @RequireRole({UserRole.ADMIN})
    public Result<Void> enable(@ApiParam("轮播图ID") @PathVariable Long id) {
        bannerService.enableBanner(id);
        return Result.success();
    }

    @ApiOperation("禁用轮播图")
    @PutMapping("/{id}/disable")
    public Result<Void> disable(@ApiParam("轮播图ID") @PathVariable Long id) {
        bannerService.disableBanner(id);
        return Result.success();
    }

    @ApiOperation("分页查询轮播图可以关联的课程（可分类）")
    @GetMapping("/course/page")
    @RequireRole({UserRole.ADMIN})
    public Result<Page<CourseVO>> page(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("课程名称") @RequestParam(required = false) String courseName,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("是否精品：1=精品/0=普通") @RequestParam(required = false) Integer isRecommend,
            @ApiParam("排序：hot=热度, time=最新, 不传=综合质量") @RequestParam(required = false) String orderBy) {
        //轮播图只能管理已发布的课程，用于用户的展示
        Page<CourseVO> result = courseService.page(page, size, courseName, categoryId, 2, isRecommend, orderBy);
        return Result.success(result);
    }

}
