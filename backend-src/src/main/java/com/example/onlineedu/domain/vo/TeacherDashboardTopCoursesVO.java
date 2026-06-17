package com.example.onlineedu.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 讲师数据统计面板 - Top10 排行 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程 Top10 排行")
public class TeacherDashboardTopCoursesVO {

    @ApiModelProperty("学习人数 Top10（按 student_count 降序）")
    private List<CourseRankItemVO> studentRank;

    @ApiModelProperty("评分 Top10（按 avg_score 降序，仅有评价的课程）")
    private List<CourseRankItemVO> scoreRank;
}
