package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.vo.TeacherStudentDetailVO;
import com.example.onlineedu.domain.vo.TeacherStudentListVO;

import java.time.LocalDateTime;

/**
 * 讲师端学员管理服务接口
 *
 * @author feng
 */
public interface TeacherStudentService {

    /**
     * 分页查询讲师的学员列表（用户维度）
     *
     * @param teacherId  讲师ID
     * @param nickname   昵称模糊搜索（null=不筛选）
     * @param learnStart 最近学习时间-开始（null=不限制）
     * @param learnEnd   最近学习时间-结束（null=不限制）
     * @param sortBy     排序：latestLearn（默认）/ courseCount / duration / completionRate
     * @param page       当前页
     * @param size       每页大小
     */
    Page<TeacherStudentListVO> listStudents(Long teacherId,
                                            String nickname,
                                            LocalDateTime learnStart,
                                            LocalDateTime learnEnd,
                                            String sortBy,
                                            Integer page, Integer size);

    /**
     * 分页查询学员在该讲师课程下的学习详情（课程维度）
     *
     * @param teacherId   讲师ID（限制只看自己课程）
     * @param studentId   学员用户ID
     * @param courseName  课程名称模糊搜索（null=不筛选）
     * @param isCompleted 完成状态（null=全部，0=未完成，1=已完成）
     * @param sortBy      排序：lastLearnTime（默认）/ completionRate / duration / enrollTime
     * @param page        当前页
     * @param size        每页大小
     */
    Page<TeacherStudentDetailVO> listStudentCourses(Long teacherId,
                                                    Long studentId,
                                                    String courseName,
                                                    Integer isCompleted,
                                                    String sortBy,
                                                    Integer page, Integer size);
}
