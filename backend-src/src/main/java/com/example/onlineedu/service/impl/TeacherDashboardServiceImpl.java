package com.example.onlineedu.service.impl;

import com.example.onlineedu.domain.dto.DateCountDTO;
import com.example.onlineedu.domain.dto.RatingCountDTO;
import com.example.onlineedu.domain.vo.*;
import com.example.onlineedu.mapper.TeacherDashboardMapper;
import com.example.onlineedu.service.TeacherDashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 讲师端数据统计面板服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class TeacherDashboardServiceImpl implements TeacherDashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private TeacherDashboardMapper dashboardMapper;

    // ==================== 1. 总览 ====================

    @Override
    public TeacherDashboardOverviewVO getOverview(Long teacherId) {
        TeacherDashboardOverviewVO vo = dashboardMapper.selectOverview(teacherId);
        return vo != null ? vo : new TeacherDashboardOverviewVO();
    }

    // ==================== 2. 折线图趋势 ====================

    @Override
    public TeacherDashboardTrendVO getTrend(Long teacherId, Integer days) {
        if (days == null || (days != 7 && days != 30)) {
            days = 7;
        }

        // 生成日期序列（从 days 天前到今天，含今天）
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        List<String> dateList = new ArrayList<>(days);
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            dateList.add(d.format(DATE_FMT));
        }

        // 查询 SQL 结果，转为 Map<date, count>
        List<DateCountDTO> newRaw = dashboardMapper.selectDailyNewStudents(teacherId, startDate);
        List<DateCountDTO> activeRaw = dashboardMapper.selectDailyActiveStudents(teacherId, startDate);

        Map<String, Integer> newMap = newRaw.stream()
                .collect(Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));
        Map<String, Integer> activeMap = activeRaw.stream()
                .collect(Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));

        // 按日期序列组装，缺失补 0
        List<Integer> newStudents = dateList.stream()
                .map(d -> newMap.getOrDefault(d, 0))
                .collect(Collectors.toList());
        List<Integer> activeStudents = dateList.stream()
                .map(d -> activeMap.getOrDefault(d, 0))
                .collect(Collectors.toList());

        TeacherDashboardTrendVO vo = new TeacherDashboardTrendVO();
        vo.setDates(dateList);
        vo.setNewStudents(newStudents);
        vo.setActiveStudents(activeStudents);
        return vo;
    }

    // ==================== 3. 饼图：评分分布 ====================

    @Override
    public TeacherDashboardRatingDistVO getRatingDist(Long teacherId) {
        List<RatingCountDTO> raw = dashboardMapper.selectRatingDist(teacherId);

        // SQL 结果转 Map，确保 1-5 星均有
        Map<Integer, Integer> ratingMap = raw.stream()
                .collect(Collectors.toMap(RatingCountDTO::getRating, RatingCountDTO::getCount));

        // 固定输出顺序：5→4→3→2→1
        List<Integer> ratings = Arrays.asList(5, 4, 3, 2, 1);
        List<Integer> counts = ratings.stream()
                .map(r -> ratingMap.getOrDefault(r, 0))
                .collect(Collectors.toList());

        int total = counts.stream().mapToInt(Integer::intValue).sum();

        // 计算占比
        List<BigDecimal> percentages = counts.stream()
                .map(c -> total == 0
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(c * 100.0 / total).setScale(2, RoundingMode.HALF_UP))
                .collect(Collectors.toList());

        TeacherDashboardRatingDistVO vo = new TeacherDashboardRatingDistVO();
        vo.setRatings(ratings);
        vo.setCounts(counts);
        vo.setPercentages(percentages);
        vo.setTotal(total);
        return vo;
    }

    // ==================== 4. Top10 排行 ====================

    @Override
    public TeacherDashboardTopCoursesVO getTopCourses(Long teacherId) {
        List<CourseRankItemVO> studentRank = dashboardMapper.selectStudentCountRank(teacherId);
        List<CourseRankItemVO> scoreRank = dashboardMapper.selectScoreRank(teacherId);

        TeacherDashboardTopCoursesVO vo = new TeacherDashboardTopCoursesVO();
        vo.setStudentRank(studentRank);
        vo.setScoreRank(scoreRank);
        return vo;
    }
}
