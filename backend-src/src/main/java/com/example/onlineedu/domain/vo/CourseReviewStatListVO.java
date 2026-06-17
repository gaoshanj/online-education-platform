package com.example.onlineedu.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程评价统计列表项 VO（课程维度）
 *
 * @author feng
 */
@Data
@ApiModel("课程评价统计列表项")
public class CourseReviewStatListVO {

    @ApiModelProperty("课程ID")
    private Long courseId;

    @ApiModelProperty("课程封面")
    private String coverImage;

    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 讲师名称：管理员端返回，讲师端为 null
     */
    @ApiModelProperty("讲师名称（讲师端为 null）")
    private String teacherName;

    @ApiModelProperty("平均评分（1位小数）")
    private BigDecimal avgScore;

    @ApiModelProperty("评价总数")
    private Integer reviewCount;

    @ApiModelProperty("好评率（4星及以上占比×100，2位小数）")
    private BigDecimal goodReviewRate;

    @ApiModelProperty("最新评价时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestReviewTime;
}
