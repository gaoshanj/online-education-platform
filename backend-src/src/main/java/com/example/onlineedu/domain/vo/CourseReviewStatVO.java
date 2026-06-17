package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 课程评分统计 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程评分统计")
public class CourseReviewStatVO {

    @ApiModelProperty("平均评分（保留1位小数）")
    private BigDecimal avgScore;

    @ApiModelProperty("总评价数")
    private Long totalReviews;

    /**
     * 评分分布：key=星级(1-5)，value=该星级的评价数量
     */
    @ApiModelProperty("评分分布 {1:数量, 2:数量, 3:数量, 4:数量, 5:数量}")
    private Map<Integer, Long> ratingDistribution;

    @ApiModelProperty("当前用户是否已评价")
    private Boolean userHasReviewed;
}
