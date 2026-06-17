package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.AnswerDTO;
import com.example.onlineedu.domain.entity.AnswerEntity;
import com.example.onlineedu.domain.entity.AnswerLikeEntity;
import com.example.onlineedu.domain.entity.QuestionEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.vo.AnswerVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.AnswerLikeMapper;
import com.example.onlineedu.mapper.AnswerMapper;
import com.example.onlineedu.mapper.QuestionMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.service.AnswerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.onlineedu.common.ErrorCode.PARAM_ERROR;

/**
 * 回答/评论服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AnswerLikeMapper answerLikeMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAnswer(Long userId, AnswerDTO dto, Boolean isTeacher) {
        // 检查问题是否存在
        QuestionEntity question = questionMapper.selectById(dto.getQuestionId());
        if (question == null) {
            throw new BusinessException(PARAM_ERROR, "问题不存在");
        }

        boolean isReply = dto.getAnswerId() != null && dto.getAnswerId() > 0;

        if (isReply) {
            // 二级评论：检查所属一级回答是否存在
            AnswerEntity parentAnswer = answerMapper.selectById(dto.getAnswerId());
            if (parentAnswer == null) {
                throw new BusinessException(PARAM_ERROR, "所属回答不存在");
            }
            //判断一级回答是否属于当前问题
            if(!parentAnswer.getQuestionId().equals(dto.getQuestionId())){
                throw new BusinessException(PARAM_ERROR,"一级回答（answerId）不属于该问题");
            }
        }

        // 构建实体
        AnswerEntity entity = new AnswerEntity();
        entity.setQuestionId(dto.getQuestionId());
        entity.setAnswerId(isReply ? dto.getAnswerId() : 0L);
        entity.setUserId(userId);
        entity.setContent(dto.getContent());
        entity.setIsTeacher(Boolean.TRUE.equals(isTeacher) ? 1 : 0);
        entity.setTargetReplyId(dto.getTargetReplyId() != null ? dto.getTargetReplyId() : 0L);
        entity.setTargetUserId(dto.getTargetUserId() != null ? dto.getTargetUserId() : 0L);
        entity.setReplyTimes(0);
        entity.setLikeCount(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);

        answerMapper.insert(entity);

        if (isReply) {
            // 更新父回答的 reply_times
            AnswerEntity parentAnswer = answerMapper.selectById(dto.getAnswerId());
            if (parentAnswer != null) {
                parentAnswer.setReplyTimes(parentAnswer.getReplyTimes() + 1);
                parentAnswer.setUpdateTime(LocalDateTime.now());
                answerMapper.updateById(parentAnswer);
            }
        } else {
            // 一级回答：更新问题的 answer_count、latest_answer_id、has_teacher_answer
            question.setAnswerCount(question.getAnswerCount() + 1);
            question.setLatestAnswerId(entity.getId());
            question.setUpdateTime(LocalDateTime.now());
            if (Boolean.TRUE.equals(isTeacher) && question.getHasTeacherAnswer() == 0) {
                question.setHasTeacherAnswer(1);
            }
            questionMapper.updateById(question);
        }

        log.info("用户{}{}问题{}成功", userId, isReply ? "评论" : "回答", dto.getQuestionId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnswer(Long userId, Long answerId) {
        AnswerEntity entity = answerMapper.selectById(answerId);
        if (entity == null) {
            throw new BusinessException(PARAM_ERROR, "回答/评论不存在");
        }

        if (!entity.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权删除此回答/评论");
        }

        answerMapper.deleteById(answerId);

        // 若为二级评论，父回答 reply_times -1
        if (entity.getAnswerId() != null && entity.getAnswerId() > 0) {
            AnswerEntity parentAnswer = answerMapper.selectById(entity.getAnswerId());
            if (parentAnswer != null && parentAnswer.getReplyTimes() > 0) {
                parentAnswer.setReplyTimes(parentAnswer.getReplyTimes() - 1);
                parentAnswer.setUpdateTime(LocalDateTime.now());
                answerMapper.updateById(parentAnswer);
            }
        } else {
            // 一级回答被删除，问题 answer_count -1
            QuestionEntity question = questionMapper.selectById(entity.getQuestionId());
            if (question != null && question.getAnswerCount() > 0) {
                question.setAnswerCount(question.getAnswerCount() - 1);
                question.setUpdateTime(LocalDateTime.now());
                // 重新计算 latest_answer_id
                if (answerId.equals(question.getLatestAnswerId())) {
                    AnswerEntity newLatest = findLatestAnswer(entity.getQuestionId(), answerId);
                    question.setLatestAnswerId(newLatest != null ? newLatest.getId() : null);
                }
                questionMapper.updateById(question);
            }
        }

        log.info("用户{}删除回答/评论{}成功", userId, answerId);
    }

    @Override
    public List<AnswerVO> getAnswersByQuestion(Long questionId, Long currentUserId) {
        // 只查询一级回答（answer_id = 0）
        LambdaQueryWrapper<AnswerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerEntity::getQuestionId, questionId)
                .eq(AnswerEntity::getAnswerId, 0L)
                .orderByDesc(AnswerEntity::getIsTeacher)
                .orderByDesc(AnswerEntity::getLikeCount)
                .orderByDesc(AnswerEntity::getCreateTime);

        List<AnswerEntity> entities = answerMapper.selectList(queryWrapper);

        return entities.stream()
                .map(entity -> entityToVO(entity, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AnswerVO> getAnswerReplies(Long answerId, Long currentUserId) {
        LambdaQueryWrapper<AnswerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnswerEntity::getAnswerId, answerId)
                .orderByAsc(AnswerEntity::getCreateTime);

        List<AnswerEntity> entities = answerMapper.selectList(queryWrapper);

        return entities.stream()
                .map(entity -> entityToVO(entity, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, Long answerId) {
        // 检查回答/评论是否存在
        AnswerEntity answerEntity = answerMapper.selectById(answerId);
        if (answerEntity == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "回答/评论不存在");
        }

        // 用手写 SQL 查询点赞记录（含历史已软删除记录，绕过 @TableLogic 的自动过滤）
        AnswerLikeEntity existingLike = answerLikeMapper.selectIgnoreDeleted(userId, answerId);

        boolean liked;

        if (existingLike == null) {
            // 从未点赞过 → 新增记录
            AnswerLikeEntity likeEntity = new AnswerLikeEntity();
            likeEntity.setUserId(userId);
            likeEntity.setAnswerId(answerId);
            likeEntity.setIsDeleted(0);
            likeEntity.setCreateTime(LocalDateTime.now());
            likeEntity.setUpdateTime(LocalDateTime.now());
            answerLikeMapper.insert(likeEntity);
            // 原子自增 like_count
            answerMapper.update(null,
                    new LambdaUpdateWrapper<AnswerEntity>()
                            .setSql("like_count = like_count + 1")
                            .eq(AnswerEntity::getId, answerId));
            liked = true;
            log.info("用户{}点赞回答/评论{}", userId, answerId);

        } else if (existingLike.getIsDeleted() == 0) {
            // 已点赞 → 取消（逻辑删除）
            answerLikeMapper.update(null,
                    new LambdaUpdateWrapper<AnswerLikeEntity>()
                            .set(AnswerLikeEntity::getIsDeleted, 1)
                            .eq(AnswerLikeEntity::getId, existingLike.getId()));
            // 原子自减 like_count（不低于 0）
            answerMapper.update(null,
                    new LambdaUpdateWrapper<AnswerEntity>()
                            .setSql("like_count = GREATEST(like_count - 1, 0)")
                            .eq(AnswerEntity::getId, answerId));
            liked = false;
            log.info("用户{}取消点赞回答/评论{}", userId, answerId);

        } else {
            // 曾取消过，重新点赞 → 用原生 UPDATE 恢复
            // （MP 的 LambdaUpdateWrapper 会自动拼 AND is_deleted=0，无法更新 is_deleted=1 的行）
            answerLikeMapper.restoreLike(existingLike.getId());
            // 原子自增 like_count
            answerMapper.update(null,
                    new LambdaUpdateWrapper<AnswerEntity>()
                            .setSql("like_count = like_count + 1")
                            .eq(AnswerEntity::getId, answerId));
            liked = true;
            log.info("用户{}重新点赞回答/评论{}", userId, answerId);
        }

        return liked;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnswerByAdmin(Long answerId) {
        AnswerEntity entity = answerMapper.selectById(answerId);
        if (entity == null) {
            throw new BusinessException(PARAM_ERROR, "回答/评论不存在");
        }

        answerMapper.deleteById(answerId);

        // 联动更新父记录计数（与用户侧删除逻辑一致，Admin 无权限校验）
        if (entity.getAnswerId() != null && entity.getAnswerId() > 0) {
            // 二级评论被删除 → 父回答 reply_times -1
            AnswerEntity parentAnswer = answerMapper.selectById(entity.getAnswerId());
            if (parentAnswer != null && parentAnswer.getReplyTimes() > 0) {
                parentAnswer.setReplyTimes(parentAnswer.getReplyTimes() - 1);
                parentAnswer.setUpdateTime(LocalDateTime.now());
                answerMapper.updateById(parentAnswer);
            }
        } else {
            // 一级回答被删除 → question.answer_count -1，重算 latest_answer_id
            QuestionEntity question = questionMapper.selectById(entity.getQuestionId());
            if (question != null && question.getAnswerCount() > 0) {
                question.setAnswerCount(question.getAnswerCount() - 1);
                question.setUpdateTime(LocalDateTime.now());
                if (answerId.equals(question.getLatestAnswerId())) {
                    AnswerEntity newLatest = findLatestAnswer(entity.getQuestionId(), answerId);
                    question.setLatestAnswerId(newLatest != null ? newLatest.getId() : null);
                }
                questionMapper.updateById(question);
            }
        }

        log.info("管理员删除回答/评论{}成功", answerId);
    }

    /**
     * 查找问题下除指定 ID 外最新的一级回答
     */
    private AnswerEntity findLatestAnswer(Long questionId, Long excludeId) {
        LambdaQueryWrapper<AnswerEntity> q = new LambdaQueryWrapper<>();
        q.eq(AnswerEntity::getQuestionId, questionId)
                .eq(AnswerEntity::getAnswerId, 0L)
                .ne(AnswerEntity::getId, excludeId)
                .orderByDesc(AnswerEntity::getCreateTime)
                .last("LIMIT 1");
        return answerMapper.selectOne(q);
    }

    /**
     * Entity 转 VO
     */
    private AnswerVO entityToVO(AnswerEntity entity, Long currentUserId) {
        AnswerVO vo = new AnswerVO();
        BeanUtils.copyProperties(entity, vo);

        // 作者信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 被回复用户昵称
        if (entity.getTargetUserId() != null && entity.getTargetUserId() > 0) {
            UserEntity targetUser = userMapper.selectById(entity.getTargetUserId());
            if (targetUser != null) {
                vo.setTargetNickname(targetUser.getNickname());
            }
        }

        // 是否点赞
        if (currentUserId != null) {
            LambdaQueryWrapper<AnswerLikeEntity> likeQuery = new LambdaQueryWrapper<>();
            likeQuery.eq(AnswerLikeEntity::getUserId, currentUserId)
                    .eq(AnswerLikeEntity::getAnswerId, entity.getId());
            vo.setIsLiked(answerLikeMapper.selectCount(likeQuery) > 0);
        } else {
            vo.setIsLiked(false);
        }

        // 是否为作者
        vo.setIsOwner(currentUserId != null && currentUserId.equals(entity.getUserId()));

        return vo;
    }
}
