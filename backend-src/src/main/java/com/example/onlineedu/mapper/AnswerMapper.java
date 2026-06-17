package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.AnswerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回答Mapper接口
 *
 * @author feng
 */
@Mapper
public interface AnswerMapper extends BaseMapper<AnswerEntity> {
}
