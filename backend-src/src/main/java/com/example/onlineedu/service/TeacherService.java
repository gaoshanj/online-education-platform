package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.vo.CourseVO;
import com.example.onlineedu.domain.vo.TeacherVO;

import java.util.List;

/**
 * 讲师服务接口
 *
 * @author feng
 */
public interface TeacherService {

    /**
     * 分页查询讲师
     *
     * @param page 当前页
     * @param size 每页大小
     * @param name 讲师名称（可选）
     * @return 分页结果
     */
    Page<TeacherVO> pageTeachers(Integer page, Integer size, String name);

    /**
     * 查询讲师详情
     *
     * @param teacherId 讲师ID
     * @return 讲师详情
     */
    TeacherVO getTeacherDetail(Long teacherId);

    /**
     * 查询讲师的所有课程
     *
     * @param teacherId 讲师ID
     * @return 课程列表
     */
    List<CourseVO> getTeacherCourses(Long teacherId);

    /**
     * 设置讲师身份（添加TEACHER角色）
     *
     * @param userId 用户ID
     */
    void setTeacherRole(Long userId);

    /**
     * 取消讲师身份（移除TEACHER角色）
     *
     * @param userId 用户ID
     */
    void removeTeacherRole(Long userId);
}
