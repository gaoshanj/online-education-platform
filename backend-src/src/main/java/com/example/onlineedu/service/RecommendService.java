package com.example.onlineedu.service;

import com.example.onlineedu.domain.vo.CourseVO;

import java.util.List;

/**
 * 课程推荐服务接口
 *
 * @author feng
 */
public interface RecommendService {

    /**
     * 热门课程推荐
     * 按综合热度分（学习人数×0.4 + 点赞数×0.3 + 收藏数×0.3）降序
     *
     * @param limit 返回数量
     * @return 课程列表
     */
    List<CourseVO> getHotRecommend(int limit);

    /**
     * 最新上线课程
     * 按发布时间降序
     *
     * @param limit 返回数量
     * @return 课程列表
     */
    List<CourseVO> getLatestRecommend(int limit);

    /**
     * 精品课程推荐
     * 查询 is_recommend=1 的课程，按热度降序
     *
     * @param limit 返回数量
     * @return 课程列表
     */
    List<CourseVO> getFeaturedRecommend(int limit);

    /**
     * 个性化推荐（ItemCF 混合策略）
     * - 已登录且有行为数据：Item-based CF + 热度分 + 新课时效加分
     * - 冷启动（行为数 < 1）或未登录：降级为精品课程
     *
     * @param userId 用户ID（未登录传 null）
     * @param limit  返回数量
     * @return 课程列表
     */
    List<CourseVO> getPersonalRecommend(Long userId, int limit);
}
