package com.example.onlineedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.onlineedu.domain.dto.TeacherApplicationDTO;
import com.example.onlineedu.domain.vo.TeacherApplicationVO;

/**
 * 讲师申请服务接口
 *
 * @author feng
 */
public interface TeacherApplicationService {

    /**
     * 提交讲师申请
     *
     * @param dto    申请信息
     * @param userId 用户ID
     */
    void submitApplication(TeacherApplicationDTO dto, Long userId);

    /**
     * 查询我的申请状态
     *
     * @param userId 用户ID
     * @return 申请信息
     */
    TeacherApplicationVO getMyApplication(Long userId);

    /**
     * 分页查询申请列表（管理端）
     *
     * @param page   当前页
     * @param size   每页大小
     * @param status 状态（可选）
     * @return 分页结果
     */
    Page<TeacherApplicationVO> pageApplications(Integer page, Integer size, Integer status);

    /**
     * 查询申请详情
     *
     * @param id 申请ID
     * @return 申请详情
     */
    TeacherApplicationVO getDetail(Long id);

    /**
     * 审核通过
     *
     * @param id        申请ID
     * @param auditorId 审核人ID
     */
    void approve(Long id, Long auditorId);

    /**
     * 审核拒绝
     *
     * @param id           申请ID
     * @param auditorId    审核人ID
     * @param rejectReason 拒绝原因
     */
    void reject(Long id, Long auditorId, String rejectReason);

    /**
     * 用户修改申请信息并重新提交（状态：待审核(0)/已拒绝(2) → 待审核(0)）
     *
     * @param dto    新的申请信息
     * @param userId 当前用户ID
     */
    void updateApplication(TeacherApplicationDTO dto, Long userId);

    /**
     * 讲师修改认证信息并重新审核（状态：→ 修改待审核(3)）
     *
     * @param dto    新的认证信息
     * @param userId 当前讲师用户ID
     */
    void updateByTeacher(TeacherApplicationDTO dto, Long userId);
}
