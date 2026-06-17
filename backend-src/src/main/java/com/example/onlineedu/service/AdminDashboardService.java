package com.example.onlineedu.service;

import com.example.onlineedu.domain.vo.AdminDashboardOverviewVO;
import com.example.onlineedu.domain.vo.AdminDashboardTopCoursesVO;
import com.example.onlineedu.domain.vo.AdminDashboardTrendVO;
import com.example.onlineedu.domain.vo.TeacherDashboardRatingDistVO;

/**
 * 管理端数据统计面板服务接口
 *
 * @author feng
 */
public interface AdminDashboardService {

    /**
     * 全平台总览统计
     */
    AdminDashboardOverviewVO getOverview();

    /**
     * 折线图：单指标每日趋势
     *
     * @param days   统计天数：7 或 30
     * @param metric 指标：newUser / activeLearn / newCourse
     */
    AdminDashboardTrendVO getTrend(Integer days, String metric);

    /**
     * 全平台评分分布（复用讲师端 VO）
     */
    TeacherDashboardRatingDistVO getRatingDist();

    /**
     * Top10 排行（指标可切换）
     *
     * @param rankBy hot-热度 / studentCount-学习人数
     */
    AdminDashboardTopCoursesVO getTopCourses(String rankBy);
}
