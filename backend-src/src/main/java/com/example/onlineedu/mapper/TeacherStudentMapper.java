package com.example.onlineedu.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 讲师端学员管理 Mapper
 * 只需一个简单查询得到学员分页列表（User_id + 最近学习时间），
 * 其余统计字段全部在 Service 层用 MyBatis-Plus 条件查询后 Java 聚合。
 *
 * @author feng
 */
@Mapper
public interface TeacherStudentMapper {

    /**
     * 分页查询讲师的学员用户ID列表（已去重，支持昵称搜索和时间筛选）
     *
     * @param page       分页对象（仅用于 total 统计）
     * @param teacherId  讲师ID
     * @param nickname   昵称模糊搜索（null=不筛选）
     * @param learnStart 最近学习时间-开始（null=不限制）
     * @param learnEnd   最近学习时间-结束（null=不限制）
     * @param sortBy     排序：latestLearn/courseCount/duration/completionRate
     * @return 学员用户ID列表（分页）
     */
    List<Long> selectStudentUserIdPage(Page<Long> page,
                                       @Param("teacherId") Long teacherId,
                                       @Param("nickname") String nickname,
                                       @Param("learnStart") LocalDateTime learnStart,
                                       @Param("learnEnd") LocalDateTime learnEnd,
                                       @Param("sortBy") String sortBy);
}
