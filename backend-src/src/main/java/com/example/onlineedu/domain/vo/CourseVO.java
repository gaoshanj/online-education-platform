package com.example.onlineedu.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息 VO
 *
 * @author feng
 */
@Data
@ApiModel("课程信息响应")
public class CourseVO {

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private Long id;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 分类ID
     */
    @ApiModelProperty("分类ID")
    private Long categoryId;

    /**
     * 分类名称
     */
    @ApiModelProperty("分类名称")
    private String categoryName;

    /**
     * 讲师ID
     */
    @ApiModelProperty("讲师ID")
    private Long teacherId;

    /**
     * 讲师名称
     */
    @ApiModelProperty("讲师名称")
    private String teacherName;

    /**
     * 课程封面图
     */
    @ApiModelProperty("课程封面图")
    private String coverImage;

    /**
     * 课程简介
     */
    @ApiModelProperty("课程简介")
    private String description;

    /**
     * 课程难度
     */
    @ApiModelProperty("课程难度")
    private Integer level;

    /**
     * 课程状态：0-草稿，1-待审核，2-已发布，3-审核拒绝，4-已下架
     */
    @ApiModelProperty("课程状态")
    private Integer status;

    /**
     * 审核拒绝原因（status=3 时有值，讲师可据此修改后重新提交）
     */
    @ApiModelProperty("审核拒绝原因")
    private String rejectReason;

    /**
     * 浏览次数
     */
    @ApiModelProperty("浏览次数")
    private Integer viewCount;

    /**
     * 学习人数
     */
    @ApiModelProperty("学习人数")
    private Integer studentCount;

    /**
     * 点赞数
     */
    @ApiModelProperty("点赞数")
    private Integer likeCount;

    /**
     * 收藏数
     */
    @ApiModelProperty("收藏数")
    private Integer favoriteCount;

    /**
     * 是否热门
     */
    @ApiModelProperty("是否热门")
    private Integer isHot;

    /**
     * 是否推荐
     */
    @ApiModelProperty("是否推荐")
    private Integer isRecommend;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    /**
     * 当前用户是否已报名
     */
    @ApiModelProperty("是否已报名")
    private Boolean isEnrolled;

    /**
     * 最后学习时间
     */
    @ApiModelProperty("最后学习时间")
    private LocalDateTime lastLearnTime;

    /**
     * 学习进度百分比（0-100）
     */
    @ApiModelProperty("学习进度百分比")
    private Integer learningProgress;

    /**
     * 当前用户是否已点赞（未登录时为 null）
     */
    @ApiModelProperty("当前用户是否已点赞")
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏（未登录时为 null）
     */
    @ApiModelProperty("当前用户是否已收藏")
    private Boolean isFavorited;

    /**
     * 最近学习的章节ID
     */
    @ApiModelProperty("最近学习的章节ID")
    private Long recentChapterId;

    /**
     * 最近学习的章节名称
     */
    @ApiModelProperty("最近学习的章节名称")
    private String recentChapterName;

    /**
     * 平均评分（1位小数）
     */
    @ApiModelProperty("平均评分（1位小数）")
    private BigDecimal avgScore;

    /**
     * 评价总数
     */
    @ApiModelProperty("评价总数")
    private Integer reviewCount;

    /**
     * 好评率：4星及以上占比×100（2位小数）
     */
    @ApiModelProperty("好评率：4星及以上占比×100（2位小数）")
    private BigDecimal goodReviewRate;

    /**
     * 最新评价时间
     */
    @ApiModelProperty("最新评价时间")
    private LocalDateTime latestReviewTime;
}
