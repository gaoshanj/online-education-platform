package com.example.onlineedu.service;

import com.example.onlineedu.domain.dto.CourseCategoryDTO;
import com.example.onlineedu.domain.vo.CourseCategoryVO;

import java.util.List;

/**
 * 课程分类服务接口
 *
 * @author feng
 */
public interface CourseCategoryService {

    /**
     * 查询所有分类（树形结构）
     *
     * @return 分类列表
     */
    List<CourseCategoryVO> listAll();

    /**
     * 添加分类
     *
     * @param dto 分类信息
     */
    void add(CourseCategoryDTO dto);

    /**
     * 更新分类
     *
     * @param dto 分类信息
     */
    void update(CourseCategoryDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void delete(Long id);
}
