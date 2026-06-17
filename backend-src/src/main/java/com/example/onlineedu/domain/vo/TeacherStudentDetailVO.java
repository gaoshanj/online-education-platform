package com.example.onlineedu.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 讲师端学员详情 VO（课程维度）
 *
 * @author feng
 */
@Data
@ApiModel("学员学习课程详情项")
public class TeacherStudentDetailVO {

    @ApiModelProperty("课程ID")
    private Long courseId;

    @ApiModelProperty("课程封面")
    private String coverImage;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("学习进度（%，2位小数）")
    private BigDecimal completionRate;

    @ApiModelProperty("已完成章节数")
    private Integer completedChapters;

    @ApiModelProperty("总章节数（叶子章节）")
    private Integer totalChapters;

    @ApiModelProperty("学习时长（秒）")
    private Integer duration;

    @ApiModelProperty("报名时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime enrollTime;

    @ApiModelProperty("最后学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLearnTime;

    @ApiModelProperty("是否已完成（进度100%）")
    private Boolean isCompleted;
}
