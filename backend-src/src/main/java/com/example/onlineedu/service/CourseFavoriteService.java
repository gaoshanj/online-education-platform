package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.vo.CourseFavoriteVO;
import com.example.onlineedu.domain.vo.CourseVO;

/**
 * 课程收藏服务接口
 *
 * @author feng
 */
public interface CourseFavoriteService {

    /**
     * 切换收藏状态（收藏/取消收藏），幂等操作
     *
     * @param userId   当前登录用户ID
     * @param courseId 课程ID
     * @return 操作后的收藏状态与最新收藏数
     */
    CourseFavoriteVO toggleFavorite(Long userId, Long courseId);

    /**
     * 查询当前用户是否已收藏某课程
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return true=已收藏
     */
    boolean isFavorited(Long userId, Long courseId);

    /**
     * 分页查询用户收藏过的课程列表
     *
     * @param userId 用户ID
     * @param page   当前页
     * @param size   每页大小
     * @return 分页课程列表（结构与课程列表接口一致，可直接跳转）
     */
    Page<CourseVO> getFavoritedCourses(Long userId, Integer page, Integer size);
}
