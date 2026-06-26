package com.example.onlineedu.mapper;

import com.example.onlineedu.domain.dto.DateCountDTO;
import com.example.onlineedu.domain.dto.RatingCountDTO;
import com.example.onlineedu.domain.vo.AdminDashboardOverviewVO;
import com.example.onlineedu.domain.vo.CourseRankItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 管理端数据统计面板 Mapper
 *
 * @author feng
 */
@Mapper
public interface AdminDashboardMapper {

    // ---- 总览统计 ----

    /** 用户总数 */
    Integer selectUserCount();

    /** 讲师总数（有 TEACHER 角色的用户数） */
    Integer selectTeacherCount();

    /** 课程总数（含所有状态） */
    Integer selectCourseCount();

    /** 学习人数（报名表去重） */
    Integer selectStudentCount();

    /** 评价总数 & 加权平均评分（映射到 AdminDashboardOverviewVO 的 reviewCount / avgScore） */
    AdminDashboardOverviewVO selectReviewSummary();

    // ---- 折线图 ----

    /** 每日新增用户 */
    List<DateCountDTO> selectDailyNewUsers(@Param("startDate") LocalDate startDate);

    /** 每日活跃学习人数 */
    List<DateCountDTO> selectDailyActiveLearn(@Param("startDate") LocalDate startDate);

    /** 每日新增课程 */
    List<DateCountDTO> selectDailyNewCourses(@Param("startDate") LocalDate startDate);

    // ---- Top10 排行 ----

    /** 热度 Top10（student_count×0.4 + like_count×0.3 + favorite_count×0.3） */
    List<CourseRankItemVO> selectHotRank();

    /** 学习人数 Top10 */
    List<CourseRankItemVO> selectStudentCountRank();

    // ---- 评分分布 ----

    /** 全平台评分分布 */
    List<RatingCountDTO> selectRatingDist();

    // ---- 累计数据 ----

    /** 每日新增用户数（从平台启动日到今天） */
    List<DateCountDTO> selectAllDailyNewUsers();

    /** 每日学习事件数（从平台启动日到今天） */
    List<DateCountDTO> selectAllDailyLearns();
}
