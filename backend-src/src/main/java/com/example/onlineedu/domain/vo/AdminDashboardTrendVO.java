package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理端数据统计面板 - 折线图趋势 VO（单指标）
 *
 * @author feng
 */
@Data
@ApiModel("管理端折线图趋势（单指标）")
public class AdminDashboardTrendVO {

    @ApiModelProperty("当前指标：newUser / activeLearn / newCourse")
    private String metric;

    @ApiModelProperty("日期序列（yyyy-MM-dd，从早到晚）")
    private List<String> dates;

    @ApiModelProperty("对应指标数量（与 dates 等长，缺失日期为 0）")
    private List<Integer> values;
}
