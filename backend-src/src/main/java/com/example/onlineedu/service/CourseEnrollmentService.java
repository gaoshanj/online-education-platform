package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.vo.MyLearningVO;

/**
 * 课程报名服务接口
 *
 * @author feng
 */
public interface CourseEnrollmentService {

    /**
     * 报名课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     */
    void enroll(Long userId, Long courseId);

    /**
     * 检查用户是否已报名课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 是否已报名
     */
    boolean isEnrolled(Long userId, Long courseId);

    /**
     * 获取我的学习课程列表
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页大小
     * @return 分页结果
     */
    Page<MyLearningVO> getMyLearningCourses(Long userId, Integer page, Integer size);

    /**
     * 更新最后学习时间
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     */
    void updateLastLearnTime(Long userId, Long courseId);

    /**
     * 获取最近学习的一门课程
     *
     * @param userId 用户ID
     * @return 最新的学习课程信息
     */
    MyLearningVO getRecentLearningCourse(Long userId);
}
