package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问题列表视图对象
 *
 * @author feng
 */
@Data
public class QuestionVO {

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

    /** 最新回答内容（用于列表预览） */
    private String latestAnswerContent;

    /** 最新回答者昵称 */
    private String latestAnswerNickname;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 当前用户是否为提问者 */
    private Boolean isOwner;
}
