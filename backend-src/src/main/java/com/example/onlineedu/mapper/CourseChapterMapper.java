package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.CourseChapterEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程章节 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapterEntity> {
}
