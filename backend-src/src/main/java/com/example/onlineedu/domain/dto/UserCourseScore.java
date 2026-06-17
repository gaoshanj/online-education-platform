package com.example.onlineedu.domain.dto;

import lombok.Data;

/**
 * 用户-课程行为评分数据对象
 * 用于 ItemCF 构建用户-课程评分矩阵
 *
 * @author feng
 */
@Data
public class UserCourseScore {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 加权行为总评分
     * 报名=5 / 完成课程（按比例）=4 / 收藏=3 / 点赞=2
     */
    private Double totalScore;
}
