package com.example.onlineedu.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.dto.CourseReviewDTO;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseReviewLikeVO;
import com.example.onlineedu.domain.vo.CourseReviewStatVO;
import com.example.onlineedu.domain.vo.CourseReviewVO;
import com.example.onlineedu.service.CourseReviewService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户端 - 课程评价控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "用户端-课程评价")
@RestController
@RequestMapping("/api/app/course/review")
public class AppCourseReviewController {

    @Autowired
    private CourseReviewService courseReviewService;

    /**
     * 1. 提交评价
     * POST /api/course/review
     */
    @ApiOperation("提交课程评价")
    @PostMapping
    public Result<Void> submitReview(@Valid @RequestBody CourseReviewDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        courseReviewService.submitReview(currentUser.getUserId(), dto);
        return Result.success();
    }

    /**
     * 2. 修改评价
     * PUT /api/course/review/{id}
     */
    @ApiOperation("修改课程评价")
    @PutMapping("/{id}")
    public Result<Void> updateReview(
            @ApiParam("评价ID") @PathVariable Long id,
            @Valid @RequestBody CourseReviewDTO dto) {
        LoginUser currentUser = UserContext.getUser();
        courseReviewService.updateReview(currentUser.getUserId(), id, dto);
        return Result.success();
    }

    /**
     * 3. 删除评价
     * DELETE /api/course/review/{id}
     */
    @ApiOperation("删除课程评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@ApiParam("评价ID") @PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        courseReviewService.deleteReview(currentUser.getUserId(), id);
        return Result.success();
    }

    /**
     * 4. 评价列表（分页 + 排序 + 筛选）
     * GET /api/course/review/list
     */
    @ApiOperation("获取课程评价列表")
    @GetMapping("/list")
    public Result<Page<CourseReviewVO>> listReviews(
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("排序方式：latest-最新（默认），hot-热门") @RequestParam(defaultValue = "latest") String sortType,
            @ApiParam("评分筛选 1-5（不传则不筛选）") @RequestParam(required = false) Integer rating,
            @ApiParam("是否只看我的评价") @RequestParam(defaultValue = "false") Boolean mine,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        Page<CourseReviewVO> result = courseReviewService.listReviews(
                userId, courseId, sortType, rating, mine, page, size);
        return Result.success(result);
    }

    /**
     * 5. 点赞/取消点赞评价（幂等切换）
     * POST /api/course/review/{id}/like
     */
    @ApiOperation("点赞或取消点赞评价")
    @PostMapping("/{id}/like")
    public Result<CourseReviewLikeVO> toggleLike(@ApiParam("评价ID") @PathVariable Long id) {
        LoginUser currentUser = UserContext.getUser();
        CourseReviewLikeVO vo = courseReviewService.toggleReviewLike(currentUser.getUserId(), id);
        return Result.success(vo);
    }

    /**
     * 7. 获取评分统计
     * GET /api/course/review/stat/{courseId}
     */
    @ApiOperation("获取课程评分统计")
    @GetMapping("/stat/{courseId}")
    public Result<CourseReviewStatVO> getReviewStat(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        Long userId = currentUser != null ? currentUser.getUserId() : null;
        CourseReviewStatVO stat = courseReviewService.getReviewStat(userId, courseId);
        return Result.success(stat);
    }


}
