package com.example.onlineedu.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseReviewLikeVO;
import com.example.onlineedu.domain.vo.CourseReviewStatListVO;
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

/**
 * 管理员端 - 课程评价控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理员端-课程评价管理")
@RestController
@RequestMapping("/api/admin/reviews")
public class AdminCourseReviewController {

    @Autowired
    private CourseReviewService courseReviewService;

    /**
     * 1. 查询评价列表（支持按 courseId 和 rating 筛选）
     * GET /api/admin/reviews
     */
//    @ApiOperation("管理员查询评价列表")
//    @GetMapping
//    public Result<Page<CourseReviewVO>> listReviews(
//            @ApiParam("课程ID（不传则查所有）") @RequestParam(required = false) Long courseId,
//            @ApiParam("评分筛选 1-5（不传则不筛选）") @RequestParam(required = false) Integer rating,
//            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
//            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
//        Page<CourseReviewVO> result = courseReviewService.adminListReviews(courseId, rating, page, size);
//        return Result.success(result);
//    }

    /**
     * 2. 删除违规评价（物理删除）
     * DELETE /api/admin/reviews/{id}
     */
    @ApiOperation("管理员删除违规评价")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@ApiParam("评价ID") @PathVariable Long id) {
        courseReviewService.adminDeleteReview(id);
        return Result.success();
    }

    /**
     * 3. 课程评价统计列表（课程维度）
     * GET /api/admin/reviews/stat
     */
    @ApiOperation("管理员查询课程评价统计列表")
    @GetMapping("/stat")
    public Result<Page<CourseReviewStatListVO>> listCourseReviewStat(
            @ApiParam("课程名模糊搜索") @RequestParam(required = false) String courseName,
            @ApiParam("讲师ID筛选") @RequestParam(required = false) Long teacherId,
            @ApiParam("评分段筛选：all/above4_5/above4_0/below3_5") @RequestParam(defaultValue = "all") String avgScoreFilter,
            @ApiParam("好评率筛选：all/above90/above80/below60") @RequestParam(defaultValue = "all") String goodReviewRateFilter,
            @ApiParam("排序：latestReview/reviewCount/scoreDesc/scoreAsc/goodRateDesc") @RequestParam(defaultValue = "latestReview") String sortBy,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        Page<CourseReviewStatListVO> result = courseReviewService.adminListCourseReviewStat(
                courseName, teacherId, avgScoreFilter, goodReviewRateFilter, sortBy, page, size);
        return Result.success(result);
    }

    /**
     * 4. 管理端详情中的评价列表（分页 + 排序 + 筛选）
     * GET /api/admin/reviews/list
     */
    @ApiOperation("获取课程评价列表")
    @GetMapping("/list")
    public Result<Page<CourseReviewVO>> listReviews(
            @ApiParam("课程ID") @RequestParam Long courseId,
            @ApiParam("排序方式：latest-最新（默认），hot-热门") @RequestParam(defaultValue = "latest") String sortType,
            @ApiParam("评分筛选 1-5（不传则不筛选）") @RequestParam(required = false) Integer rating,
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        LoginUser currentUser = UserContext.getUser();
        Page<CourseReviewVO> result = courseReviewService.listReviews(
                currentUser.getUserId(), courseId, sortType, rating, Boolean.FALSE, page, size);
        return Result.success(result);
    }

    /**
     * 5. 获取评分统计
     * GET /api/admin/reviews/stat/{courseId}
     */
    @ApiOperation("获取课程评分统计")
    @GetMapping("/stat/{courseId}")
    public Result<CourseReviewStatVO> getReviewStat(
            @ApiParam("课程ID") @PathVariable Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        CourseReviewStatVO stat = courseReviewService.getReviewStat(currentUser.getUserId(), courseId);
        return Result.success(stat);
    }
}
