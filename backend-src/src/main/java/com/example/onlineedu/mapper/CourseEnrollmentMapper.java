package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.CourseEnrollmentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程报名 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollmentEntity> {
}
