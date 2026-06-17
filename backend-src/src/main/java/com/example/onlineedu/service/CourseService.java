package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.CourseDTO;
import com.example.onlineedu.domain.vo.CourseVO;

import java.util.List;

/**
 * 课程服务接口
 *
 * @author feng
 */
public interface CourseService {

    /**
     * 创建草稿课程，返回新课程ID
     *
     * @param dto 课程信息
     * @return 课程ID
     */
    Long add(CourseDTO dto);

    /**
     * 更新课程
     *
     * @param dto 课程信息
     */
    void update(CourseDTO dto);

    /**
     * 发布课程
     *
     * @param id 课程ID
     */
    void publish(Long id);

    /**
     * 下架课程
     *
     * @param id 课程ID
     */
    void offline(Long id);

    /**
     * 删除课程
     *
     * @param id 课程ID
     */
    void delete(Long id);

    /**
     * 分页查询课程（管理端）
     *
     * @param page         当前页
     * @param size         每页大小
     * @param courseName   课程名称（可选）
     * @param categoryId   分类ID（可选）
     * @param status       状态（可选）
     * @param isRecommend  是否精品：1=精品/0=普通（可选）
     * @param orderBy      排序：hot=热度, time=最新（不传=默认创建time降序）
     * @return 分页结果
     */
    Page<CourseVO> page(Integer page, Integer size, String courseName, Long categoryId,
                        Integer status, Integer isRecommend, String orderBy);

    /**
     * 查询课程详情
     *
     * @param id 课程ID
     * @return 课程详情
     */
    CourseVO getDetail(Long id);

    /**
     * 用户端分页查询课程（支持搜索、排序）
     *
     * @param page       当前页
     * @param size       每页大小
     * @param categoryId 分类ID（可选）
     * @param keyword    关键词（可选）
     * @param orderBy    排序方式（hot/time）
     * @return 分页结果
     */
    Page<CourseVO> pageForApp(Integer page, Integer size, Long categoryId, String keyword, String orderBy);


    /**
     * 增加课程浏览数
     *
     * @param courseId 课程ID
     */
    void increaseViewCount(Long courseId);

    /**
     * 提交审核（讲师操作：草稿/已拒绝 → 待审核）
     *
     * @param id 课程ID
     */
    void submitForReview(Long id);

    /**
     * 审核通过（管理员操作：待审核 → 已发布）
     *
     * @param id 课程ID
     */
    void approve(Long id);

    /**
     * 审核拒绝（管理员操作：待审核 → 审核拒绝）
     *
     * @param id           课程ID
     * @param rejectReason 拒绝原因
     */
    void reject(Long id, String rejectReason);

    /**
     * 管理员强制下架（不校验归属）
     *
     * @param id 课程ID
     */
    void forceOffline(Long id);

//    /**
//     * 讲师专属分页查询（仅查自己的课程）
//     *
//     * @param page         当前页
//     * @param size         每页大小
//     * @param courseName   课程名称（可选）
//     * @param categoryId   分类ID（可选）
//     * @param status       状态（可选）
//     * @param isRecommend  是否精品（可选）
//     * @param orderBy      排序方式（可选）
//     * @return 分页结果
//     */
//    Page<CourseVO> pageByTeacher(Integer page, Integer size, String courseName, Long categoryId,
//                                 Integer status, Integer isRecommend, String orderBy);

    /**
     * 设置/取消精品课程（管理块操作）
     *
     * @param id          课程ID
     * @param isRecommend 1=设为精品, 0=取消精品
     */
    void setRecommend(Long id, Integer isRecommend);
}
