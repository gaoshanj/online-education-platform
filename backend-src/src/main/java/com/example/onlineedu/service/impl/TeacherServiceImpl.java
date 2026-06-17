package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.entity.*;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.domain.vo.TeacherVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.*;
import com.example.onlineedu.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 讲师服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private TeacherApplicationMapper applicationMapper;

    private static final String TEACHER_ROLE_CODE = "TEACHER";

    @Override
    public Page<TeacherVO> pageTeachers(Integer page, Integer size, String name) {
        // 查询TEACHER角色
        RoleEntity teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, TEACHER_ROLE_CODE));

        if (teacherRole == null) {
            return new Page<>(page, size, 0);
        }

        // 查询拥有TEACHER角色的用户ID
        List<UserRoleEntity> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getRoleId, teacherRole.getId()));

        if (userRoles.isEmpty()) {
            return new Page<>(page, size, 0);
        }

        List<Long> teacherIds = userRoles.stream()
                .map(UserRoleEntity::getUserId)
                .collect(Collectors.toList());

        // 分页查询讲师用户
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserEntity::getId, teacherIds)
                .and(StringUtils.hasText(name), wrapper -> wrapper.like(UserEntity::getUsername, name)
                        .or()
                        .like(UserEntity::getNickname, name))
                .orderByDesc(UserEntity::getCreateTime);

        Page<UserEntity> entityPage = userMapper.selectPage(new Page<>(page, size), queryWrapper);

        // 转换为VO
        Page<TeacherVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToVO)
                .collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public TeacherVO getTeacherDetail(Long teacherId) {
        UserEntity user = userMapper.selectById(teacherId);
        if (user == null) {
            throw new BusinessException(ErrorCode.TEACHER_NOT_EXIST);
        }

        return entityToVO(user);
    }

    @Override
    public List<CourseVO> getTeacherCourses(Long teacherId) {
        // 查询讲师的所有课程
        List<CourseEntity> courses = courseMapper.selectList(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getTeacherId, teacherId)
                        .orderByDesc(CourseEntity::getCreateTime));

        return courses.stream()
                .map(this::courseEntityToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTeacherRole(Long userId) {
        // 检查用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 查询TEACHER角色
        RoleEntity teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, TEACHER_ROLE_CODE));

        if (teacherRole == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "讲师角色不存在");
        }

        // 检查是否已经是讲师
        Long count = userRoleMapper.selectCount(
                new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, userId)
                        .eq(UserRoleEntity::getRoleId, teacherRole.getId()));

        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该用户已经是讲师");
        }

        // 添加TEACHER角色
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(userId);
        userRole.setRoleId(teacherRole.getId());
        userRole.setCreateTime(LocalDateTime.now());

        userRoleMapper.insert(userRole);
        log.info("设置讲师身份成功：userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTeacherRole(Long userId) {
        // 检查用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        TeacherApplicationEntity entity = applicationMapper.selectOne(new LambdaQueryWrapper<TeacherApplicationEntity>()
                .eq(TeacherApplicationEntity::getUserId, userId));
        //如果是申请的讲师，申请表状态设置为已取消讲师资格
        if (entity != null) {
            entity.setStatus(4);
            entity.setRejectReason(null);
            entity.setAuditorId(null);
            entity.setAuditTime(null);
            entity.setUpdateTime(LocalDateTime.now());
            applicationMapper.updateById(entity);
        }


        // 查询TEACHER角色
        RoleEntity teacherRole = roleMapper.selectOne(
                new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleCode, TEACHER_ROLE_CODE));

        if (teacherRole == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "讲师角色不存在");
        }

        // 移除TEACHER角色
        userRoleMapper.delete(
                new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, userId)
                        .eq(UserRoleEntity::getRoleId, teacherRole.getId()));

        log.info("取消讲师身份成功：userId={}", userId);
    }

    /**
     * Entity 转 TeacherVO
     */
    private TeacherVO entityToVO(UserEntity entity) {
        TeacherVO vo = new TeacherVO();
        BeanUtils.copyProperties(entity, vo);

        // 统计课程数据（利用 course 表冗余字段，无需额外联表）
        List<CourseEntity> courses = courseMapper.selectList(
                new LambdaQueryWrapper<CourseEntity>()
                        .eq(CourseEntity::getTeacherId, entity.getId())
                        .eq(CourseEntity::getIsDeleted, 0));

        vo.setCourseCount(courses.size());

        int totalStudents  = courses.stream().mapToInt(CourseEntity::getStudentCount).sum();
        int totalViews     = courses.stream().mapToInt(CourseEntity::getViewCount).sum();
        int totalLikes     = courses.stream().mapToInt(CourseEntity::getLikeCount).sum();
        int totalFavorites = courses.stream().mapToInt(CourseEntity::getFavoriteCount).sum();
        int totalReviews   = courses.stream().mapToInt(CourseEntity::getReviewCount).sum();

        vo.setStudentCount(totalStudents);
        vo.setViewCount(totalViews);
        vo.setLikeCount(totalLikes);
        vo.setFavoriteCount(totalFavorites);
        vo.setReviewCount(totalReviews);

        // 加权平均评分：sum(avg_score × review_count) / sum(review_count)
        if (totalReviews > 0) {
            double weightedSum = courses.stream()
                    .filter(c -> c.getReviewCount() != null && c.getReviewCount() > 0
                                 && c.getAvgScore() != null)
                    .mapToDouble(c -> c.getAvgScore().doubleValue() * c.getReviewCount())
                    .sum();
            vo.setAvgScore(java.math.BigDecimal.valueOf(weightedSum / totalReviews)
                    .setScale(1, java.math.RoundingMode.HALF_UP));
        }

        return vo;
    }


    /**
     * CourseEntity 转 CourseVO
     */
    private CourseVO courseEntityToVO(CourseEntity entity) {
        CourseVO vo = new CourseVO();
        BeanUtils.copyProperties(entity, vo);

        // 查询分类名称
        CourseCategoryEntity category = categoryMapper.selectById(entity.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }

        // 查询讲师名称
        UserEntity teacher = userMapper.selectById(entity.getTeacherId());
        if (teacher != null) {
            vo.setTeacherName(teacher.getNickname() != null ? teacher.getNickname() : teacher.getUsername());
        }

        return vo;
    }
}
