package com.example.onlineedu.controller.teacher;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.TeacherDashboardOverviewVO;
import com.example.onlineedu.domain.vo.TeacherDashboardRatingDistVO;
import com.example.onlineedu.domain.vo.TeacherDashboardTopCoursesVO;
import com.example.onlineedu.domain.vo.TeacherDashboardTrendVO;
import com.example.onlineedu.service.TeacherDashboardService;
import com.example.onlineedu.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 讲师端 - 数据统计面板控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "讲师端-数据统计面板")
@RestController
@RequestMapping("/api/teacher/dashboard")
public class TeacherDashboardController {

    @Autowired
    private TeacherDashboardService dashboardService;

    /**
     * 1. 总览统计卡片
     * GET /api/teacher/dashboard/overview
     */
    @ApiOperation("总览统计（课程数、学习人数、点赞、收藏、评价、平均评分）")
    @GetMapping("/overview")
    @RequireRole({UserRole.TEACHER})
    public Result<TeacherDashboardOverviewVO> getOverview() {
        LoginUser currentUser = UserContext.getUser();
        return Result.success(dashboardService.getOverview(currentUser.getUserId()));
    }

    /**
     * 2. 折线图：每日新增/活跃学员趋势
     * GET /api/teacher/dashboard/trend?days=7
     */
    @ApiOperation("折线图：每日新增学员 / 每日活跃学习人数")
    @GetMapping("/trend")
    @RequireRole({UserRole.TEACHER})
    public Result<TeacherDashboardTrendVO> getTrend(
            @ApiParam("统计天数：7 或 30，默认 7")
            @RequestParam(defaultValue = "7") Integer days) {
        LoginUser currentUser = UserContext.getUser();
        return Result.success(dashboardService.getTrend(currentUser.getUserId(), days));
    }

    /**
     * 3. 饼图：课程评分分布
     * GET /api/teacher/dashboard/rating-dist
     */
    @ApiOperation("饼图：1-5 星评分分布与占比")
    @GetMapping("/rating-dist")
    @RequireRole({UserRole.TEACHER})
    public Result<TeacherDashboardRatingDistVO> getRatingDist() {
        LoginUser currentUser = UserContext.getUser();
        return Result.success(dashboardService.getRatingDist(currentUser.getUserId()));
    }

    /**
     * 4. Top10 排行（学习人数 + 评分）
     * GET /api/teacher/dashboard/top-courses
     */
    @ApiOperation("Top10 排行：学习人数 + 评分（适配 ECharts 条形图）")
    @GetMapping("/top-courses")
    @RequireRole({UserRole.TEACHER})
    public Result<TeacherDashboardTopCoursesVO> getTopCourses() {
        LoginUser currentUser = UserContext.getUser();
        return Result.success(dashboardService.getTopCourses(currentUser.getUserId()));
    }
}
