package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.AiChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI聊天消息Mapper接口
 *
 * @author feng
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessageEntity> {
}
