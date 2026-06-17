package com.example.onlineedu.service;

import com.example.onlineedu.domain.dto.CourseChapterDTO;
import com.example.onlineedu.domain.vo.CourseChapterTreeVO;

import java.util.List;

/**
 * 课程章节服务接口
 *
 * @author feng
 */
public interface CourseChapterService {

    /**
     * 添加章节
     *
     * @param dto 章节信息
     */
    void add(CourseChapterDTO dto);

    /**
     * 更新章节
     *
     * @param dto 章节信息
     */
    void update(CourseChapterDTO dto);

    /**
     * 删除章节
     *
     * @param id 章节ID
     */
    void delete(Long id);

    /**
     * 查询课程的章节树
     *
     * @param courseId 课程ID
     * @return 章节树
     */
    List<CourseChapterTreeVO> getChapterTree(Long courseId);

    /**
     * 调整章节排序
     *
     * @param id        章节ID
     * @param sortOrder 新的排序序号
     */
    void updateSort(Long id, Integer sortOrder);
}
