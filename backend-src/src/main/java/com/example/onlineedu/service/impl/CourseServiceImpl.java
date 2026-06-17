package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.CourseDTO;
import com.example.onlineedu.domain.entity.*;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.*;
import com.example.onlineedu.service.CourseService;
import com.example.onlineedu.service.CourseEnrollmentService;
import com.example.onlineedu.service.LearningProgressService;
import com.example.onlineedu.service.CourseLikeService;
import com.example.onlineedu.service.CourseFavoriteService;
import com.example.onlineedu.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseEnrollmentService enrollmentService;

    @Autowired
    private LearningProgressService progressService;

    @Autowired
    private CourseLikeService likeService;

    @Autowired
    private CourseFavoriteService favoriteService;

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Autowired
    private CourseChapterMapper courseChapterMapper;

    @Autowired
    private CourseEnrollmentMapper courseEnrollmentMapper;

    @Autowired
    private CourseLikeMapper courseLikeMapper;

    @Autowired
    private CourseFavoriteMapper courseFavoriteMapper;

    @Autowired
    private CourseReviewMapper courseReviewMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Long add(CourseDTO dto) {
        // 检查分类是否存在
        CourseCategoryEntity category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }

        // 自动从当前登录用户获取 teacherId（讲师端调用时无需前端传递）
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null) {
            dto.setTeacherId(currentUser.getUserId());
        }

        // 检查讲师是否存在（）不需要重复判断
