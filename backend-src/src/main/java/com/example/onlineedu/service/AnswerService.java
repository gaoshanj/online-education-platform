package com.example.onlineedu.service;

import com.example.onlineedu.domain.dto.AnswerDTO;
import com.example.onlineedu.domain.vo.AnswerVO;

import java.util.List;

/**
 * 回答/评论服务接口
 *
 * @author feng
 */
public interface AnswerService {

    /**
     * 提交回答或评论
     * - dto.answerId 为 null 或 0：提交一级回答
     * - dto.answerId > 0：提交二级评论
     *
     * @param userId    用户ID
     * @param dto       回答/评论信息
     * @param isTeacher 是否为讲师
     */
    void submitAnswer(Long userId, AnswerDTO dto, Boolean isTeacher);

    /**
     * 删除回答或评论（仅本人可操作）
     *
     * @param userId   用户ID
     * @param answerId 回答/评论ID
     */
    void deleteAnswer(Long userId, Long answerId);

    /**
     * 查询问题的一级回答列表（不含二级评论）
     *
     * @param questionId    问题ID
     * @param currentUserId 当前用户ID（可为null）
     * @return 一级回答列表
     */
    List<AnswerVO> getAnswersByQuestion(Long questionId, Long currentUserId);

    /**
     * 获取某一级回答下的二级评论列表
     *
     * @param answerId      一级回答ID
     * @param currentUserId 当前用户ID（可为null）
     * @return 二级评论列表
     */
    List<AnswerVO> getAnswerReplies(Long answerId, Long currentUserId);

    /**
     * 点赞或取消点赞（toggle）
     *
     * @param userId   用户ID
     * @param answerId 回答/评论ID
     * @return true-已点赞，false-已取消
     */
    boolean toggleLike(Long userId, Long answerId);

    /**
     * 管理端：删除回答或评论
     *
     * @param answerId 回答/评论ID
     */
    void deleteAnswerByAdmin(Long answerId);
}
