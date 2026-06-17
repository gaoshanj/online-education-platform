package com.example.onlineedu.service;

import com.example.onlineedu.domain.vo.TeacherDashboardOverviewVO;
import com.example.onlineedu.domain.vo.TeacherDashboardRatingDistVO;
import com.example.onlineedu.domain.vo.TeacherDashboardTopCoursesVO;
import com.example.onlineedu.domain.vo.TeacherDashboardTrendVO;

/**
 * 讲师端数据统计面板服务接口
 *
 * @author feng
 */
public interface TeacherDashboardService {

    /**
     * 总览统计卡片
     */
    TeacherDashboardOverviewVO getOverview(Long teacherId);

    /**
     * 折线图：每日新增 / 活跃学员趋势
     *
     * @param teacherId 讲师ID
     * @param days      统计天数：7 或 30
     */
    TeacherDashboardTrendVO getTrend(Long teacherId, Integer days);

    /**
     * 饼图：评分分布（1-5 星，缺失补 0）
     */
    TeacherDashboardRatingDistVO getRatingDist(Long teacherId);

    /**
     * Top10 排行：学习人数 + 评分
     */
    TeacherDashboardTopCoursesVO getTopCourses(Long teacherId);
}
