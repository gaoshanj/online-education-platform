package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理端数据统计面板 - 累计数据 VO
 *
 * @author feng
 */
@Data
@ApiModel("管理端累计趋势（自平台启动日起）")
public class AdminDashboardCumulativeVO {

    @ApiModelProperty("日期序列（yyyy-MM-dd，从平台启动日到今天）")
    private List<String> dates;

    @ApiModelProperty("累计用户数（每日累计，单调递增）")
    private List<Integer> cumulativeUsers;

    @ApiModelProperty("累计学习次数（每日累计，单调递增）")
    private List<Integer> cumulativeLearns;
}
