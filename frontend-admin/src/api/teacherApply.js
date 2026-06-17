import request from '@/utils/request'

// 分页查询讲师申请列表
export function getTeacherApplyPage(params) {
    return request.get('/admin/teacher-apply/page', { params })
}

// 查看申请详情
export function getTeacherApplyDetail(id) {
    return request.get(`/admin/teacher-apply/detail/${id}`)
}

// 审核通过
export function approveTeacherApply(id) {
    return request.put(`/admin/teacher-apply/approve/${id}`)
}

// 审核拒绝
export function rejectTeacherApply(id, rejectReason) {
    return request.put(`/admin/teacher-apply/reject/${id}`, null, { params: { rejectReason } })
}
