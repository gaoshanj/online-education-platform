package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.CourseReviewAggDTO;
import com.example.onlineedu.domain.entity.CourseReviewEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程评价 Mapper
 *
 * @author feng
 */
@Mapper
public interface CourseReviewMapper extends BaseMapper<CourseReviewEntity> {

    /**
     * 分页查询评价列表（支持排序、评分筛选、我的评价筛选）
     *
     * @param page     分页对象
     * @param courseId 课程ID
     * @param rating   评分筛选（null=不筛选）
     * @param userId   筛选我的评价时传入用户ID（null=不筛选）
     * @param sortType 排序方式：latest=按创建时间倒序，hot=按点赞数倒序
     * @return 评价实体列表
     */
    List<CourseReviewEntity> selectReviewPage(Page<CourseReviewEntity> page,
                                              @Param("courseId") Long courseId,
                                              @Param("rating") Integer rating,
                                              @Param("userId") Long userId,
                                              @Param("sortType") String sortType);

    /**
     * 聚合查询单课程评价统计（count/avg/好评数/最新时间）
     * 供 refreshCourseReviewStat 使用，对应 CourseReviewMapper.xml 中的 selectReviewStatAgg
     */
    CourseReviewAggDTO selectReviewStatAgg(@Param("courseId") Long courseId);

    /**
     * 物理删除评价（绕过 @TableLogic，执行真正的 DELETE）
     */
    @Delete("DELETE FROM course_review WHERE id = #{id}")
    int physicalDeleteById(@Param("id") Long id);
}
