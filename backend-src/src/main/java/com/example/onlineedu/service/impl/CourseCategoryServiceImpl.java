package com.example.onlineedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineedu.common.ErrorCode;
import com.example.onlineedu.domain.dto.CourseCategoryDTO;
import com.example.onlineedu.domain.entity.CourseCategoryEntity;
import com.example.onlineedu.domain.vo.CourseCategoryVO;
import com.example.onlineedu.exception.BusinessException;
import com.example.onlineedu.mapper.CourseCategoryMapper;
import com.example.onlineedu.mapper.CourseMapper;
import com.example.onlineedu.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程分类服务实现类
 *
 * @author feng
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    private CourseCategoryMapper categoryMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<CourseCategoryVO> listAll() {
        // 查询所有分类
        List<CourseCategoryEntity> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<CourseCategoryEntity>()
                        .orderByAsc(CourseCategoryEntity::getSortOrder));

        // 转换为VO
        List<CourseCategoryVO> allVOs = allCategories.stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allVOs, 0L);
    }

    @Override
    public void add(CourseCategoryDTO dto) {
        // 检查分类名称是否已存在
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<CourseCategoryEntity>()
                        .eq(CourseCategoryEntity::getCategoryName, dto.getCategoryName()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXIST, "分类名称已存在");
        }

        // 创建分类
        CourseCategoryEntity entity = new CourseCategoryEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);

        categoryMapper.insert(entity);
        log.info("添加课程分类成功：{}", dto.getCategoryName());
    }

    @Override
    public void update(CourseCategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "分类ID不能为空");
        }

        // 检查分类是否存在
        CourseCategoryEntity entity = categoryMapper.selectById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }

        // 检查分类名称是否重复（排除自己）
        Long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<CourseCategoryEntity>()
                        .eq(CourseCategoryEntity::getCategoryName, dto.getCategoryName())
                        .ne(CourseCategoryEntity::getId, dto.getId()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXIST, "分类名称已存在");
        }

        // 更新分类
        BeanUtils.copyProperties(dto, entity);
        entity.setUpdateTime(LocalDateTime.now());

        categoryMapper.updateById(entity);
        log.info("更新课程分类成功：{}", dto.getCategoryName());
    }

    @Override
    public void delete(Long id) {
        // 检查分类是否存在
        CourseCategoryEntity entity = categoryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_EXIST);
        }

        // 收集该分类及所有子分类的 ID
        List<Long> allCategoryIds = collectCategoryAndDescendantIds(id);

        // 校验是否有课程关联到这些分类
        Long courseCount = courseMapper.selectCount(
                new LambdaQueryWrapper<com.example.onlineedu.domain.entity.CourseEntity>()
                        .in(com.example.onlineedu.domain.entity.CourseEntity::getCategoryId, allCategoryIds));
        if (courseCount > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,
                    "该分类或其子分类下存在课程，不能删除");
        }

        // 逻辑删除分类自身（子分类暂保留，可根据需要级联删除）
        categoryMapper.deleteById(id);

        log.info("删除课程分类成功：{}", entity.getCategoryName());
    }

    /**
     * 递归收集某分类及其所有后代分类的 ID
     */
    private List<Long> collectCategoryAndDescendantIds(Long parentId) {
        List<Long> ids = new java.util.ArrayList<>();
        ids.add(parentId);
        List<CourseCategoryEntity> children = categoryMapper.selectList(
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
    private CourseCategoryVO entityToVO(CourseCategoryEntity entity) {
        CourseCategoryVO vo = new CourseCategoryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * 构建树形结构
     */
    private List<CourseCategoryVO> buildTree(List<CourseCategoryVO> allCategories, Long parentId) {
        List<CourseCategoryVO> result = new ArrayList<>();

        for (CourseCategoryVO category : allCategories) {
            if (category.getParentId().equals(parentId)) {
                // 递归查找子分类
                List<CourseCategoryVO> children = buildTree(allCategories, category.getId());
                category.setChildren(children);
                result.add(category);
            }
        }

        return result;
    }
}
