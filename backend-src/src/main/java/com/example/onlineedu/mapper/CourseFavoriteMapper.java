package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.CourseFavoriteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 课程收藏 Mapper
 *
 * @author feng
 */
@Mapper
public interface CourseFavoriteMapper extends BaseMapper<CourseFavoriteEntity> {

    /**
     * 查询收藏记录（忽略逻辑删除过滤，含历史已取消的记录）
     * 用于 toggleFavorite 判断三态：从未收藏 / 已收藏 / 曾收藏后取消
     */
    @Select("SELECT * FROM course_favorite WHERE user_id = #{userId} AND course_id = #{courseId} LIMIT 1")
    CourseFavoriteEntity selectIgnoreDeleted(@Param("userId") Long userId,
                                             @Param("courseId") Long courseId);

    /**
     * 恢复已取消的收藏记录（原生 UPDATE，绕过 @TableLogic 自动拼接的 is_deleted=0 条件）
     *
     * @param id 收藏记录主键
     */
    @Update("UPDATE course_favorite SET is_deleted = 0, update_time = NOW() WHERE id = #{id}")
    void restoreFavorite(@Param("id") Long id);
}
