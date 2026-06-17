package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.onlineedu.domain.entity.CourseReviewLikeEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 课程评价点赞 Mapper
 *
 * @author feng
 */
@Mapper
public interface CourseReviewLikeMapper extends BaseMapper<CourseReviewLikeEntity> {

    /**
     * 查询点赞记录（忽略逻辑删除过滤，含历史已取消的记录）
     * 用于 toggleLike 判断三态：从未点赞 / 已点赞 / 曾点赞后取消
     */
    @Select("SELECT * FROM course_review_like WHERE user_id = #{userId} AND review_id = #{reviewId} LIMIT 1")
    CourseReviewLikeEntity selectIgnoreDeleted(@Param("userId") Long userId,
                                               @Param("reviewId") Long reviewId);

    /**
     * 恢复已取消的点赞记录（原生 UPDATE，绕过 @TableLogic 自动拼接的 is_deleted=0 条件）
     */
    @Update("UPDATE course_review_like SET is_deleted = 0, update_time = NOW() WHERE id = #{id}")
    void restoreLike(@Param("id") Long id);

    /**
     * 物理删除某评价下的所有点赞记录（评价被删除时调用，绕过 @TableLogic）
     */
    @Delete("DELETE FROM course_review_like WHERE review_id = #{reviewId}")
    void physicalDeleteByReviewId(@Param("reviewId") Long reviewId);
}
