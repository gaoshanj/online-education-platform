package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.LearningProgressEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 学习进度 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgressEntity> {

    /**
     * 查询某门课程所有学员的累计学习时长（秒）
     */
    @Select("SELECT COALESCE(SUM(duration), 0) FROM learning_progress WHERE course_id = #{courseId} AND is_deleted = 0")
    Integer sumDurationByCourseId(@Param("courseId") Long courseId);
}
