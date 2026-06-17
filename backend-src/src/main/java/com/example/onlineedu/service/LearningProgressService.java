package com.example.onlineedu.service;

import com.example.onlineedu.domain.dto.LearningProgressDTO;
import com.example.onlineedu.domain.vo.ChapterProgressVO;

import java.util.List;

/**
 * 学习进度服务接口
 *
 * @author feng
 */
public interface LearningProgressService {

    /**
     * 保存学习进度
     *
     * @param userId 用户ID
     * @param dto    学习进度信息
     */
    void saveProgress(Long userId, LearningProgressDTO dto);

    /**
     * 获取章节学习进度
     *
     * @param userId    用户ID
     * @param courseId  课程ID
     * @param chapterId 章节ID
     * @return 章节学习进度
     */
    ChapterProgressVO getProgress(Long userId, Long courseId, Long chapterId);

    /**
     * 获取课程所有章节的学习进度
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 章节学习进度列表
     */
    List<ChapterProgressVO> getCourseProgress(Long userId, Long courseId);

    /**
     * 计算课程整体学习进度百分比
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 学习进度百分比（0-100）
     */
    Integer calculateCourseProgress(Long userId, Long courseId);
}
