package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.entity.*;
import com.example.onlineedu.domain.vo.TeacherStudentDetailVO;
import com.example.onlineedu.domain.vo.TeacherStudentListVO;
import com.example.onlineedu.mapper.*;
import com.example.onlineedu.service.TeacherStudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 讲师端学员管理服务实现类
 * 采用"简单SQL分页取ID + Java聚合计算"模式，避免复杂联表。
 *
 * @author feng
 */
@Slf4j
@Service
public class TeacherStudentServiceImpl implements TeacherStudentService {

    @Autowired
    private TeacherStudentMapper teacherStudentMapper;
    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;
    @Autowired
    private LearningProgressMapper progressMapper;
    @Autowired
    private CourseChapterMapper chapterMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private UserMapper userMapper;

    // ==================== 学员列表（用户维度） ====================

    @Override
    public Page<TeacherStudentListVO> listStudents(Long teacherId,
                                                   String nickname,
                                                   LocalDateTime learnStart,
                                                   LocalDateTime learnEnd,
                                                   String sortBy,
                                                   Integer page, Integer size) {
        // Step 1: 分页取该讲师下的学员 userId（简单 SQL）
        Page<Long> idPage = new Page<>(page, size);
        List<Long> studentIds = teacherStudentMapper.selectStudentUserIdPage(
                idPage, teacherId, nickname, learnStart, learnEnd, sortBy);

        Page<TeacherStudentListVO> resultPage = new Page<>(page, size, idPage.getTotal());

        if (studentIds.isEmpty()) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
        }

        // Step 2: 批量查询用户信息
        List<UserEntity> users = userMapper.selectList(
                new LambdaQueryWrapper<UserEntity>()
                        .in(UserEntity::getId, studentIds));
        Map<Long, UserEntity> userMap = users.stream()
                .collect(Collectors.toMap(UserEntity::getId, u -> u));

        // Step 3: 批量查询这些学员在该讲师课程下的所有报名记录
        // 先取讲师所有课程ID
        List<Long> teacherCourseIds = getTeacherCourseIds(teacherId);

        List<CourseEnrollmentEntity> allEnrollments = Collections.emptyList();
        if (!teacherCourseIds.isEmpty()) {
            allEnrollments = enrollmentMapper.selectList(
                    new LambdaQueryWrapper<CourseEnrollmentEntity>()
                            .in(CourseEnrollmentEntity::getUserId, studentIds)
                            .in(CourseEnrollmentEntity::getCourseId, teacherCourseIds));
        }
        // 按 userId 分组
        Map<Long, List<CourseEnrollmentEntity>> enrollByUser = allEnrollments.stream()
                .collect(Collectors.groupingBy(CourseEnrollmentEntity::getUserId));

        // Step 4: 批量查询学习进度（同样范围：这批学员 + 讲师课程）
        List<LearningProgressEntity> allProgress = Collections.emptyList();
        if (!teacherCourseIds.isEmpty()) {
            allProgress = progressMapper.selectList(
                    new LambdaQueryWrapper<LearningProgressEntity>()
                            .in(LearningProgressEntity::getUserId, studentIds)
                            .in(LearningProgressEntity::getCourseId, teacherCourseIds));
        }
        // 按 userId 分组
        Map<Long, List<LearningProgressEntity>> progressByUser = allProgress.stream()
                .collect(Collectors.groupingBy(LearningProgressEntity::getUserId));

        // Step 5: 查叶子章节数量（按课程）
        Map<Long, Long> leafCountByCourse = getLeafChapterCountMap(teacherCourseIds);

        // Step 6: Java 聚合，组装 VO
        List<TeacherStudentListVO> voList = studentIds.stream()
                .map(userId -> buildStudentListVO(
                        userId, userMap.get(userId),
                        enrollByUser.getOrDefault(userId, Collections.emptyList()),
                        progressByUser.getOrDefault(userId, Collections.emptyList()),
                        leafCountByCourse))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Step 7: 在 Java 中排序（分页数据已取出，小结果集排序合理）
        sortStudentList(voList, sortBy);

