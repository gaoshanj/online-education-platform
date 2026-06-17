package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseLikeVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.service.CourseLikeService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 课程点赞控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程点赞")
@RestController
@RequestMapping("/api/app/course-like")
public class AppCourseLikeController {

    @Autowired
    private CourseLikeService courseLikeService;

    /**
     * 点赞 / 取消点赞（幂等切换）
     * 需要登录
     */
    @ApiOperation("点赞或取消点赞课程")
    @PostMapping("/toggle/{courseId}")
    public Result<CourseLikeVO> toggleLike(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        CourseLikeVO vo = courseLikeService.toggleLike(currentUser.getUserId(), courseId);
        return Result.success(vo);
    }

    /**
     * 查询当前用户是否已点赞某课程
     * 需要登录
     */
    @ApiOperation("查询是否已点赞")
    @GetMapping("/status/{courseId}")
    public Result<CourseLikeVO> getLikeStatus(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser.getUserId();
        boolean liked = courseLikeService.isLiked(userId, courseId);

        CourseLikeVO vo = new CourseLikeVO();
        vo.setIsLiked(liked);
        // likeCount 由调用方按需查询，状态接口仅返回是否已点赞
        return Result.success(vo);
    }

    /**
     * 分页查询我点赞过的课程列表
     * 需要登录，结构与 /api/app/course/page 一致，前端可直接复用课程卡片
     */
    @ApiOperation("我的点赞课程列表（分页）")
    @GetMapping("/my")
    public Result<Page<CourseVO>> getMyLikedCourses(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<CourseVO> result = courseLikeService.getLikedCourses(currentUser.getUserId(), page, size);
        return Result.success(result);
    }
}
