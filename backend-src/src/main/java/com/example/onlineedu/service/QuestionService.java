package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.QuestionDTO;
import com.example.onlineedu.domain.vo.QuestionDetailVO;
import com.example.onlineedu.domain.vo.QuestionVO;

/**
 * 问答服务接口
 *
 * @author feng
 */
public interface QuestionService {

    /**
     * 学生提问
     *
     * @param userId 用户ID
     * @param dto    问题信息
     */
    void askQuestion(Long userId, QuestionDTO dto);

    /**
     * 更新问题（仅提问者可操作）
     *
     * @param userId 用户ID
     * @param dto    问题信息
     */
    void updateQuestion(Long userId, QuestionDTO dto);

    /**
     * 删除问题（仅提问者可操作）
     *
     * @param userId     用户ID
     * @param questionId 问题ID
     */
    void deleteQuestion(Long userId, Long questionId);

    /**
     * 分页查询课程问题列表
     *
     * @param courseId      课程ID
     * @param page          当前页
     * @param size          每页大小
     * @param onlyMine      是否只看自己的问题
     * @param currentUserId 当前用户ID（可为 null）
     * @return 分页结果
     */
    Page<QuestionVO> pageQuestionsByCourse(Long courseId, Integer page, Integer size,
                                           Boolean onlyMine, Long currentUserId);

    /**
     * 查询问题详情
     *
     * @param questionId    问题ID
     * @param currentUserId 当前用户ID（可为null）
     * @return 问题详情
     */
    QuestionDetailVO getQuestionDetail(Long questionId, Long currentUserId);

    /**
     * 管理端：分页查询所有问题
     *
     * @param page             当前页
     * @param size             每页大小
     * @param courseId         课程ID（可选）
     * @param hasTeacherAnswer 是否有讲师回答（可选）
     * @return 分页结果
     */
    Page<QuestionVO> pageAllQuestions(Integer page, Integer size, Long courseId, Integer hasTeacherAnswer);

    /**
     * 管理端：删除问题
     *
     * @param questionId 问题ID
     */
    void deleteQuestionByAdmin(Long questionId);

    /**
     * 讲师端：分页查询自己课程的问题
     *
     * @param page             当前页
     * @param size             每页大小
     * @param courseId         课程ID（可选，为空则查询该讲师所有课程的问题）
     * @param hasTeacherAnswer 是否有讲师回答（可选）
     * @param teacherId        讲师用户ID
     * @return 分页结果
     */
    Page<QuestionVO> pageQuestionsByTeacher(Integer page, Integer size, Long courseId,
                                            Integer hasTeacherAnswer, Long teacherId);
}

