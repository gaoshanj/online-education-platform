package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.CourseReviewDTO;
import com.example.onlineedu.domain.vo.CourseReviewLikeVO;
import com.example.onlineedu.domain.vo.CourseReviewStatListVO;
import com.example.onlineedu.domain.vo.CourseReviewStatVO;
import com.example.onlineedu.domain.vo.CourseReviewVO;

/**
 * 课程评价服务接口
 *
 * @author feng
 */
public interface CourseReviewService {

    /**
     * 提交评价（每个用户对同一课程只能评价一次）
     */
    void submitReview(Long userId, CourseReviewDTO dto);

    /**
     * 修改自己的评价
     */
    void updateReview(Long userId, Long reviewId, CourseReviewDTO dto);

    /**
     * 物理删除自己的评价（同步清理该评价的所有点赞记录）
     */
    void deleteReview(Long userId, Long reviewId);

    /**
     * 分页查询评价列表
     *
     * @param currentUserId 当前登录用户ID（用于判断 isLiked，可为 null）
     * @param courseId      课程ID
     * @param sortType      排序：latest-最新，hot-热门
     * @param rating        星级筛选 1-5（null=不筛选）
     * @param mine          是否只看自己的评价（true 时 userId 生效）
     * @param page          当前页
     * @param size          每页大小
     */
    Page<CourseReviewVO> listReviews(Long currentUserId, Long courseId,
                                     String sortType, Integer rating,
                                     Boolean mine, Integer page, Integer size);

    /**
     * 点赞/取消点赞评价（幂等切换）
     */
    CourseReviewLikeVO toggleReviewLike(Long userId, Long reviewId);

    /**
     * 获取课程评分统计
     */
    CourseReviewStatVO getReviewStat(Long currentUserId, Long courseId);

    /**
     * 管理员：分页查询单条评价列表（可按 courseId / rating 筛选）
     */
    Page<CourseReviewVO> adminListReviews(Long courseId, Integer rating, Integer page, Integer size);

    /**
     * 管理员：物理删除违规评价
     */
    void adminDeleteReview(Long reviewId);

    /**
     * 管理员：课程评价统计列表（课程维度，含讲师名称）
     *
     * @param courseName           课程名模糊搜索
     * @param teacherId            讲师ID筛选（null=不筛选）
     * @param avgScoreFilter       评分筛选：all/above4_5/above4_0/below3_5
     * @param goodReviewRateFilter 好评率筛选：all/above90/above80/below60
     * @param sortBy               排序：latestReview/reviewCount/scoreDesc/scoreAsc/goodRateDesc
     */
    Page<CourseReviewStatListVO> adminListCourseReviewStat(
            String courseName, Long teacherId,
            String avgScoreFilter, String goodReviewRateFilter,
            String sortBy, Integer page, Integer size);

    /**
     * 讲师：课程评价统计列表（仅自己的课程，不含讲师名称）
     *
     * @param teacherId            当前讲师ID
     * @param courseName           课程名模糊搜索
     * @param avgScoreFilter       评分筛选
     * @param goodReviewRateFilter 好评率筛选
     * @param sortBy               排序方式
     */
    Page<CourseReviewStatListVO> teacherListCourseReviewStat(
            Long teacherId, String courseName,
            String avgScoreFilter, String goodReviewRateFilter,
            String sortBy, Integer page, Integer size);
}
