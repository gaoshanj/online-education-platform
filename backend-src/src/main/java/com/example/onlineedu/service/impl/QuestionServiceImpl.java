package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.QuestionDTO;
import com.example.onlineedu.domain.entity.*;
import com.example.onlineedu.domain.vo.AnswerVO;
import com.example.onlineedu.domain.vo.QuestionDetailVO;
import com.example.onlineedu.domain.vo.QuestionVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.*;
import com.example.onlineedu.service.AnswerService;
import com.example.onlineedu.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问答服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void askQuestion(Long userId, QuestionDTO dto) {
        // 检查课程是否存在
        CourseEntity course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 校验是否已报名（只有报名用户才能提问）
        Long enrollCount = enrollmentMapper.selectCount(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getUserId, userId)
                        .eq(CourseEnrollmentEntity::getCourseId, dto.getCourseId()));
        if (enrollCount == 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请先报名该课程后再提问");
        }

        QuestionEntity entity = new QuestionEntity();
        entity.setCourseId(dto.getCourseId());
        entity.setUserId(userId);
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setAnswerCount(0);
        entity.setHasTeacherAnswer(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);

        questionMapper.insert(entity);

        // 维护课程问题数冗余字段
        courseMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CourseEntity>()
                        .setSql("question_count = question_count + 1")
                        .eq(CourseEntity::getId, dto.getCourseId()));

        log.info("用户{}提问成功：{}", userId, dto.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(Long userId, QuestionDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "问题ID不能为空");
        }

        QuestionEntity entity = questionMapper.selectById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "问题不存在");
        }

        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权编辑此问题");
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setUpdateTime(LocalDateTime.now());

        questionMapper.updateById(entity);
        log.info("用户{}更新问题成功：{}", userId, dto.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestion(Long userId, Long questionId) {
        QuestionEntity entity = questionMapper.selectById(questionId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "问题不存在");
        }

        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权删除此问题");
        }

        Long courseId = entity.getCourseId();
        questionMapper.deleteById(questionId);

        // 维护课程问题数冗余字段（不低于0）
        courseMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CourseEntity>()
                        .setSql("question_count = GREATEST(question_count - 1, 0)")
                        .eq(CourseEntity::getId, courseId));

        log.info("用户{}删除问题成功：{}", userId, questionId);
    }

    @Override
    public Page<QuestionVO> pageQuestionsByCourse(Long courseId, Integer page, Integer size,
                                                  Boolean onlyMine, Long currentUserId) {
        LambdaQueryWrapper<QuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(QuestionEntity::getCourseId, courseId);

        // 仅看自己的问题
        if (Boolean.TRUE.equals(onlyMine) && currentUserId != null) {
            queryWrapper.eq(QuestionEntity::getUserId, currentUserId);
        }

        queryWrapper.orderByDesc(QuestionEntity::getCreateTime);

        Page<QuestionEntity> entityPage = questionMapper.selectPage(new Page<>(page, size), queryWrapper);

        Page<QuestionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(entity -> entityToVO(entity, currentUserId))
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public QuestionDetailVO getQuestionDetail(Long questionId, Long currentUserId) {
        QuestionEntity entity = questionMapper.selectById(questionId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "问题不存在");
        }

        QuestionDetailVO vo = new QuestionDetailVO();
        BeanUtils.copyProperties(entity, vo);

        // 课程名称
        CourseEntity course = courseMapper.selectById(entity.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
        }

        // 提问者信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        vo.setIsOwner(currentUserId != null && currentUserId.equals(entity.getUserId()));

        // 一级回答列表（不含二级评论）
        List<AnswerVO> answers = answerService.getAnswersByQuestion(questionId, currentUserId);
        vo.setAnswers(answers);

        return vo;
    }

    @Override
    public Page<QuestionVO> pageAllQuestions(Integer page, Integer size, Long courseId, Integer hasTeacherAnswer) {
        LambdaQueryWrapper<QuestionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(courseId != null, QuestionEntity::getCourseId, courseId)
                .eq(hasTeacherAnswer != null, QuestionEntity::getHasTeacherAnswer, hasTeacherAnswer)
                .orderByDesc(QuestionEntity::getCreateTime);

        Page<QuestionEntity> entityPage = questionMapper.selectPage(new Page<>(page, size), queryWrapper);

        Page<QuestionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(entity -> entityToVO(entity, null))
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestionByAdmin(Long questionId) {
        QuestionEntity entity = questionMapper.selectById(questionId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "问题不存在");
        }

        questionMapper.deleteById(questionId);

        // 级联删除该问题下的所有回答和评论（含二级评论，answer_id 均属于该 questionId）
        answerMapper.delete(
                new LambdaQueryWrapper<AnswerEntity>()
                        .eq(AnswerEntity::getQuestionId, questionId));

        log.info("管理员删除问题成功：{}（已级联删除所有回答与评论）", questionId);
    }

    @Override
    public Page<QuestionVO> pageQuestionsByTeacher(Integer page, Integer size, Long courseId,
                                                   Integer hasTeacherAnswer, Long teacherId) {
        LambdaQueryWrapper<QuestionEntity> queryWrapper = new LambdaQueryWrapper<>();

        // 若指定了 courseId，先校验是否属于该讲师
        if (courseId != null) {
            CourseEntity course = courseMapper.selectById(courseId);
            if (course == null || !course.getTeacherId().equals(teacherId)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "无权查看此课程的问题");
            }
            queryWrapper.eq(QuestionEntity::getCourseId, courseId);
        } else {
            // 查询该讲师所有课程的问题
            List<Long> courseIds = courseMapper.selectList(
                    new LambdaQueryWrapper<CourseEntity>()
                            .eq(CourseEntity::getTeacherId, teacherId)
                            .select(CourseEntity::getId)
            ).stream().map(CourseEntity::getId).collect(Collectors.toList());

            if (courseIds.isEmpty()) {
                return new Page<>(page, size, 0);
            }
            queryWrapper.in(QuestionEntity::getCourseId, courseIds);
        }

        queryWrapper.eq(hasTeacherAnswer != null, QuestionEntity::getHasTeacherAnswer, hasTeacherAnswer)
                .orderByDesc(QuestionEntity::getCreateTime);

        Page<QuestionEntity> entityPage = questionMapper.selectPage(new Page<>(page, size), queryWrapper);

        Page<QuestionVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(entity -> entityToVO(entity, null))
                .collect(Collectors.toList()));

        return voPage;
    }

    /**
     * Entity 转 VO，并填充最新回答预览
     */
    private QuestionVO entityToVO(QuestionEntity entity, Long currentUserId) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(entity, vo);

        // 课程名称
        CourseEntity course = courseMapper.selectById(entity.getCourseId());
        if (course != null) {
            vo.setCourseName(course.getCourseName());
        }

        // 提问者信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 最新回答预览
        if (entity.getLatestAnswerId() != null) {
            AnswerEntity latestAnswer = answerMapper.selectById(entity.getLatestAnswerId());
            if (latestAnswer != null) {
                vo.setLatestAnswerContent(latestAnswer.getContent());
                UserEntity answerUser = userMapper.selectById(latestAnswer.getUserId());
                if (answerUser != null) {
                    vo.setLatestAnswerNickname(answerUser.getNickname());
                }
            }
        }

        vo.setIsOwner(currentUserId != null && currentUserId.equals(entity.getUserId()));

        return vo;
    }
}
