package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseFavoriteVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.CourseFavoriteService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 课程收藏控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程收藏")
@RestController
@RequestMapping("/api/app/course-favorite")
public class AppCourseFavoriteController {

    @Autowired
    private CourseFavoriteService courseFavoriteService;

    /**
     * 收藏 / 取消收藏（幂等切换）
     * 需要登录
     */
    @ApiOperation("收藏或取消收藏课程")
    @PostMapping("/toggle/{courseId}")
    public Result<CourseFavoriteVO> toggleFavorite(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        CourseFavoriteVO vo = courseFavoriteService.toggleFavorite(currentUser.getUserId(), courseId);
        return Result.success(vo);
    }

    /**
     * 查询当前用户是否已收藏某课程
     * 需要登录
     */
    @ApiOperation("查询是否已收藏")
    @GetMapping("/status/{courseId}")
    public Result<CourseFavoriteVO> getFavoriteStatus(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        boolean favorited = courseFavoriteService.isFavorited(currentUser.getUserId(), courseId);

        CourseFavoriteVO vo = new CourseFavoriteVO();
        vo.setIsFavorited(favorited);
        return Result.success(vo);
    }

    /**
     * 分页查询我收藏过的课程列表
     * 需要登录，结构与 /api/app/course/page 一致，前端可直接复用课程卡片
     */
    @ApiOperation("我的收藏课程列表（分页）")
    @GetMapping("/my")
    public Result<Page<CourseVO>> getMyFavoritedCourses(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<CourseVO> result = courseFavoriteService.getFavoritedCourses(currentUser.getUserId(), page, size);
        return Result.success(result);
    }
}
