package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.entity.CourseEnrollmentEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.vo.MyLearningVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseEnrollmentMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.service.CourseEnrollmentService;
import com.example.onlineedu.service.LearningProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.onlineedu.domain.entity.LearningProgressEntity;
import com.example.onlineedu.domain.entity.CourseChapterEntity;
import com.example.onlineedu.mapper.LearningProgressMapper;
import com.example.onlineedu.mapper.CourseChapterMapper;

/**
 * 课程报名服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private LearningProgressService learningProgressService;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private CourseChapterMapper courseChapterMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enroll(Long userId, Long courseId) {
        // 检查课程是否存在
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 检查课程是否已发布（status=2 才是已发布）
        if (course.getStatus() != 2) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程未发布,无法报名");
        }

        // 检查是否已报名
        LambdaQueryWrapper<CourseEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEnrollmentEntity::getUserId, userId)
                .eq(CourseEnrollmentEntity::getCourseId, courseId);
        CourseEnrollmentEntity existingEnrollment = enrollmentMapper.selectOne(queryWrapper);

        if (existingEnrollment != null) {
            log.info("用户{}已报名课程{},无需重复报名", userId, courseId);
            return;
        }

        // 创建报名记录
        CourseEnrollmentEntity enrollment = new CourseEnrollmentEntity();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollTime(LocalDateTime.now());
        enrollment.setCreateTime(LocalDateTime.now());
        enrollment.setUpdateTime(LocalDateTime.now());

        enrollmentMapper.insert(enrollment);

        // 增加课程学习人数
        course.setStudentCount(course.getStudentCount() + 1);
        course.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(course);

        log.info("用户{}成功报名课程{}", userId, courseId);
    }

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEnrollmentEntity::getUserId, userId)
                .eq(CourseEnrollmentEntity::getCourseId, courseId);
        return enrollmentMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Page<MyLearningVO> getMyLearningCourses(Long userId, Integer page, Integer size) {
        // 查询用户报名的课程
        LambdaQueryWrapper<CourseEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEnrollmentEntity::getUserId, userId)
                .orderByDesc(CourseEnrollmentEntity::getLastLearnTime)
                .orderByDesc(CourseEnrollmentEntity::getEnrollTime);

        Page<CourseEnrollmentEntity> enrollmentPage = enrollmentMapper.selectPage(
                new Page<>(page, size), queryWrapper);

        // 转换为VO
        Page<MyLearningVO> voPage = new Page<>(enrollmentPage.getCurrent(),
                enrollmentPage.getSize(), enrollmentPage.getTotal());

        List<MyLearningVO> voList = enrollmentPage.getRecords().stream()
                .map(enrollment -> {
                    MyLearningVO vo = new MyLearningVO();
                    
                    // 查询课程信息
                    CourseEntity course = courseMapper.selectById(enrollment.getCourseId());
                    if (course != null) {
                        vo.setCourseId(course.getId());
                        vo.setCourseName(course.getCourseName());
                        vo.setCoverImage(course.getCoverImage());

                        // 查询讲师名称
                        UserEntity teacher = userMapper.selectById(course.getTeacherId());
                        if (teacher != null) {
                            vo.setTeacherName(teacher.getNickname() != null ? 
                                    teacher.getNickname() : teacher.getUsername());
                        }
                    }

                    vo.setEnrollTime(enrollment.getEnrollTime());
                    vo.setLastLearnTime(enrollment.getLastLearnTime());

                    // 计算学习进度
                    Integer progress = learningProgressService.calculateCourseProgress(
                            userId, enrollment.getCourseId());
                    vo.setLearningProgress(progress);

                    // 查询课程总章节数（排除父章节，parent_id != 0 的才是叶子章节）
                    LambdaQueryWrapper<CourseChapterEntity> totalChapterQuery = new LambdaQueryWrapper<>();
                    totalChapterQuery.eq(CourseChapterEntity::getCourseId, enrollment.getCourseId())
                            .ne(CourseChapterEntity::getParentId, 0L);
                    Long  totalChapters =  courseChapterMapper.selectCount(totalChapterQuery);
                    vo.setTotalChapters(totalChapters.intValue());

                    // 查询已完成章节数（is_completed=1 且对应章节 parent_id != 0）
                    LambdaQueryWrapper<LearningProgressEntity> completedQuery = new LambdaQueryWrapper<>();
                    completedQuery.eq(LearningProgressEntity::getUserId, userId)
                            .eq(LearningProgressEntity::getCourseId, enrollment.getCourseId())
                            .eq(LearningProgressEntity::getIsCompleted, 1);
                    List<LearningProgressEntity> completedList = learningProgressMapper.selectList(completedQuery);
                    // 过滤掉父章节（parentId = 0 的章节不计入）
                    long completedChapters = completedList.stream()
                            .filter(p -> {
                                CourseChapterEntity c = courseChapterMapper.selectById(p.getChapterId());
                                return c != null && c.getParentId() != 0L;
                            })
                            .count();
                    vo.setCompletedChapters((int) completedChapters);

                    // 查询最近学习章节
                    LambdaQueryWrapper<LearningProgressEntity> progressQuery = new LambdaQueryWrapper<>();
                    progressQuery.eq(LearningProgressEntity::getUserId, userId)
                            .eq(LearningProgressEntity::getCourseId, enrollment.getCourseId())
                            .orderByDesc(LearningProgressEntity::getUpdateTime)
                            .last("limit 1");
                    
                    LearningProgressEntity recentProgress = learningProgressMapper.selectOne(progressQuery);
                    if (recentProgress != null) {
                        vo.setRecentChapterId(recentProgress.getChapterId());
                        CourseChapterEntity chapter = courseChapterMapper.selectById(recentProgress.getChapterId());
                        if (chapter != null) {
                            vo.setRecentChapterName(chapter.getChapterName());
                        }
                    }

                    return vo;
                })
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void updateLastLearnTime(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEnrollmentEntity::getUserId, userId)
                .eq(CourseEnrollmentEntity::getCourseId, courseId);

        CourseEnrollmentEntity enrollment = enrollmentMapper.selectOne(queryWrapper);
        if (enrollment != null) {
            enrollment.setLastLearnTime(LocalDateTime.now());
            enrollment.setUpdateTime(LocalDateTime.now());
            enrollmentMapper.updateById(enrollment);
        }
    }

    @Override
    public MyLearningVO getRecentLearningCourse(Long userId) {
        // 查询用户最近一次学习的课程记录
        LambdaQueryWrapper<CourseEnrollmentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEnrollmentEntity::getUserId, userId)
                .orderByDesc(CourseEnrollmentEntity::getLastLearnTime)
                .last("limit 1");

        CourseEnrollmentEntity enrollment = enrollmentMapper.selectOne(queryWrapper);
        if (enrollment == null) {
            return null;
        }

        MyLearningVO vo = new MyLearningVO();
        
        // 查询课程信息
        CourseEntity course = courseMapper.selectById(enrollment.getCourseId());
        if (course != null) {
            vo.setCourseId(course.getId());
            vo.setCourseName(course.getCourseName());
            vo.setCoverImage(course.getCoverImage());

            // 查询讲师名称
            UserEntity teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getNickname() != null ? 
                        teacher.getNickname() : teacher.getUsername());
            }
        }

        vo.setEnrollTime(enrollment.getEnrollTime());
        vo.setLastLearnTime(enrollment.getLastLearnTime());

        // 计算学习进度
        Integer progress = learningProgressService.calculateCourseProgress(
                userId, enrollment.getCourseId());
        vo.setLearningProgress(progress);

        // 查询课程总章节数（排除父章节，parent_id != 0 的才是叶子章节）
        LambdaQueryWrapper<CourseChapterEntity> totalChapterQuery = new LambdaQueryWrapper<>();
        totalChapterQuery.eq(CourseChapterEntity::getCourseId, enrollment.getCourseId())
                .ne(CourseChapterEntity::getParentId, 0L);
        Long totalChapters =  courseChapterMapper.selectCount(totalChapterQuery);
        vo.setTotalChapters(totalChapters.intValue());

        // 查询已完成章节数（is_completed=1 且对应章节 parent_id != 0）
        LambdaQueryWrapper<LearningProgressEntity> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, enrollment.getCourseId())
                .eq(LearningProgressEntity::getIsCompleted, 1);
        List<LearningProgressEntity> completedList = learningProgressMapper.selectList(completedQuery);
        // 过滤掉父章节（parentId = 0 的章节不计入）
        long completedChapters = completedList.stream()
                .filter(p -> {
                    CourseChapterEntity c = courseChapterMapper.selectById(p.getChapterId());
                    return c != null && c.getParentId() != 0L;
                })
                .count();
        vo.setCompletedChapters((int) completedChapters);

        // 查询最近学习章节
        LambdaQueryWrapper<LearningProgressEntity> progressQuery = new LambdaQueryWrapper<>();
        progressQuery.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, enrollment.getCourseId())
                .orderByDesc(LearningProgressEntity::getUpdateTime)
                .last("limit 1");
        
        LearningProgressEntity recentProgress = learningProgressMapper.selectOne(progressQuery);
        if (recentProgress != null) {
            vo.setRecentChapterId(recentProgress.getChapterId());
            CourseChapterEntity chapter = courseChapterMapper.selectById(recentProgress.getChapterId());
            if (chapter != null) {
                vo.setRecentChapterName(chapter.getChapterName());
            }
        }

        return vo;
    }
}
