package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 讲师数据统计面板 - 总览 VO
 *
 * @author feng
 */
@Data
@ApiModel("讲师数据统计总览")
public class TeacherDashboardOverviewVO {

    @ApiModelProperty("课程总数")
    private Integer courseCount;

    @ApiModelProperty("学习人数（各课 student_count 累加）")
    private Integer studentCount;

    @ApiModelProperty("点赞总数")
    private Integer likeCount;

    @ApiModelProperty("收藏总数")
    private Integer favoriteCount;

    @ApiModelProperty("评价总数")
    private Integer reviewCount;

    @ApiModelProperty("平均评分（1位小数，无评价为 null）")
    private BigDecimal avgScore;
}
