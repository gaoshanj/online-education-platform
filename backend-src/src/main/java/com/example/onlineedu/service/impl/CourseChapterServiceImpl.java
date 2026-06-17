package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.CourseChapterDTO;
import com.example.onlineedu.domain.entity.CourseChapterEntity;
import com.example.onlineedu.domain.entity.CourseEntity;
import com.example.onlineedu.domain.model.LoginUser;
import com.example.onlineedu.domain.vo.CourseChapterTreeVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseChapterMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.service.CourseChapterService;
import com.example.onlineedu.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程章节服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseChapterServiceImpl implements CourseChapterService {

    @Autowired
    private CourseChapterMapper chapterMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public void add(CourseChapterDTO dto) {
        // 检查课程是否存在
        CourseEntity course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 如果是小节，检查父章节是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            CourseChapterEntity parentChapter = chapterMapper.selectById(dto.getParentId());
            if (parentChapter == null) {
                throw new BusinessException(ErrorCode.CHAPTER_NOT_EXIST, "父章节不存在");
            }
        }

        // 讲师归属校验
        checkCourseOwnership(dto.getCourseId());

        // 课程状态校验：只有可编辑状态才能操作章节
        checkCourseEditable(course);

        // 创建章节
        CourseChapterEntity entity = new CourseChapterEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);

        chapterMapper.insert(entity);
        log.info("添加章节成功：{}", dto.getChapterName());
    }

    @Override
    public void update(CourseChapterDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "章节ID不能为空");
        }

        // 检查章节是否存在
        CourseChapterEntity entity = chapterMapper.selectById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_EXIST);
        }

        // 讲师归属校验
        checkCourseOwnership(entity.getCourseId());

        // 课程状态校验
        CourseEntity course = courseMapper.selectById(entity.getCourseId());
        checkCourseEditable(course);

        // 更新章节
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());

        chapterMapper.updateById(entity);
        log.info("更新章节成功：{}", dto.getChapterName());
    }

    @Override
    public void delete(Long id) {
        // 检查章节是否存在
        CourseChapterEntity entity = chapterMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_EXIST);
        }

        // 讲师归属校验
        checkCourseOwnership(entity.getCourseId());

        // 课程状态校验
        CourseEntity course = courseMapper.selectById(entity.getCourseId());
        checkCourseEditable(course);

        // 检查是否有子章节（小节）
        Long childCount = chapterMapper.selectCount(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该章节下有小节，无法删除");
        }

        // 逻辑删除
        chapterMapper.deleteById(id);

        log.info("删除章节成功：{}", entity.getChapterName());
    }

    @Override
    public List<CourseChapterTreeVO> getChapterTree(Long courseId) {
        // 校验课程是否存在
        CourseEntity course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // App 用户端只能查看已发布（status=2）课程的章节
        // 管理员/讲师不受此限制
        LoginUser currentUser = UserContext.getUser();
        boolean isAdminOrTeacher = currentUser != null
                && currentUser.getRoles() != null
                && (currentUser.getRoles().contains("ADMIN") || currentUser.getRoles().contains("TEACHER"));
        if (!isAdminOrTeacher && course.getStatus() != 2) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }

        // 查询课程的所有章节
        List<CourseChapterEntity> allChapters = chapterMapper.selectList(
                new LambdaQueryWrapper<CourseChapterEntity>()
                        .eq(CourseChapterEntity::getCourseId, courseId)
                        .orderByAsc(CourseChapterEntity::getSortOrder));

        // 转换为VO
        List<CourseChapterTreeVO> allVOs = allChapters.stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public void updateSort(Long id, Integer sortOrder) {
        CourseChapterEntity entity = chapterMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.CHAPTER_NOT_EXIST);
        }

        // 讲师归属校验
        checkCourseOwnership(entity.getCourseId());

        entity.setSortOrder(sortOrder);
        entity.setUpdateTime(LocalDateTime.now());
        chapterMapper.updateById(entity);

        log.info("更新章节排序成功：{} -> {}", entity.getChapterName(), sortOrder);
    }

    /**
     * Entity 转 VO
     */
    private CourseChapterTreeVO entityToVO(CourseChapterEntity entity) {
        CourseChapterTreeVO vo = new CourseChapterTreeVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 构建树形结构
     */
    private List<CourseChapterTreeVO> buildTree(List<CourseChapterTreeVO> allChapters, Long parentId) {
        List<CourseChapterTreeVO> result = new ArrayList<>();

        for (CourseChapterTreeVO chapter : allChapters) {
            if (chapter.getParentId().equals(parentId)) {
                // 递归查找子章节
                List<CourseChapterTreeVO> children = buildTree(allChapters, chapter.getId());
                chapter.setChildren(children);
                result.add(chapter);
            }
        }

        return result;
    }

    /**
     * 检查课程所有权（讲师归属校验）
     */
    private void checkCourseOwnership(Long courseId) {
        LoginUser currentUser = UserContext.getUser();
        if (currentUser != null && currentUser.getRoles() != null) {
            boolean isAdmin = currentUser.getRoles().contains("ADMIN");
            boolean isTeacher = currentUser.getRoles().contains("TEACHER");

            // 如果是讲师（且不是管理员），检查是否是自己的课程
            if (isTeacher && !isAdmin) {
                CourseEntity course = courseMapper.selectById(courseId);
                if (course == null || !course.getTeacherId().equals(currentUser.getUserId())) {
                    throw new BusinessException(ErrorCode.NOT_COURSE_TEACHER);
                }
            }
        }
    }

    /**
     * 检查课程是否处于可编辑状态
     * 允许：草稿(0)、审核拒绝(3)、已下架(4)
     * 禁止：待审核(1)、已发布(2)
     */
    private void checkCourseEditable(CourseEntity course) {
        if (course == null) {
            throw new BusinessException(ErrorCode.COURSE_NOT_EXIST);
        }
        int status = course.getStatus();
        if (status == 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "课程待审核期间不能修改章节");
        }
        if (status == 2) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "已发布课程不能直接修改章节，请先下架课程");
        }
    }
}
