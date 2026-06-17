package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper 接口
 *
 * @author feng
 */
@Mapper
public interface CourseMapper extends BaseMapper<CourseEntity> {

    /**
     * 分页查询课程评价统计列表（管理员/讲师端复用）
     *
     * @param page                 分页对象
     * @param courseName           课程名模糊搜索（null=不筛选）
     * @param teacherId            讲师ID筛选（null=不筛选，管理员专属）
     * @param avgScoreFilter       评分筛选：all/above4_5/above4_0/below3_5
     * @param goodReviewRateFilter 好评率筛选：all/above90/above80/below60
     * @param sortBy               排序：latestReview/reviewCount/scoreDesc/scoreAsc/goodRateDesc
     * @return CourseEntity 列表（含冗余统计字段）
     */
    List<CourseEntity> selectReviewStatPage(Page<CourseEntity> page,
                                            @Param("courseName") String courseName,
                                            @Param("teacherId") Long teacherId,
                                            @Param("avgScoreFilter") String avgScoreFilter,
                                            @Param("goodReviewRateFilter") String goodReviewRateFilter,
                                            @Param("sortBy") String sortBy);
}

