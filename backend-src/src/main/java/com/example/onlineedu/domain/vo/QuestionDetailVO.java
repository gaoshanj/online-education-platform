package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问题详情视图对象
 *
 * @author feng
 */
@Data
public class QuestionDetailVO {

    /** 问题ID */
    private Long id;

    /** 课程ID */
    private Long courseId;

    /** 课程名称 */
    private String courseName;

    /** 提问用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 问题标题 */
    private String title;

    /** 问题内容 */
    private String content;

    /** 回答数量（不含评论） */
    private Integer answerCount;

    /** 是否有讲师回答：0-否，1-是 */
    private Integer hasTeacherAnswer;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 一级回答列表（不含二级评论） */
    private List<AnswerVO> answers;

    /** 当前用户是否为提问者 */
    private Boolean isOwner;
}
