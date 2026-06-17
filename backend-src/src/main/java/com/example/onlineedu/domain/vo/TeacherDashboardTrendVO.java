package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 讲师数据统计面板 - 折线图趋势 VO
 *
 * @author feng
 */
@Data
@ApiModel("每日学员趋势（折线图）")
public class TeacherDashboardTrendVO {

    @ApiModelProperty("日期序列（yyyy-MM-dd，从早到晚）")
    private List<String> dates;

    @ApiModelProperty("每日新增学员数（与 dates 等长，缺失日期为 0）")
    private List<Integer> newStudents;

    @ApiModelProperty("每日活跃学习人数（与 dates 等长，缺失日期为 0）")
    private List<Integer> activeStudents;
}
