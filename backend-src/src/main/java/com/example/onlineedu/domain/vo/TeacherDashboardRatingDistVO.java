package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 讲师数据统计面板 - 评分分布饼图 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程评分分布（饼图）")
public class TeacherDashboardRatingDistVO {

    @ApiModelProperty("星级列表：固定为 [5,4,3,2,1]")
    private List<Integer> ratings;

    @ApiModelProperty("对应评价数量（与 ratings 等长）")
    private List<Integer> counts;

    @ApiModelProperty("对应占比（%，2位小数，与 ratings 等长）")
    private List<BigDecimal> percentages;

    @ApiModelProperty("总评价数")
    private Integer total;
}
