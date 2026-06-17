package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.entity.CourseLikeEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.entity.CourseCategoryEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.vo.CourseLikeVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseLikeMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.mapper.CourseCategoryMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.service.CourseLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程点赞服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseLikeServiceImpl implements CourseLikeService {

    @Autowired
    private CourseLikeMapper courseLikeMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseLikeVO toggleLike(Long userId, Long courseId) {
        // 检查课程是否存在且已发布
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 查询点赞记录（包含软删除的历史记录）
        CourseLikeEntity existingLike = courseLikeMapper.selectIgnoreDeleted(userId, courseId);

        boolean isLiked;

        if (existingLike == null) {
            // 从未点赞过 → 新增记录
            CourseLikeEntity like = new CourseLikeEntity();
            like.setUserId(userId);
            like.setCourseId(courseId);
            like.setCreateTime(LocalDateTime.now());
            like.setUpdateTime(LocalDateTime.now());
            like.setIsDeleted(0);
            courseLikeMapper.insert(like);
            // 原子自增 like_count
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("like_count = like_count + 1")
                            .eq(CourseEntity::getId, courseId));
            isLiked = true;
            log.info("用户{}点赞课程{}", userId, courseId);

        } else if (existingLike.getIsDeleted() == 0) {
            // 已点赞 → 取消点赞（逻辑删除）
            courseLikeMapper.update(null,
                    new LambdaUpdateWrapper<CourseLikeEntity>()
                            .set(CourseLikeEntity::getIsDeleted, 1)
                            .eq(CourseLikeEntity::getId, existingLike.getId()));
            // 原子自减 like_count（不低于 0）
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("like_count = GREATEST(like_count - 1, 0)")
                            .eq(CourseEntity::getId, courseId));
            isLiked = false;
            log.info("用户{}取消点赞课程{}", userId, courseId);

        } else {
            // 曾经点赞过后取消，现在重新点赞 → 恢复记录
            // 必须用原生 SQL，MP 的 LambdaUpdateWrapper 会自动拼 AND is_deleted=0，导致 WHERE 匹配不到 is_deleted=1 的行
            courseLikeMapper.restoreLike(existingLike.getId());
            // 原子自增 like_count
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("like_count = like_count + 1")
                            .eq(CourseEntity::getId, courseId));
            isLiked = true;
            log.info("用户{}重新点赞课程{}", userId, courseId);
        }

        // 查询最新 like_count
        CourseEntity updatedCourse = courseMapper.selectById(courseId);

        CourseLikeVO vo = new CourseLikeVO();
        vo.setIsLiked(isLiked);
        vo.setLikeCount(updatedCourse != null ? updatedCourse.getLikeCount() : 0);
        return vo;
    }

    @Override
    public boolean isLiked(Long userId, Long courseId) {
        Long count = courseLikeMapper.selectCount(
                new LambdaQueryWrapper<CourseLikeEntity>()
                        .eq(CourseLikeEntity::getUserId, userId)
                        .eq(CourseLikeEntity::getCourseId, courseId)
                        .eq(CourseLikeEntity::getIsDeleted, 0)
        );
        return count != null && count > 0;
    }

    @Override
    public Page<CourseVO> getLikedCourses(Long userId, Integer page, Integer size) {
        // 分页查询用户点赞的课程ID（按点赞时间倒序）
        Page<CourseLikeEntity> likePage = courseLikeMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<CourseLikeEntity>()
                        .eq(CourseLikeEntity::getUserId, userId)
                        .eq(CourseLikeEntity::getIsDeleted, 0)
                        .orderByDesc(CourseLikeEntity::getCreateTime)
        );

        // 构造结果分页对象（保留分页元数据）
        Page<CourseVO> voPage = new Page<>(likePage.getCurrent(), likePage.getSize(), likePage.getTotal());

        List<CourseVO> voList = likePage.getRecords().stream()
                .map(like -> {
                    CourseEntity course = courseMapper.selectById(like.getCourseId());
                    if (course == null) {
                        return null;
                    }
                    CourseVO vo = entityToVO(course);
                    vo.setIsLiked(true); // 查询的就是点赞列表，一定为 true
                    return vo;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * CourseEntity → CourseVO 转换（与 CourseServiceImpl 保持一致）
     */
    private CourseVO entityToVO(CourseEntity entity) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(entity, vo);

        CourseCategoryEntity category = categoryMapper.selectById(entity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        UserEntity teacher = userMapper.selectById(entity.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getNickname() != null ? teacher.getNickname() : teacher.getUsername());
        }

        return vo;
    }
}
