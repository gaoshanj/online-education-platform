package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理端数据统计面板 - 全平台总览 VO
 *
 * @author feng
 */
@Data
@ApiModel("管理端数据统计总览")
public class AdminDashboardOverviewVO {

    @ApiModelProperty("用户总数")
    private Integer userCount;

    @ApiModelProperty("讲师总数（已审核通过）")
    private Integer teacherCount;

    @ApiModelProperty("课程总数（含所有状态）")
    private Integer courseCount;

    @ApiModelProperty("学习人数（报名去重总数）")
    private Integer studentCount;

    @ApiModelProperty("评价总数")
    private Integer reviewCount;

    @ApiModelProperty("全平台加权平均评分（1位小数）")
    private BigDecimal avgScore;
}