        resultPage.setRecords(voList);
        return resultPage;
    }

    // ==================== 学员详情（课程维度） ====================

    @Override
    public Page<TeacherStudentDetailVO> listStudentCourses(Long teacherId,
                                                           Long studentId,
                                                           String courseName,
                                                           Integer isCompleted,
                                                           String sortBy,
                                                           Integer page, Integer size) {
        // Step 1: 取讲师所有课程ID
        List<Long> teacherCourseIds = getTeacherCourseIds(teacherId);
        if (teacherCourseIds.isEmpty()) {
            return emptyPage(page, size);
        }

        // Step 2: 查该学员在讲师课程下的报名记录
        List<CourseEnrollmentEntity> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getUserId, studentId)
                        .in(CourseEnrollmentEntity::getCourseId, teacherCourseIds));
        if (enrollments.isEmpty()) {
            return emptyPage(page, size);
        }

        List<Long> enrolledCourseIds = enrollments.stream()
                .map(CourseEnrollmentEntity::getCourseId)
                .collect(Collectors.toList());
        Map<Long, CourseEnrollmentEntity> enrollMap = enrollments.stream()
                .collect(Collectors.toMap(CourseEnrollmentEntity::getCourseId, e -> e));

        // Step 3: 批量查课程信息，支持课程名搜索
        LambdaQueryWrapper<CourseEntity> courseWrapper = new LambdaQueryWrapper<CourseEntity>()
                .in(CourseEntity::getId, enrolledCourseIds);
        if (courseName != null && !courseName.isEmpty()) {
            courseWrapper.like(CourseEntity::getCourseName, courseName);
        }
        List<CourseEntity> courses = courseMapper.selectList(courseWrapper);
        Map<Long, CourseEntity> courseMap = courses.stream()
                .collect(Collectors.toMap(CourseEntity::getId, c -> c));

        // 过滤后的课程ID（考虑课程名筛选）
        List<Long> filteredCourseIds = courses.stream()
                .map(CourseEntity::getId)
                .collect(Collectors.toList());
        if (filteredCourseIds.isEmpty()) {
            return emptyPage(page, size);
        }

        // Step 4: 批量查该学员在这些课程下的学习进度
        List<LearningProgressEntity> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getUserId, studentId)
                        .in(LearningProgressEntity::getCourseId, filteredCourseIds));
        // 按 courseId 分组
        Map<Long, List<LearningProgressEntity>> progressByCourse = progressList.stream()
                .collect(Collectors.groupingBy(LearningProgressEntity::getCourseId));

        // Step 5: 叶子章节数
        Map<Long, Long> leafCountByCourse = getLeafChapterCountMap(filteredCourseIds);

        // Step 6: Java 聚合
        List<TeacherStudentDetailVO> voList = filteredCourseIds.stream()
                .map(courseId -> buildDetailVO(
                        courseId, courseMap.get(courseId),
                        enrollMap.get(courseId),
                        progressByCourse.getOrDefault(courseId, Collections.emptyList()),
                        leafCountByCourse.getOrDefault(courseId, 0L)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Step 7: 完成状态过滤
        if (isCompleted != null) {
            boolean completed = isCompleted == 1;
            voList = voList.stream()
                    .filter(v -> v.getIsCompleted() == completed)
                    .collect(Collectors.toList());
        }

        // Step 8: 排序
        sortDetailList(voList, sortBy);

        // Step 9: 内存分页
        long total = voList.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, voList.size());
        List<TeacherStudentDetailVO> pagedList = fromIndex >= voList.size()
                ? Collections.emptyList()
                : voList.subList(fromIndex, toIndex);

        Page<TeacherStudentDetailVO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pagedList);
        return resultPage;
    }

    // ==================== 私有工具方法 ====================

    /** 获取讲师所有课程ID */
    private List<Long> getTeacherCourseIds(Long teacherId) {
        List<CourseEntity> courses = courseMapper.selectList(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getTeacherId, teacherId)
                        .select(CourseEntity::getId));
        return courses.stream().map(CourseEntity::getId).collect(Collectors.toList());
    }

    /** 查叶子章节数量（parent_id!=0 且有视频）按 courseId 分组 */
    private Map<Long, Long> getLeafChapterCountMap(List<Long> courseIds) {
        if (courseIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CourseChapterEntity> leafChapters = chapterMapper.selectList(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .in(CourseChapterEntity::getCourseId, courseIds)
                        .ne(CourseChapterEntity::getParentId, 0)
                        .isNotNull(CourseChapterEntity::getVideoUrl)
                        .ne(CourseChapterEntity::getVideoUrl, ""));
        return leafChapters.stream()
                .collect(Collectors.groupingBy(CourseChapterEntity::getCourseId, Collectors.counting()));
    }

    /** 构建学员列表 VO */
    private TeacherStudentListVO buildStudentListVO(Long userId, UserEntity user,
                                                    List<CourseEnrollmentEntity> enrollments,
                                                    List<LearningProgressEntity> progressList,
                                                    Map<Long, Long> leafCountByCourse) {
        if (user == null) return null;

        TeacherStudentListVO vo = new TeacherStudentListVO();
        vo.setUserId(userId);
        vo.setAvatar(user.getAvatar());
        vo.setNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());

        // 加入时间：最早报名时间
        vo.setJoinTime(enrollments.stream()
                .map(CourseEnrollmentEntity::getEnrollTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null));

        // 学习课程数
        vo.setCourseCount(enrollments.size());

        // 最近学习时间
        vo.setLatestLearnTime(enrollments.stream()
                .map(CourseEnrollmentEntity::getLastLearnTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null));

        // 总学习时长（秒）
        int totalDuration = progressList.stream()
                .mapToInt(p -> p.getDuration() != null ? p.getDuration() : 0)
                .sum();
        vo.setTotalDuration(totalDuration);

        // 平均完成率：按课程分组 → 计算各课程完成率 → 取平均
        Map<Long, List<LearningProgressEntity>> progressByCourse = progressList.stream()
                .collect(Collectors.groupingBy(LearningProgressEntity::getCourseId));

        if (progressByCourse.isEmpty()) {
            vo.setAvgCompletionRate(BigDecimal.ZERO);
        } else {
            double avgRate = progressByCourse.entrySet().stream()
                    .mapToDouble(entry -> {
                        long leafTotal = leafCountByCourse.getOrDefault(entry.getKey(), 0L);
                        if (leafTotal == 0) return 0.0;
                        long completed = entry.getValue().stream()
                                .filter(p -> p.getIsCompleted() != null && p.getIsCompleted() == 1)
                                .count();
                        return completed * 100.0 / leafTotal;
                    })
                    .average()
                    .orElse(0.0);
            vo.setAvgCompletionRate(BigDecimal.valueOf(avgRate).setScale(2, RoundingMode.HALF_UP));
        }

        return vo;
    }

    /** 构建学员课程详情 VO */
    private TeacherStudentDetailVO buildDetailVO(Long courseId, CourseEntity course,
                                                 CourseEnrollmentEntity enrollment,
                                                 List<LearningProgressEntity> progressList,
                                                 long leafTotal) {
        if (course == null || enrollment == null) return null;

        TeacherStudentDetailVO vo = new TeacherStudentDetailVO();
        vo.setCourseId(courseId);
        vo.setCoverImage(course.getCoverImage());
        vo.setCourseName(course.getCourseName());
        vo.setEnrollTime(enrollment.getEnrollTime());
        vo.setLastLearnTime(enrollment.getLastLearnTime());

        // 学习时长
        int duration = progressList.stream()
                .mapToInt(p -> p.getDuration() != null ? p.getDuration() : 0)
                .sum();
        vo.setDuration(duration);

        // 已完成章节数
        long completed = progressList.stream()
                .filter(p -> p.getIsCompleted() != null && p.getIsCompleted() == 1)
                .count();
        vo.setCompletedChapters((int) completed);
        vo.setTotalChapters((int) leafTotal);

        // 完成率
        BigDecimal rate;
        if (leafTotal == 0) {
            rate = BigDecimal.ZERO;
        } else {
            rate = BigDecimal.valueOf(completed * 100.0 / leafTotal)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        vo.setCompletionRate(rate);
        vo.setIsCompleted(leafTotal > 0 && completed >= leafTotal);

        return vo;
    }

    /** 学员列表排序 */
    private void sortStudentList(List<TeacherStudentListVO> list, String sortBy) {
        if ("courseCount".equals(sortBy)) {
            list.sort(Comparator.comparingInt(TeacherStudentListVO::getCourseCount).reversed());
        } else if ("duration".equals(sortBy)) {
            list.sort(Comparator.comparingInt(TeacherStudentListVO::getTotalDuration).reversed());
        } else if ("completionRate".equals(sortBy)) {
            list.sort(Comparator.comparing(TeacherStudentListVO::getAvgCompletionRate).reversed());
        } else {
            // 默认：最近学习时间倒序
            list.sort(Comparator.comparing(
                    v -> v.getLatestLearnTime() != null ? v.getLatestLearnTime() : LocalDateTime.MIN,
                    Comparator.reverseOrder()));
        }
    }

    /** 学员详情排序 */
    private void sortDetailList(List<TeacherStudentDetailVO> list, String sortBy) {
        if ("completionRate".equals(sortBy)) {
            list.sort(Comparator.comparing(TeacherStudentDetailVO::getCompletionRate).reversed());
        } else if ("duration".equals(sortBy)) {
            list.sort(Comparator.comparingInt(TeacherStudentDetailVO::getDuration).reversed());
        } else if ("enrollTime".equals(sortBy)) {
            list.sort(Comparator.comparing(
                    v -> v.getEnrollTime() != null ? v.getEnrollTime() : LocalDateTime.MIN,
                    Comparator.reverseOrder()));
        } else {
            // 默认：最后学习时间倒序
            list.sort(Comparator.comparing(
                    v -> v.getLastLearnTime() != null ? v.getLastLearnTime() : LocalDateTime.MIN,
                    Comparator.reverseOrder()));
        }
    }

    private <T> Page<T> emptyPage(Integer page, Integer size) {
        Page<T> p = new Page<>(page, size, 0);
        p.setRecords(Collections.emptyList());
        return p;
    }
}
