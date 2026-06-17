package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 课程排行榜单项 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程排行单项")
public class CourseRankItemVO {

    @ApiModelProperty("课程ID")
    private Long courseId;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("指标值（学习人数 or 平均评分）")
    private BigDecimal value;
}
