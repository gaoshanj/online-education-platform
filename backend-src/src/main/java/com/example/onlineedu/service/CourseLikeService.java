package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.vo.CourseLikeVO;
import com.example.onlineedu.domain.vo.CourseVO;

/**
 * 课程点赞服务接口
 *
 * @author feng
 */
public interface CourseLikeService {

    /**
     * 切换点赞状态（点赞/取消点赞），幂等操作
     *
     * @param userId   当前登录用户ID
     * @param courseId 课程ID
     * @return 操作后的点赞状态与最新点赞数
     */
    CourseLikeVO toggleLike(Long userId, Long courseId);

    /**
     * 查询当前用户是否已点赞某课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return true=已点赞
     */
    boolean isLiked(Long userId, Long courseId);

    /**
     * 分页查询用户点赞过的课程列表
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页大小
     * @return 分页课程列表（结构与课程列表接口一致，可直接跳转）
     */
    Page<CourseVO> getLikedCourses(Long userId, Integer page, Integer size);
}
