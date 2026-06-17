package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.AiChatSessionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天会话Mapper接口
 *
 * @author feng
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSessionEntity> {
}
