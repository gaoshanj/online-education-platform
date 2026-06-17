package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.LearningProgressDTO;
import com.example.onlineedu.domain.entity.CourseChapterEntity;
import com.example.onlineedu.domain.entity.LearningProgressEntity;
import com.example.onlineedu.domain.vo.ChapterProgressVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseChapterMapper;
import com.example.onlineedu.mapper.LearningProgressMapper;
import com.example.onlineedu.service.CourseEnrollmentService;
import com.example.onlineedu.service.LearningProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学习进度服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class LearningProgressServiceImpl implements LearningProgressService {

    @Autowired
    private LearningProgressMapper progressMapper;

    @Autowired
    private CourseChapterMapper chapterMapper;

    @Autowired
    @Lazy
    private CourseEnrollmentService enrollmentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProgress(Long userId, LearningProgressDTO dto) {
        // 检查章节是否存在
        CourseChapterEntity chapter = chapterMapper.selectById(dto.getChapterId());
        if (chapter == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "章节不存在");
        }

        // 检查章节是否属于该课程
        if (!chapter.getCourseId().equals(dto.getCourseId())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "章节不属于该课程");
        }

        // 查询是否已有进度记录
        LambdaQueryWrapper<LearningProgressEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, dto.getCourseId())
                .eq(LearningProgressEntity::getChapterId, dto.getChapterId());

        LearningProgressEntity progress = progressMapper.selectOne(queryWrapper);

        if (progress == null) {
            // 创建新的进度记录
            progress = new LearningProgressEntity();
            progress.setUserId(userId);
            progress.setCourseId(dto.getCourseId());
            progress.setChapterId(dto.getChapterId());
            progress.setLastPosition(dto.getLastPosition());
            progress.setMaxPosition(dto.getLastPosition());
            progress.setDuration(dto.getDuration());
            
            // 判断是否完成（播放位置接近视频总时长）
            if (chapter.getVideoDuration() != null && chapter.getVideoDuration() > 0) {
                int threshold = (int) (chapter.getVideoDuration() * 0.9); // 播放到90%算完成
                progress.setIsCompleted(dto.getLastPosition() >= threshold ? 1 : 0);
            } else {
                progress.setIsCompleted(0);
            }
            
            progress.setCreateTime(LocalDateTime.now());
            progress.setUpdateTime(LocalDateTime.now());
            progressMapper.insert(progress);
        } else {
            // 更新进度记录（复习时会更新最后的播放位置）
            progress.setLastPosition(dto.getLastPosition());
            
            // 更新历史最高播放位置
            if (progress.getMaxPosition() == null) {
                progress.setMaxPosition(dto.getLastPosition());
            } else {
                progress.setMaxPosition(Math.max(progress.getMaxPosition(), dto.getLastPosition()));
            }

            // 前端传递的是本次观看的增量时长
            progress.setDuration(progress.getDuration() + dto.getDuration());
            // 判断是否完成
            if (chapter.getVideoDuration() != null && chapter.getVideoDuration() > 0) {
                int threshold = (int) (chapter.getVideoDuration() * 0.9);
                // 只有当状态为未完成时，才去判断并更新是否完成。
                // 避免用户学完后“二刷”从头看，导致 isCompleted 被再次改成 0。
                if (progress.getIsCompleted() == null || progress.getIsCompleted() == 0) {
                    // 使用 maxPosition 作为考核成绩
                    progress.setIsCompleted(progress.getMaxPosition() >= threshold ? 1 : 0);
                }
            }
            
            progress.setUpdateTime(LocalDateTime.now());
            progressMapper.updateById(progress);
        }

        // 更新最后学习时间
        enrollmentService.updateLastLearnTime(userId, dto.getCourseId());

        log.info("保存学习进度成功：用户{}, 课程{}, 章节{}, 位置{}", 
                userId, dto.getCourseId(), dto.getChapterId(), dto.getLastPosition());
    }

    @Override
    public ChapterProgressVO getProgress(Long userId, Long courseId, Long chapterId) {
        // 查询章节信息
        CourseChapterEntity chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "章节不存在");
        }

        ChapterProgressVO vo = new ChapterProgressVO();
        vo.setChapterId(chapterId);
        vo.setChapterName(chapter.getChapterName());
        vo.setVideoDuration(chapter.getVideoDuration());

        // 查询学习进度
        LambdaQueryWrapper<LearningProgressEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, courseId)
                .eq(LearningProgressEntity::getChapterId, chapterId);

        LearningProgressEntity progress = progressMapper.selectOne(queryWrapper);
        if (progress != null) {
            vo.setLastPosition(progress.getLastPosition());
            vo.setMaxPosition(progress.getMaxPosition() != null ? progress.getMaxPosition() : progress.getLastPosition());
            vo.setDuration(progress.getDuration());
            vo.setIsCompleted(progress.getIsCompleted());
        } else {
            vo.setLastPosition(0);
            vo.setMaxPosition(0);
            vo.setDuration(0);
            vo.setIsCompleted(0);
        }

        return vo;
    }

    @Override
    public List<ChapterProgressVO> getCourseProgress(Long userId, Long courseId) {
        // 查询课程所有章节
        LambdaQueryWrapper<CourseChapterEntity> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(CourseChapterEntity::getCourseId, courseId)
                .orderByAsc(CourseChapterEntity::getSortOrder);
        List<CourseChapterEntity> chapters = chapterMapper.selectList(chapterQuery);

        // 查询用户的学习进度
        LambdaQueryWrapper<LearningProgressEntity> progressQuery = new LambdaQueryWrapper<>();
        progressQuery.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, courseId);
        List<LearningProgressEntity> progressList = progressMapper.selectList(progressQuery);

        // 转换为VO
        return chapters.stream().map(chapter -> {
            ChapterProgressVO vo = new ChapterProgressVO();
            vo.setChapterId(chapter.getId());
            vo.setChapterName(chapter.getChapterName());
            vo.setVideoDuration(chapter.getVideoDuration());

            // 查找对应的进度记录
            LearningProgressEntity progress = progressList.stream()
                    .filter(p -> p.getChapterId().equals(chapter.getId()))
                    .findFirst()
                    .orElse(null);

            if (progress != null) {
                vo.setLastPosition(progress.getLastPosition());
                vo.setMaxPosition(progress.getMaxPosition() != null ? progress.getMaxPosition() : progress.getLastPosition());
                vo.setDuration(progress.getDuration());
                vo.setIsCompleted(progress.getIsCompleted());
            } else {
                vo.setLastPosition(0);
                vo.setMaxPosition(0);
                vo.setDuration(0);
                vo.setIsCompleted(0);
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer calculateCourseProgress(Long userId, Long courseId) {
        // 查询课程所有章节（只统计有视频的章节）
        LambdaQueryWrapper<CourseChapterEntity> chapterQuery = new LambdaQueryWrapper<>();
        chapterQuery.eq(CourseChapterEntity::getCourseId, courseId)
                .isNotNull(CourseChapterEntity::getVideoUrl)
                .ne(CourseChapterEntity::getVideoUrl, "");
        long totalChapters = chapterMapper.selectCount(chapterQuery);

        if (totalChapters == 0) {
            return 0;
        }

        // 查询已完成的章节数
        LambdaQueryWrapper<LearningProgressEntity> progressQuery = new LambdaQueryWrapper<>();
        progressQuery.eq(LearningProgressEntity::getUserId, userId)
                .eq(LearningProgressEntity::getCourseId, courseId)
                .eq(LearningProgressEntity::getIsCompleted, 1);
        long completedChapters = progressMapper.selectCount(progressQuery);

        // 计算百分比
        return (int) ((completedChapters * 100) / totalChapters);
    }
}
