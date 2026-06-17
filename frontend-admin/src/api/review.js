import request from '@/utils/request'

// 获取课程评价统计列表
export function getReviewStatList(params) {
    return request.get('/admin/reviews/stat', { params })
}

// 获取某课程的评价统计摘要
export function getCourseReviewStatByCourseId(courseId) {
    return request.get(`/admin/reviews/stat/${courseId}`)
}

// 获取某课程的评价详情列表分页
export function getCourseReviewDetailList(params) {
    return request.get('/admin/reviews/list', { params })
}

// 管理员删除某条违规评价
export function deleteReview(id) {
    return request.delete(`/admin/reviews/${id}`)
}
