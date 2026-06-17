import request from '@/utils/request'

// 分页查询全平台课程
export function getCoursePage(params) {
    return request.get('/admin/course/page', { params })
}

// 查询课程详情
export function getCourseDetail(id) {
    return request.get(`/admin/course/detail/${id}`)
}

// 审核通过
export function approveCourse(id) {
    return request.put(`/admin/course/approve/${id}`)
}

// 审核拒绝
export function rejectCourse(id, rejectReason) {
    return request.put(`/admin/course/reject/${id}`, null, { params: { rejectReason } })
}

// 强制下架违规课程
export function offlineCourse(id) {
    return request.put(`/admin/course/offline/${id}`)
}

// 删除课程
export function deleteCourse(id) {
    return request.delete(`/admin/course/delete/${id}`)
}

// 查询课程章节树
export function getChapterTree(courseId) {
    return request.get(`/admin/chapter/tree/${courseId}`)
}

// 设置/取消精品课程
export function recommendCourse(id, isRecommend) {
    return request.put(`/admin/course/recommend/${id}`, null, { params: { isRecommend } })
}
