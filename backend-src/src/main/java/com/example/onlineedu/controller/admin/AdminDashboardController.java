package com.example.onlineedu.controller.admin;

import com.example.onlineedu.annotation.RequireRole;
import com.example.onlineedu.common.Result;
import com.example.onlineedu.domain.enums.UserRole;
import com.example.onlineedu.domain.vo.AdminDashboardCumulativeVO;
import com.example.onlineedu.domain.vo.AdminDashboardOverviewVO;
import com.example.onlineedu.domain.vo.AdminDashboardTopCoursesVO;
import com.example.onlineedu.domain.vo.AdminDashboardTrendVO;
import com.example.onlineedu.domain.vo.TeacherDashboardRatingDistVO;
import com.example.onlineedu.service.AdminDashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 数据统计面板控制器
 *
 * @author feng
 */
@Slf4j
@Api(tags = "管理端-数据统计面板")
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService dashboardService;

    /**
     * 1. 全平台总览统计
     * GET /api/admin/dashboard/overview
     */
    @ApiOperation("全平台总览统计（用户数/讲师数/课程数/学习人数/评价数/平均评分）")
    @GetMapping("/overview")
    @RequireRole({UserRole.ADMIN})
    public Result<AdminDashboardOverviewVO> getOverview() {
        return Result.success(dashboardService.getOverview());
    }

    /**
     * 2. 折线图：每日趋势（单指标）
     * GET /api/admin/dashboard/trend?days=7&metric=newUser
     */
    @ApiOperation("折线图：每日指标趋势（指标可切换）")
    @GetMapping("/trend")
    @RequireRole({UserRole.ADMIN})
    public Result<AdminDashboardTrendVO> getTrend(
            @ApiParam("统计天数：7 或 30，默认 7")
            @RequestParam(defaultValue = "7") Integer days,
            @ApiParam("指标：newUser（新增用户）/ activeLearn（活跃学习）/ newCourse（新增课程），默认 newUser")
            @RequestParam(defaultValue = "newUser") String metric) {
        return Result.success(dashboardService.getTrend(days, metric));
    }

    /**
     * 3. 饼图：全平台评分分布
     * GET /api/admin/dashboard/rating-dist
     */
    @ApiOperation("饼图：全平台课程评分分布（1-5星均输出）")
    @GetMapping("/rating-dist")
    @RequireRole({UserRole.ADMIN})
    public Result<TeacherDashboardRatingDistVO> getRatingDist() {
        return Result.success(dashboardService.getRatingDist());
    }

    /**
     * 4. Top10 排行（指标可切换）
     * GET /api/admin/dashboard/top-courses?rankBy=hot
     */
    @ApiOperation("Top10 课程排行（指标可切换：热度/学习人数）")
    @GetMapping("/top-courses")
    @RequireRole({UserRole.ADMIN})
    public Result<AdminDashboardTopCoursesVO> getTopCourses(
            @ApiParam("排序指标：hot（热度，默认）/ studentCount（学习人数）")
            @RequestParam(defaultValue = "hot") String rankBy) {
        return Result.success(dashboardService.getTopCourses(rankBy));
    }

    /**
     * 5. 累计数据（自平台启动日起）
     * GET /api/admin/dashboard/cumulative
     */
    @ApiOperation("累计趋势数据（累计用户数 + 累计学习次数，从平台启动日到今天）")
    @GetMapping("/cumulative")
    @RequireRole({UserRole.ADMIN})
    public Result<AdminDashboardCumulativeVO> getCumulative() {
        return Result.success(dashboardService.getCumulativeData());
    }
}
