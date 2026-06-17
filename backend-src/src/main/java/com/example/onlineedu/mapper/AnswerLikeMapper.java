package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.AnswerLikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 回答点赞 Mapper
 *
 * @author feng
 */
@Mapper
public interface AnswerLikeMapper extends BaseMapper<AnswerLikeEntity> {

    /**
     * 查询点赞记录（忽略逻辑删除过滤，含历史已取消的记录）
     * 用于 toggleLike 判断三态：从未点赞 / 已点赞 / 曾点赞后取消
     */
    @Select("SELECT * FROM answer_like WHERE user_id = #{userId} AND answer_id = #{answerId} LIMIT 1")
    AnswerLikeEntity selectIgnoreDeleted(@Param("userId") Long userId,
                                        @Param("answerId") Long answerId);

    /**
     * 恢复已取消的点赞记录（原生 UPDATE，绕过 @TableLogic 自动拼接的 is_deleted=0 条件）
     *
     * @param id 点赞记录主键
     */
    @Update("UPDATE answer_like SET is_deleted = 0, update_time = NOW() WHERE id = #{id}")
    void restoreLike(@Param("id") Long id);
}

