package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.TeacherApplicationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讲师申请 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface TeacherApplicationMapper extends BaseMapper<TeacherApplicationEntity> {
}
