package com.example.onlineedu.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 讲师端学员列表项 VO（用户维度）
 *
 * @author feng
 */
@Data
@ApiModel("学员列表项")
public class TeacherStudentListVO {

    @ApiModelProperty("学员用户ID")
    private Long userId;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("加入时间（首次报名该讲师课程）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    @ApiModelProperty("学习课程数")
    private Integer courseCount;

    @ApiModelProperty("总学习时长（秒）")
    private Integer totalDuration;

    @ApiModelProperty("平均完成率（%，2位小数）")
    private BigDecimal avgCompletionRate;

    @ApiModelProperty("最近学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestLearnTime;
}
