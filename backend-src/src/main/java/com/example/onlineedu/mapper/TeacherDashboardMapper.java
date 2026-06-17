package com.example.onlineedu.mapper;

import com.example.onlineedu.domain.dto.DateCountDTO;
import com.example.onlineedu.domain.dto.RatingCountDTO;
import com.example.onlineedu.domain.vo.CourseRankItemVO;
import com.example.onlineedu.domain.vo.TeacherDashboardOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 讲师端数据统计面板 Mapper
 *
 * @author feng
 */
@Mapper
public interface TeacherDashboardMapper {

    /**
     * 总览统计：一条 SQL 聚合课程表冗余字段
     */
    TeacherDashboardOverviewVO selectOverview(@Param("teacherId") Long teacherId);

    /**
     * 每日新增学员数（按 enroll_time 分组）
     */
    List<DateCountDTO> selectDailyNewStudents(@Param("teacherId") Long teacherId,
                                              @Param("startDate") LocalDate startDate);

    /**
     * 每日活跃学习人数（按 last_learn_time 分组）
     */
    List<DateCountDTO> selectDailyActiveStudents(@Param("teacherId") Long teacherId,
                                                 @Param("startDate") LocalDate startDate);

    /**
     * 评分分布：GROUP BY rating
     */
    List<RatingCountDTO> selectRatingDist(@Param("teacherId") Long teacherId);

    /**
     * 学习人数 Top10（按 student_count DESC）
     */
    List<CourseRankItemVO> selectStudentCountRank(@Param("teacherId") Long teacherId);

    /**
     * 评分 Top10（按 avg_score DESC，仅有评价的课程）
     */
    List<CourseRankItemVO> selectScoreRank(@Param("teacherId") Long teacherId);
}
