package com.example.onlineedu.service.impl;

import com.example.onlineedu.domain.dto.DateCountDTO;
import com.example.onlineedu.domain.dto.RatingCountDTO;
import com.example.onlineedu.domain.vo.*;
import com.example.onlineedu.mapper.AdminDashboardMapper;
import com.example.onlineedu.service.AdminDashboardService;
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
 * 管理端数据统计面板服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private AdminDashboardMapper adminDashboardMapper;

    // ======================== 总览 ========================

    @Override
    public AdminDashboardOverviewVO getOverview() {
        // 并行（同一线程顺序调用）取各项统计，组装到 VO
        Integer userCount    = adminDashboardMapper.selectUserCount();
        Integer teacherCount = adminDashboardMapper.selectTeacherCount();
        Integer courseCount  = adminDashboardMapper.selectCourseCount();
        Integer studentCount = adminDashboardMapper.selectStudentCount();
        // reviewSummary 包含 reviewCount + avgScore
        AdminDashboardOverviewVO vo = adminDashboardMapper.selectReviewSummary();
        if (vo == null) {
            vo = new AdminDashboardOverviewVO();
        }
        vo.setUserCount(userCount    != null ? userCount    : 0);
        vo.setTeacherCount(teacherCount != null ? teacherCount : 0);
        vo.setCourseCount(courseCount  != null ? courseCount  : 0);
        vo.setStudentCount(studentCount != null ? studentCount : 0);
        return vo;
    }

    // ======================== 折线图 ========================

    @Override
    public AdminDashboardTrendVO getTrend(Integer days, String metric) {
        if (days == null || (days != 7 && days != 30)) {
            days = 7;
        }
        if (metric == null || metric.isBlank()) {
            metric = "newUser";
        }

        // 生成完整日期序列
        LocalDate today     = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);
        List<String> dateList = new ArrayList<>(days);
        for (LocalDate d = startDate; !d.isAfter(today); d = d.plusDays(1)) {
            dateList.add(d.format(DATE_FMT));
        }

        // 按 metric 选 SQL
        List<DateCountDTO> raw;
        switch (metric) {
            case "activeLearn":
                raw = adminDashboardMapper.selectDailyActiveLearn(startDate);
                break;
            case "newCourse":
                raw = adminDashboardMapper.selectDailyNewCourses(startDate);
                break;
            default:
                metric = "newUser";
                raw = adminDashboardMapper.selectDailyNewUsers(startDate);
        }

        Map<String, Integer> dataMap = raw.stream()
                .collect(Collectors.toMap(DateCountDTO::getDate, DateCountDTO::getCount));

        List<Integer> values = dateList.stream()
                .map(d -> dataMap.getOrDefault(d, 0))
                .collect(Collectors.toList());

        AdminDashboardTrendVO vo = new AdminDashboardTrendVO();
        vo.setMetric(metric);
        vo.setDates(dateList);
        vo.setValues(values);
        return vo;
    }

    // ======================== 评分饼图 ========================

    @Override
    public TeacherDashboardRatingDistVO getRatingDist() {
        List<RatingCountDTO> raw = adminDashboardMapper.selectRatingDist();

        Map<Integer, Integer> ratingMap = raw.stream()
                .collect(Collectors.toMap(RatingCountDTO::getRating, RatingCountDTO::getCount));

        List<Integer> ratings  = Arrays.asList(5, 4, 3, 2, 1);
        List<Integer> counts   = ratings.stream()
                .map(r -> ratingMap.getOrDefault(r, 0))
                .collect(Collectors.toList());

        int total = counts.stream().mapToInt(Integer::intValue).sum();

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

    // ======================== Top10 排行 ========================

    @Override
    public AdminDashboardTopCoursesVO getTopCourses(String rankBy) {
        if (rankBy == null || rankBy.isBlank()) {
            rankBy = "hot";
        }
        List<CourseRankItemVO> items;
        if ("studentCount".equalsIgnoreCase(rankBy)) {
            rankBy = "studentCount";
            items = adminDashboardMapper.selectStudentCountRank();
        } else {
            rankBy = "hot";
            items = adminDashboardMapper.selectHotRank();
        }
        AdminDashboardTopCoursesVO vo = new AdminDashboardTopCoursesVO();
        vo.setRankBy(rankBy);
        vo.setItems(items);
        return vo;
    }

    // ======================== 累计数据（自平台启动日起） ========================

    @Override
    public AdminDashboardCumulativeVO getCumulativeData() {
        LocalDate today = LocalDate.now();

        // 获取全量每日新增用户数据
        List<DateCountDTO> rawUsers  = adminDashboardMapper.selectAllDailyNewUsers();
        List<DateCountDTO> rawLearns = adminDashboardMapper.selectAllDailyLearns();

        // 找到最早的日期作为起点
        LocalDate earliestDate = today;
        if (!rawUsers.isEmpty()) {
            try {
                LocalDate uMin = LocalDate.parse(rawUsers.get(0).getDate(), DATE_FMT);
                if (uMin.isBefore(earliestDate)) earliestDate = uMin;
            } catch (Exception ignored) {}
        }
        if (!rawLearns.isEmpty()) {
            try {
                LocalDate lMin = LocalDate.parse(rawLearns.get(0).getDate(), DATE_FMT);
                if (lMin.isBefore(earliestDate)) earliestDate = lMin;
            } catch (Exception ignored) {}
        }

        // 构建完整日期序列
        List<String> dateList = new ArrayList<>();
        for (LocalDate d = earliestDate; !d.isAfter(today); d = d.plusDays(1)) {
            dateList.add(d.format(DATE_FMT));
        }

        // 构建 Map 方便查找
        Map<String, Integer> userMap = new LinkedHashMap<>();
        for (DateCountDTO dto : rawUsers) {
            userMap.put(dto.getDate(), dto.getCount());
        }
        Map<String, Integer> learnMap = new LinkedHashMap<>();
        for (DateCountDTO dto : rawLearns) {
            learnMap.put(dto.getDate(), dto.getCount());
        }

        // 计算累计值（单调递增）
        List<Integer> cumUsers  = new ArrayList<>(dateList.size());
        List<Integer> cumLearns = new ArrayList<>(dateList.size());
        int runningUser  = 0;
        int runningLearn = 0;

        for (String date : dateList) {
            runningUser  += userMap.getOrDefault(date, 0);
            runningLearn += learnMap.getOrDefault(date, 0);
            cumUsers.add(runningUser);
            cumLearns.add(runningLearn);
        }

        AdminDashboardCumulativeVO vo = new AdminDashboardCumulativeVO();
        vo.setDates(dateList);
        vo.setCumulativeUsers(cumUsers);
        vo.setCumulativeLearns(cumLearns);
        return vo;
    }
}
