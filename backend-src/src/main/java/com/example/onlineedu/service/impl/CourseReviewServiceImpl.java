package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.CourseReviewAggDTO;
import com.example.onlineedu.domain.dto.CourseReviewDTO;
import com.example.onlineedu.domain.entity.*;
import com.example.onlineedu.domain.vo.CourseReviewLikeVO;
import com.example.onlineedu.domain.vo.CourseReviewStatListVO;
import com.example.onlineedu.domain.vo.CourseReviewStatVO;
import com.example.onlineedu.domain.vo.CourseReviewVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.*;
import com.example.onlineedu.service.CourseReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程评价服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseReviewServiceImpl implements CourseReviewService {

    @Autowired
    private CourseReviewMapper courseReviewMapper;

    @Autowired
    private CourseReviewLikeMapper courseReviewLikeMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;


    // ===================== 用户端 =====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long userId, CourseReviewDTO dto) {
        // 1. 校验课程存在
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请先报名该课程后再评价");
        }

        // 2. 校验是否已评价（物理存在的记录，不含逻辑删除的）
        Long count = courseReviewMapper.selectCount(
                new LambdaQueryWrapper<CourseReviewEntity>()
                        .eq(CourseReviewEntity::getUserId, userId)
                        .eq(CourseReviewEntity::getCourseId, dto.getCourseId())
        );
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "您已评价过该课程，如需修改请使用编辑功能");
        }

        // 3. 插入评价
        CourseReviewEntity review = new CourseReviewEntity();
        review.setCourseId(dto.getCourseId());
        review.setUserId(userId);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setLikeCount(0);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        review.setIsDeleted(0);
        courseReviewMapper.insert(review);
        log.info("用户{}对课程{}提交评价，评分={}", userId, dto.getCourseId(), dto.getRating());
        refreshCourseReviewStat(dto.getCourseId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReview(Long userId, Long reviewId, CourseReviewDTO dto) {
        CourseReviewEntity review = courseReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权修改他人的评价");
        }

        courseReviewMapper.update(null,
                new LambdaUpdateWrapper<CourseReviewEntity>()
                        .set(CourseReviewEntity::getRating, dto.getRating())
                        .set(CourseReviewEntity::getContent, dto.getContent())
                        .set(CourseReviewEntity::getUpdateTime, LocalDateTime.now())
                        .eq(CourseReviewEntity::getId, reviewId));
        log.info("用户{}修改评价{}，新评分={}", userId, reviewId, dto.getRating());
        refreshCourseReviewStat(review.getCourseId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long userId, Long reviewId) {
        CourseReviewEntity review = courseReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权删除他人的评价");
        }
        Long courseId = review.getCourseId();
        physicalDeleteReview(reviewId);
        log.info("用户{}物理删除评价{}", userId, reviewId);
        refreshCourseReviewStat(courseId);
    }

    @Override
    public Page<CourseReviewVO> listReviews(Long currentUserId, Long courseId,
                                            String sortType, Integer rating,
                                            Boolean mine, Integer page, Integer size) {
        // mine=true 时只查当前用户的评价
        Long filterUserId = Boolean.TRUE.equals(mine) ? currentUserId : null;

        Page<CourseReviewEntity> entityPage = new Page<>(page, size);
        List<CourseReviewEntity> records = courseReviewMapper.selectReviewPage(
                entityPage, courseId, rating, filterUserId, sortType);
        entityPage.setRecords(records);

        // 批量查询当前用户的点赞状态（减少 N+1）
        Set<Long> likedReviewIds = getLikedReviewIds(currentUserId, records);

        // 转 VO
        List<CourseReviewVO> voList = records.stream()
                .map(r -> toVO(r, likedReviewIds))
                .collect(Collectors.toList());

        Page<CourseReviewVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseReviewLikeVO toggleReviewLike(Long userId, Long reviewId) {
        // 校验评价存在
        CourseReviewEntity review = courseReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评价不存在");
        }

        CourseReviewLikeEntity existing = courseReviewLikeMapper.selectIgnoreDeleted(userId, reviewId);
        boolean isLiked;

        if (existing == null) {
            // 从未点赞 → 新增
            CourseReviewLikeEntity like = new CourseReviewLikeEntity();
            like.setUserId(userId);
            like.setReviewId(reviewId);
            like.setIsDeleted(0);
            like.setCreateTime(LocalDateTime.now());
            like.setUpdateTime(LocalDateTime.now());
            courseReviewLikeMapper.insert(like);
            incrementLikeCount(reviewId, 1);
            isLiked = true;
            log.info("用户{}点赞评价{}", userId, reviewId);

        } else if (existing.getIsDeleted() == 0) {
            // 已点赞 → 取消（逻辑删除）
            courseReviewLikeMapper.update(null,
                    new LambdaUpdateWrapper<CourseReviewLikeEntity>()
                            .set(CourseReviewLikeEntity::getIsDeleted, 1)
                            .set(CourseReviewLikeEntity::getUpdateTime, LocalDateTime.now())
                            .eq(CourseReviewLikeEntity::getId, existing.getId()));
            incrementLikeCount(reviewId, -1);
            isLiked = false;
            log.info("用户{}取消点赞评价{}", userId, reviewId);

        } else {
            // 曾取消 → 恢复点赞
            courseReviewLikeMapper.restoreLike(existing.getId());
            incrementLikeCount(reviewId, 1);
            isLiked = true;
            log.info("用户{}重新点赞评价{}", userId, reviewId);
        }

        CourseReviewEntity updated = courseReviewMapper.selectById(reviewId);
        CourseReviewLikeVO vo = new CourseReviewLikeVO();
        vo.setIsLiked(isLiked);
        vo.setLikeCount(updated != null ? updated.getLikeCount() : 0);
        return vo;
    }

    @Override
    public CourseReviewStatVO getReviewStat(Long currentUserId, Long courseId) {
        // 查询该课程所有有效评价
        List<CourseReviewEntity> reviews = courseReviewMapper.selectList(
                new LambdaQueryWrapper<CourseReviewEntity>()
                        .eq(CourseReviewEntity::getCourseId, courseId)
        );

        long total = reviews.size();
        CourseReviewStatVO vo = new CourseReviewStatVO();
        vo.setTotalReviews(total);

        // 计算平均分
        if (total > 0) {
            double sum = reviews.stream().mapToInt(CourseReviewEntity::getRating).sum();
            BigDecimal avg = BigDecimal.valueOf(sum / total).setScale(1, RoundingMode.HALF_UP);
            vo.setAvgScore(avg);
        } else {
            vo.setAvgScore(BigDecimal.ZERO);
        }

        // 评分分布
        Map<Integer, Long> dist = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            dist.put(i, 0L);
        }
        reviews.stream()
                .collect(Collectors.groupingBy(CourseReviewEntity::getRating, Collectors.counting()))
                .forEach(dist::put);
        vo.setRatingDistribution(dist);

        // 当前用户是否已评价
        boolean hasReviewed = false;
        if (currentUserId != null) {
            Long cnt = courseReviewMapper.selectCount(
                    new LambdaQueryWrapper<CourseReviewEntity>()
                            .eq(CourseReviewEntity::getUserId, currentUserId)
                            .eq(CourseReviewEntity::getCourseId, courseId)
            );
            hasReviewed = cnt != null && cnt > 0;
        }
        vo.setUserHasReviewed(hasReviewed);

        return vo;
    }

    // ===================== 管理员端 =====================

    @Override
    public Page<CourseReviewVO> adminListReviews(Long courseId, Integer rating, Integer page, Integer size) {
        Page<CourseReviewEntity> entityPage = new Page<>(page, size);
        LambdaQueryWrapper<CourseReviewEntity> wrapper = new LambdaQueryWrapper<CourseReviewEntity>()
                .orderByDesc(CourseReviewEntity::getCreateTime);
        if (courseId != null) {
            wrapper.eq(CourseReviewEntity::getCourseId, courseId);
        }
        if (rating != null) {
            wrapper.eq(CourseReviewEntity::getRating, rating);
        }
        courseReviewMapper.selectPage(entityPage, wrapper);

        List<CourseReviewVO> voList = entityPage.getRecords().stream()
                .map(r -> toVO(r, null))
                .collect(Collectors.toList());

        Page<CourseReviewVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeleteReview(Long reviewId) {
        CourseReviewEntity review = courseReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "评价不存在");
        }
        Long courseId = review.getCourseId();
        physicalDeleteReview(reviewId);
        log.info("管理员物理删除评价{}", reviewId);
        refreshCourseReviewStat(courseId);
    }

    // ===================== 私有工具方法 =====================

    /**
     * 物理删除评价（执行真正的 DELETE，绕过 @TableLogic）
     * 同步物理删除该评价的所有点赞记录
     */
    private void physicalDeleteReview(Long reviewId) {
        // 物理删除评价主记录
        courseReviewMapper.physicalDeleteById(reviewId);
        // 物理删除该评价的所有点赞记录
        courseReviewLikeMapper.physicalDeleteByReviewId(reviewId);
    }

    /**
     * 原子更新评价点赞数
     *
     * @param delta +1 or -1
     */
    private void incrementLikeCount(Long reviewId, int delta) {
        if (delta > 0) {
            courseReviewMapper.update(null,
                    new LambdaUpdateWrapper<CourseReviewEntity>()
                            .setSql("like_count = like_count + 1")
                            .eq(CourseReviewEntity::getId, reviewId));
        } else {
            courseReviewMapper.update(null,
                    new LambdaUpdateWrapper<CourseReviewEntity>()
                            .setSql("like_count = GREATEST(like_count - 1, 0)")
                            .eq(CourseReviewEntity::getId, reviewId));
        }
    }

    /**
     * 批量查询当前用户对评价列表的点赞状态（避免 N+1）
     */
    private Set<Long> getLikedReviewIds(Long userId, List<CourseReviewEntity> reviews) {
        if (userId == null || reviews.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        List<Long> reviewIds = reviews.stream()
                .map(CourseReviewEntity::getId)
                .collect(Collectors.toList());
        List<CourseReviewLikeEntity> likes = courseReviewLikeMapper.selectList(
                new LambdaQueryWrapper<CourseReviewLikeEntity>()
                        .eq(CourseReviewLikeEntity::getUserId, userId)
                        .in(CourseReviewLikeEntity::getReviewId, reviewIds)
                        .eq(CourseReviewLikeEntity::getIsDeleted, 0)
        );
        return likes.stream()
                .map(CourseReviewLikeEntity::getReviewId)
                .collect(Collectors.toSet());
    }

    /**
     * CourseReviewEntity → CourseReviewVO 转换
     */
    private CourseReviewVO toVO(CourseReviewEntity entity, Set<Long> likedReviewIds) {
        CourseReviewVO vo = new CourseReviewVO();
        vo.setId(entity.getId());
        vo.setCourseId(entity.getCourseId());
        vo.setUserId(entity.getUserId());
        vo.setRating(entity.getRating());
        vo.setContent(entity.getContent());
        vo.setLikeCount(entity.getLikeCount());
        vo.setCreateTime(entity.getCreateTime());
        vo.setIsLiked(likedReviewIds != null && likedReviewIds.contains(entity.getId()));

        // 填充用户昵称和头像
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }

    // ===================== 统计列表 =====================

    @Override
    public Page<CourseReviewStatListVO> adminListCourseReviewStat(
            String courseName, Long teacherId,
            String avgScoreFilter, String goodReviewRateFilter,
            String sortBy, Integer page, Integer size) {
        Page<CourseEntity> entityPage = new Page<>(page, size);
        List<CourseEntity> records = courseMapper.selectReviewStatPage(
                entityPage, courseName, teacherId, avgScoreFilter, goodReviewRateFilter, sortBy);
        entityPage.setRecords(records);

        List<CourseReviewStatListVO> voList = records.stream()
                .map(c -> toCourseStatVO(c, true))
                .collect(Collectors.toList());

        Page<CourseReviewStatListVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<CourseReviewStatListVO> teacherListCourseReviewStat(
            Long teacherId, String courseName,
            String avgScoreFilter, String goodReviewRateFilter,
            String sortBy, Integer page, Integer size) {
        Page<CourseEntity> entityPage = new Page<>(page, size);
        // 讲师端强制传入 teacherId，确保只能查自己的课程
        List<CourseEntity> records = courseMapper.selectReviewStatPage(
                entityPage, courseName, teacherId, avgScoreFilter, goodReviewRateFilter, sortBy);
        entityPage.setRecords(records);

        List<CourseReviewStatListVO> voList = records.stream()
                .map(c -> toCourseStatVO(c, false))
                .collect(Collectors.toList());

        Page<CourseReviewStatListVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    // ===================== 私有工具方法（续） =====================

    /**
     * 重新计算并更新 course 表中的评价统计冗余字段
     * 在提交/修改/删除评价后调用
     */
    private void refreshCourseReviewStat(Long courseId) {
        CourseReviewAggDTO agg = courseReviewMapper.selectReviewStatAgg(courseId);
        if (agg == null) {
            return;
        }
        long total = agg.getReviewCount() != null ? agg.getReviewCount() : 0L;
        BigDecimal avgScore = BigDecimal.ZERO;
        BigDecimal goodRate = BigDecimal.ZERO;
        LocalDateTime latestTime = null;

        if (total > 0) {
            // 平均分保留1位小数
            avgScore = agg.getAvgScore() != null
                    ? agg.getAvgScore().setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            // 好评率：好评数/总数×100，保留2位小数
            long goodCount = agg.getGoodReviewCount() != null ? agg.getGoodReviewCount() : 0L;
            goodRate = BigDecimal.valueOf(goodCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
            latestTime = agg.getLatestReviewTime();
        }

        BigDecimal finalAvgScore = avgScore;
        BigDecimal finalGoodRate = goodRate;
        LocalDateTime finalLatestTime = latestTime;
        long finalTotal = total;

        courseMapper.update(null,
                new LambdaUpdateWrapper<CourseEntity>()
                        .set(CourseEntity::getAvgScore, finalAvgScore)
                        .set(CourseEntity::getReviewCount, (int) finalTotal)
                        .set(CourseEntity::getGoodReviewRate, finalGoodRate)
                        .set(CourseEntity::getLatestReviewTime, finalLatestTime)
                        .eq(CourseEntity::getId, courseId));
        log.info("课程{}评价统计已更新: 平均分={}, 评价数={}, 好评率={}",
                courseId, finalAvgScore, finalTotal, finalGoodRate);
    }

    /**
     * CourseEntity → CourseReviewStatListVO
     *
     * @param includeTeacherName 是否填充讲师名称（讲师端不需要）
     */
    private CourseReviewStatListVO toCourseStatVO(CourseEntity course, boolean includeTeacherName) {
        CourseReviewStatListVO vo = new CourseReviewStatListVO();
        vo.setCourseId(course.getId());
        vo.setCoverImage(course.getCoverImage());
        vo.setCourseName(course.getCourseName());
        vo.setAvgScore(course.getAvgScore());
        vo.setReviewCount(course.getReviewCount());
        vo.setGoodReviewRate(course.getGoodReviewRate());
        vo.setLatestReviewTime(course.getLatestReviewTime());

        if (includeTeacherName) {
            UserEntity teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getNickname() != null ? teacher.getNickname() : teacher.getUsername());
            }
        }
        return vo;
    }
}
