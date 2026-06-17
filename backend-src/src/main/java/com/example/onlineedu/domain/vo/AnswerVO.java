package com.example.onlineedu.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回答/评论视图对象
 *
 * @author feng
 */
@Data
public class AnswerVO {

    /** 回答/评论ID */
    private Long id;

    /** 问题ID */
    private Long questionId;

    /**
     * 所属一级回答ID（0 表示此条本身是一级回答；> 0 表示此条是评论）
     */
    private Long answerId;

    /** 回答/评论用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 内容 */
    private String content;

    /** 是否为讲师回答：0-否，1-是 */
    private Integer isTeacher;

    /** 被回复的目标评论ID（0 表示直接回复回答） */
    private Long targetReplyId;

    /** 被回复的目标用户ID */
    private Long targetUserId;

    /** 被回复的目标用户昵称 */
    private String targetNickname;

    /** 评论数量（仅一级回答有效） */
    private Integer replyTimes;

    /** 点赞数 */
    private Integer likeCount;

    /** 当前用户是否已点赞 */
    private Boolean isLiked;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 当前用户是否为作者 */
    private Boolean isOwner;

    /** 二级评论列表（仅问题详情接口中一级回答携带，点击展开时返回） */
    private List<AnswerVO> replies;
}
