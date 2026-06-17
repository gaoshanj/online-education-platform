package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.CourseCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程分类 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategoryEntity> {
}
