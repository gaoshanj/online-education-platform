package com.example.onlineedu.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程实体类
 *
 * @author feng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class CourseEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 课程名称
	 */
	@TableField("course_name")
	private String courseName;

	/**
	 * 分类ID
	 */
	@TableField("category_id")
	private Long categoryId;

	/**
	 * 讲师ID
	 */
	@TableField("teacher_id")
	private Long teacherId;

	/**
	 * 课程封面图
	 */
	@TableField("cover_image")
	private String coverImage;

	/**
	 * 课程简介
	 */
	@TableField("description")
	private String description;



	/**
	 * 课程难度：1-入门，2-初级，3-中级，4-高级
	 */
	@TableField("level")
	private Integer level;

	/**
	 * 课程状态：0-草稿，1-待审核，2-已发布，3-审核拒绝，4-已下架
	 */
	@TableField("status")
	private Integer status;

	/**
	 * 审核拒绝原因
	 */
	@TableField("reject_reason")
	private String rejectReason;

	/**
	 * 浏览次数
	 */
	@TableField("view_count")
	private Integer viewCount;

	/**
	 * 学习人数
	 */
	@TableField("student_count")
	private Integer studentCount;

	/**
	 * 点赞数
	 */
	@TableField("like_count")
	private Integer likeCount;

	/**
	 * 收藏数
	 */
	@TableField("favorite_count")
	private Integer favoriteCount;

	/**
	 * 热度分数（定时任务计算：view×1 + student×10 + like×3 + favorite×5  + avg_score×20）
	 */
	@TableField("hot_score")
	private BigDecimal hotScore;

	/**
	 * 是否推荐：0-否，1-是
	 */
	@TableField("is_recommend")
	private Integer isRecommend;

	// ===== 评价统计冗余字段（由评价写入时维护） =====

	/**
	 * 平均评分（1位小数）
	 */
	@TableField("avg_score")
	private BigDecimal avgScore;

	/**
	 * 评价总数
	 */
	@TableField("review_count")
	private Integer reviewCount;

	/**
	 * 问题总数（冗余，由提问/删除时维护）
	 */
	@TableField("question_count")
	private Integer questionCount;

	/**
	 * 好评率：4星及以上占比×100（2位小数）
	 */
	@TableField("good_review_rate")
	private BigDecimal goodReviewRate;

	/**
	 * 最新评价时间
	 */
	@TableField("latest_review_time")
	private LocalDateTime latestReviewTime;
}
