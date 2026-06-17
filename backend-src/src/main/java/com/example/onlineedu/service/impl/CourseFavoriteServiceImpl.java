package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.entity.CourseFavoriteEntity;
import com.example.onlineedu.domain.entity.CourseCategoryEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.entity.UserEntity;
import com.example.onlineedu.domain.vo.CourseFavoriteVO;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseFavoriteMapper;
import com.example.onlineedu.mapper.CourseCategoryMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.mapper.UserMapper;
import com.example.onlineedu.service.CourseFavoriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程收藏服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseFavoriteServiceImpl implements CourseFavoriteService {

    @Autowired
    private CourseFavoriteMapper courseFavoriteMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseFavoriteVO toggleFavorite(Long userId, Long courseId) {
        // 检查课程是否存在
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 查询收藏记录（包含软删除的历史记录，绕过 @TableLogic）
        CourseFavoriteEntity existingFavorite = courseFavoriteMapper.selectIgnoreDeleted(userId, courseId);

        boolean isFavorited;

        if (existingFavorite == null) {
            // 从未收藏过 → 新增记录
            CourseFavoriteEntity favorite = new CourseFavoriteEntity();
            favorite.setUserId(userId);
            favorite.setCourseId(courseId);
            favorite.setCreateTime(LocalDateTime.now());
            favorite.setUpdateTime(LocalDateTime.now());
            favorite.setIsDeleted(0);
            courseFavoriteMapper.insert(favorite);
            // 原子自增 favorite_count
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("favorite_count = favorite_count + 1")
                            .eq(CourseEntity::getId, courseId));
            isFavorited = true;
            log.info("用户{}收藏课程{}", userId, courseId);

        } else if (existingFavorite.getIsDeleted() == 0) {
            // 已收藏 → 取消收藏（逻辑删除）
            courseFavoriteMapper.update(null,
                    new LambdaUpdateWrapper<CourseFavoriteEntity>()
                            .set(CourseFavoriteEntity::getIsDeleted, 1)
                            .eq(CourseFavoriteEntity::getId, existingFavorite.getId()));
            // 原子自减 favorite_count（不低于 0）
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("favorite_count = GREATEST(favorite_count - 1, 0)")
                            .eq(CourseEntity::getId, courseId));
            isFavorited = false;
            log.info("用户{}取消收藏课程{}", userId, courseId);

        } else {
            // 曾经收藏过后取消，现在重新收藏 → 恢复记录
            // 必须用原生 SQL，MP 的 LambdaUpdateWrapper 会自动拼 AND is_deleted=0，导致 WHERE 匹配不到 is_deleted=1 的行
            courseFavoriteMapper.restoreFavorite(existingFavorite.getId());
            // 原子自增 favorite_count
            courseMapper.update(null,
                    new LambdaUpdateWrapper<CourseEntity>()
                            .setSql("favorite_count = favorite_count + 1")
                            .eq(CourseEntity::getId, courseId));
            isFavorited = true;
            log.info("用户{}重新收藏课程{}", userId, courseId);
        }

        // 查询最新 favorite_count
        CourseEntity updatedCourse = courseMapper.selectById(courseId);

        CourseFavoriteVO vo = new CourseFavoriteVO();
        vo.setIsFavorited(isFavorited);
        vo.setFavoriteCount(updatedCourse != null ? updatedCourse.getFavoriteCount() : 0);
        return vo;
    }

    @Override
    public boolean isFavorited(Long userId, Long courseId) {
        Long count = courseFavoriteMapper.selectCount(
                new LambdaQueryWrapper<CourseFavoriteEntity>()
                        .eq(CourseFavoriteEntity::getUserId, userId)
                        .eq(CourseFavoriteEntity::getCourseId, courseId)
                        .eq(CourseFavoriteEntity::getIsDeleted, 0)
        );
        return count != null && count > 0;
    }

    @Override
    public Page<CourseVO> getFavoritedCourses(Long userId, Integer page, Integer size) {
        // 分页查询用户收藏的课程（按收藏时间倒序）
        Page<CourseFavoriteEntity> favPage = courseFavoriteMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<CourseFavoriteEntity>()
                        .eq(CourseFavoriteEntity::getUserId, userId)
                        .eq(CourseFavoriteEntity::getIsDeleted, 0)
                        .orderByDesc(CourseFavoriteEntity::getCreateTime)
        );

        // 构造结果分页对象
        Page<CourseVO> voPage = new Page<>(favPage.getCurrent(), favPage.getSize(), favPage.getTotal());

        List<CourseVO> voList = favPage.getRecords().stream()
                .map(fav -> {
                    CourseEntity course = courseMapper.selectById(fav.getCourseId());
                    if (course == null) {
                        return null;
                    }
                    CourseVO vo = entityToVO(course);
                    vo.setIsFavorited(true); // 查询的就是收藏列表，一定为 true
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
