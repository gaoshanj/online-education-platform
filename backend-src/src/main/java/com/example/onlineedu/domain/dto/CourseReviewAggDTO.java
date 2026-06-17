package com.example.onlineedu.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程评价统计聚合查询结果（供 refreshCourseReviewStat 使用）
 *
 * @author feng
 */
@Data
public class CourseReviewAggDTO {

    /** 评价总数 */
    private Long reviewCount;

    /** 平均评分 */
    private BigDecimal avgScore;

    /** 好评数（rating >= 4） */
    private Long goodReviewCount;

    /** 最新评价时间 */
    private LocalDateTime latestReviewTime;
}
