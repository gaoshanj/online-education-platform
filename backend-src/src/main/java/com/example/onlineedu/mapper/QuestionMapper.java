package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.QuestionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答Mapper接口
 *
 * @author feng
 */
@Mapper
public interface QuestionMapper extends BaseMapper<QuestionEntity> {
}
