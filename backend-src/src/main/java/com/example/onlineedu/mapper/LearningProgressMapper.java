package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.LearningProgressEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgressEntity> {
}
