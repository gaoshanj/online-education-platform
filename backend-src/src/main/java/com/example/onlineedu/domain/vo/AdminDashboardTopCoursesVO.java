package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 管理端数据统计面板 - Top10 排行 VO
 *
 * @author feng
 */
@Data
@ApiModel("管理端 Top10 课程排行")
public class AdminDashboardTopCoursesVO {

    @ApiModelProperty("排序指标：hot-热度 / studentCount-学习人数")
    private String rankBy;

    @ApiModelProperty("Top10 列表")
    private List<CourseRankItemVO> items;
}