//        UserEntity teacher = userMapper.selectById(dto.getTeacherId());
//        if (teacher == null) {
//            throw new BusinessException(ErrorCode.TEACHER_NOT_EXIST);
//        }

        // 创建课程（默认草稿状态）
        CourseEntity entity = new CourseEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setStatus(0); // 草稿
        entity.setViewCount(0);
        entity.setStudentCount(0);
        entity.setLikeCount(0);
        entity.setFavoriteCount(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);

        courseMapper.insert(entity); // MybatisPlus 自动将主键回填到 entity.id
        log.info("创建草稿课程成功：{}，ID：{}", dto.getCourseName(), entity.getId());
        return entity.getId();
    }

    @Override
    public void update(CourseDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程ID不能为空");
        }

        // 检查课程是否存在
        CourseEntity entity = courseMapper.selectById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 状态校验：草稿(0)、审核拒绝(3)、已下架(4) 均可编辑
        int status = entity.getStatus();
        if (status == 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程待审核期间不能编辑");
        }
        if (status == 2) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已发布课程不能直接编辑，请先下架");
        }

        // 讲师权限校验：只能编辑自己的课程
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null && currentUser.getRoles() != null) {
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean isTeacher = currentUser.getRoles().contains("TEACHER");
            if (isTeacher && !isAdmin && !entity.getTeacherId().equals(currentUser.getUserId())) {
                throw new BusinessException(ErrorCode.NOT_COURSE_TEACHER);
            }
        }

        // 检查分类是否存在
        if (dto.getCategoryId() != null) {
            CourseCategoryEntity category = categoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
            }
        }

        // 更新课程（重新编辑后清空拒绝原因）
        BeanUtils.copyProperties(dto, entity);
        entity.setRejectReason(null);
        entity.setUpdateTime(LocalDateTime.now());

        courseMapper.updateById(entity);
        log.info("更新课程成功：{}", dto.getCourseName());
    }

    @Override
    public void publish(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 讲师权限校验：只能发布自己的课程
        com.example.onlineedu.domain.model.LoginUser currentUser = com.example.onlineedu.utils.UserContext.getUser();
        if (currentUser != null && currentUser.getRoles() != null) {
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean isTeacher = currentUser.getRoles().contains("TEACHER");

            if (isTeacher && !isAdmin && !entity.getTeacherId().equals(currentUser.getUserId())) {
                throw new BusinessException(ErrorCode.NOT_COURSE_TEACHER);
            }
        }

        entity.setStatus(1); // 已发布
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);

        log.info("发布课程成功：{}", entity.getCourseName());
    }

    @Override
    public void offline(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 讲师权限校验：只能下架自己的课程
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null && currentUser.getRoles() != null) {
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean isTeacher = currentUser.getRoles().contains("TEACHER");
            if (isTeacher && !isAdmin && !entity.getTeacherId().equals(currentUser.getUserId())) {
                throw new BusinessException(ErrorCode.NOT_COURSE_TEACHER);
            }
        }

        // 只有已发布课程才能申请下架
        if (entity.getStatus() != 2) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只有已发布的课程才能下架");
        }

        entity.setStatus(4); // 已下架
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);

        log.info("下架课程成功：{}", entity.getCourseName());
    }

    @Override
    public void delete(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 逻辑删除课程本身
        courseMapper.deleteById(entity.getId());

        // 级联软删除课程关联数据
        courseChapterMapper.delete(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getCourseId, id));
        learningProgressMapper.delete(
                new LambdaQueryWrapper<LearningProgressEntity>()
                        .eq(LearningProgressEntity::getCourseId, id));
        courseEnrollmentMapper.delete(
                new LambdaQueryWrapper<CourseEnrollmentEntity>()
                        .eq(CourseEnrollmentEntity::getCourseId, id));
        courseLikeMapper.delete(
                new LambdaQueryWrapper<CourseLikeEntity>()
                        .eq(CourseLikeEntity::getCourseId, id));
        courseFavoriteMapper.delete(
                new LambdaQueryWrapper<CourseFavoriteEntity>()
                        .eq(CourseFavoriteEntity::getCourseId, id));
        courseReviewMapper.delete(
                new LambdaQueryWrapper<CourseReviewEntity>()
                        .eq(CourseReviewEntity::getCourseId, id));
        questionMapper.delete(
                new LambdaQueryWrapper<QuestionEntity>()
                        .eq(QuestionEntity::getCourseId,id));

        log.info("删除课程成功：{}（已级联删除章节/报名/进度/点赞/收藏/评价/提问）", entity.getCourseName());
    }

    @Override
    public Page<CourseVO> pageForApp(Integer page, Integer size, Long categoryId, String keyword, String orderBy) {
        // 构建查询条件（只查询已发布的课程）
        LambdaQueryWrapper<CourseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEntity::getStatus, 2); // 只查询已发布的课程

        // 分类过滤：父类自动包含所有子类
        if (categoryId != null) {
            List<Long> categoryIds = collectCategoryAndDescendantIds(categoryId);
            queryWrapper.in(CourseEntity::getCategoryId, categoryIds);
        }

        // 关键词搜索（搜索课程名称和简介）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(CourseEntity::getCourseName, keyword)
                    .or()
                    .like(CourseEntity::getDescription, keyword));
        }

        // 排序
        applyOrder(queryWrapper, orderBy);

        // 分页查询
        Page<CourseEntity> entityPage = courseMapper.selectPage(new Page<>(page, size), queryWrapper);

        // 转换为VO
        Page<CourseVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToVO)
                .collect(java.util.stream.Collectors.toList()));

        return voPage;
    }

    /*@Override
    public Page<CourseVO> pageByTeacher(Integer page, Integer size, String courseName, Long categoryId,
                                        Integer status, Integer isRecommend, String orderBy) {
        LoginUser currentUser = UserContext.getUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN);
        }
        LambdaQueryWrapper<CourseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseEntity::getTeacherId, currentUser.getUserId())
                .like(StringUtils.hasText(courseName), CourseEntity::getCourseName, courseName)
                .eq(categoryId != null, CourseEntity::getCategoryId, categoryId)
                .eq(status != null, CourseEntity::getStatus, status)
                .eq(isRecommend != null, CourseEntity::getIsRecommend, isRecommend);

        applyOrder(queryWrapper, orderBy);

        Page<CourseEntity> entityPage = courseMapper.selectPage(new Page<>(page, size), queryWrapper);
        Page<CourseVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToVO)
                .collect(java.util.stream.Collectors.toList()));
        return voPage;
    }*/

    @Override
    public Page<CourseVO> page(Integer page, Integer size, String courseName, Long categoryId,
                               Integer status, Integer isRecommend, String orderBy) {
        LambdaQueryWrapper<CourseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(courseName), CourseEntity::getCourseName, courseName)
                .eq(status != null, CourseEntity::getStatus, status)
                .eq(isRecommend != null, CourseEntity::getIsRecommend, isRecommend);

        // 选择分类时，同时查询其所有子分类的课程
        if (categoryId != null) {
            java.util.List<Long> categoryIds = collectCategoryAndDescendantIds(categoryId);
            queryWrapper.in(CourseEntity::getCategoryId, categoryIds);
        }

        // 管理员可看全部，讲师只看自己的
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null && currentUser.getRoles() != null) {
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean isTeacher = currentUser.getRoles().contains("TEACHER");
            if (isTeacher && !isAdmin) {
                queryWrapper.eq(CourseEntity::getTeacherId, currentUser.getUserId());
            }
        }

        applyOrder(queryWrapper, orderBy);

        Page<CourseEntity> entityPage = courseMapper.selectPage(new Page<>(page, size), queryWrapper);
        Page<CourseVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::entityToVO)
                .collect(java.util.stream.Collectors.toList()));
        return voPage;
    }

    @Override
    public CourseVO getDetail(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // App 普通用户只能查看已发布（status=2）的课程
        LoginUser currentUser = UserContext.getUser();
        boolean isAdminOrTeacher = currentUser != null
                && currentUser.getRoles() != null
                && (currentUser.getRoles().contains("ADMIN") || currentUser.getRoles().contains("TEACHER"));
        if (!isAdminOrTeacher && entity.getStatus() != 2) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }


        CourseVO vo = entityToVO(entity);

        // 如果用户已登录,查询报名状态、学习进度、点赞和收藏情况
        //LoginUser currentUser = UserContext.getUser();
        if (currentUser != null) {
            Long userId = currentUser.getUserId();
            vo.setIsEnrolled(enrollmentService.isEnrolled(userId, id));
            
            if (Boolean.TRUE.equals(vo.getIsEnrolled())) {
                Integer progress = progressService.calculateCourseProgress(userId, id);
                vo.setLearningProgress(progress);

                // 查询最近学习章节
                LambdaQueryWrapper<LearningProgressEntity> progressQuery = new LambdaQueryWrapper<>();
                progressQuery.eq(LearningProgressEntity::getUserId, userId)
                        .eq(LearningProgressEntity::getCourseId, id)
                        .orderByDesc(LearningProgressEntity::getUpdateTime)
                        .last("limit 1");
                
                LearningProgressEntity recentProgress = learningProgressMapper.selectOne(progressQuery);
                if (recentProgress != null) {
                    vo.setRecentChapterId(recentProgress.getChapterId());
                    CourseChapterEntity chapter = courseChapterMapper.selectById(recentProgress.getChapterId());
                    if (chapter != null) {
                        vo.setRecentChapterName(chapter.getChapterName());
                    }
                }
            }
            vo.setIsLiked(likeService.isLiked(userId, id));
            vo.setIsFavorited(favoriteService.isFavorited(userId, id));
        }

        return vo;
    }


    @Override
    public void increaseViewCount(Long courseId) {
        CourseEntity entity = courseMapper.selectById(courseId);
        if (entity != null) {
            entity.setViewCount(entity.getViewCount() + 1);
            entity.setUpdateTime(LocalDateTime.now());
            courseMapper.updateById(entity);
        }
    }

    @Override
    public void submitForReview(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        // 归属校验
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null && !entity.getTeacherId().equals(currentUser.getUserId())) {
            throw new BusinessException(ErrorCode.NOT_COURSE_TEACHER);
        }
        // 草稿(0)、审核拒绝(3)、已下架(4) 均可提交审核
        int status = entity.getStatus();
        if (status == 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程已在审核中，请勿重复提交");
        }
        if (status == 2) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程已发布，无需重新提交审核");
        }
        entity.setStatus(1); // 待审核
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);
        log.info("课程提交审核：{}", entity.getCourseName());
    }

    @Override
    public void approve(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        if (entity.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只有待审核的课程才能审核通过");
        }
        entity.setStatus(2); // 已发布
        entity.setRejectReason(null);
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);
        log.info("课程审核通过：{}", entity.getCourseName());
    }

    @Override
    public void reject(Long id, String rejectReason) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        if (entity.getStatus() != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "只有待审核的课程才能审核拒绝");
        }
        entity.setStatus(3); // 审核拒绝
        entity.setRejectReason(rejectReason);
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);
        log.info("课程审核拒绝：{}，原因：{}", entity.getCourseName(), rejectReason);
    }

    @Override
    public void forceOffline(Long id) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        entity.setStatus(4); // 已下架
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);
        log.info("管理员强制下架课程：{}", entity.getCourseName());
    }


    @Override
    public void setRecommend(Long id, Integer isRecommend) {
        CourseEntity entity = courseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        entity.setIsRecommend(isRecommend);
        entity.setUpdateTime(LocalDateTime.now());
        courseMapper.updateById(entity);
        log.info("课程精品状态已更新：id={}, isRecommend={}", id, isRecommend);
    }

    /**
     * 递归收集某分类及其所有子分类的 ID（用于父类包含子类的过滤查询）
     */
    private List<Long> collectCategoryAndDescendantIds(Long parentId) {
        java.util.List<Long> ids = new java.util.ArrayList<>();
        ids.add(parentId);
        java.util.List<CourseCategoryEntity> children = categoryMapper.selectList(
                new LambdaQueryWrapper<CourseCategoryEntity>()
                        .eq(CourseCategoryEntity::getParentId, parentId));
        for (CourseCategoryEntity child : children) {
            ids.addAll(collectCategoryAndDescendantIds(child.getId()));
        }
        return ids;
    }

    /**
     * Entity 转 VO
     */
    private CourseVO entityToVO(CourseEntity entity) {
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

    /**
     * 公共排序逻辑（管理/讲师/用户端共用）
     * hot  = 热度排序（hot_score DESC）
     * time = 最新发布（create_time DESC）
     * 不传 = 综合质量（avg_score → student_count → create_time DESC）
     */
    private void applyOrder(LambdaQueryWrapper<CourseEntity> queryWrapper, String orderBy) {
        if ("hot".equals(orderBy)) {
            queryWrapper.orderByDesc(CourseEntity::getHotScore);
        } else if ("time".equals(orderBy)) {
            queryWrapper.orderByDesc(CourseEntity::getCreateTime);
        } else {
            // 默认综合排序（质量优先）：
            //   1. 平均评分高的优先（质量信号）
            //   2. 同评分看学习人数（受认可程度）
            //   3. 再看发布时间（同等情况新课在前）
            // 与热度榜的区别：热度看"量"，综合看"质"
            queryWrapper.orderByDesc(CourseEntity::getAvgScore)
                    .orderByDesc(CourseEntity::getStudentCount)
                    .orderByDesc(CourseEntity::getCreateTime);
        }
    }

}
