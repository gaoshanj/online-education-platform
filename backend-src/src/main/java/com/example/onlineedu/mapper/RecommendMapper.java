package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.domain.dto.UserCourseScore;
import com.example.onlineedu.domain.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 推荐系统 Mapper 接口
 * 简单查询使用 MyBatis-Plus，复杂多表查询使用 XML
 *
 * @author feng
 */
@Mapper
public interface RecommendMapper {

    // ========== MyBatis-Plus 简单查询 ==========

    /**
     * 注入的 baseMapper，用于 MyBatis-Plus 查询
     * 实际通过 CourseMapper 代理实现，此处直接声明静态工具方法供 Impl 使用
     */

    // ========== XML 复杂查询 ==========

    /**
     * 查询目标用户对各课程的加权行为评分
     * 聚合：报名(5) + 完成课程（按比例,总分:4) + 收藏(3) + 点赞(2)
     *
     * @param userId 目标用户ID
     * @return 用户已交互课程的评分列表
     */
    List<UserCourseScore> getUserBehaviorScores(@Param("userId") Long userId);

    /**
     * 查询全量用户-课程行为加权评分
     * 用于构建 ItemCF 课程-课程余弦相似度矩阵
     *
     * @return 所有用户对所有课程的评分列表
     */
    List<UserCourseScore> getAllUserCourseScores();

    /**
     * 查询用户已报名的课程ID列表
     *
     * @param userId 用户ID
     * @return 已报名课程ID列表
     */
    List<Long> getEnrolledCourseIds(@Param("userId") Long userId);
}
