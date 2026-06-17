import request from '@/utils/request'

// 分页查询讲师
export function getTeacherPage(params) {
    return request.get('/admin/teacher/page', { params })
}

// 查询讲师详情
export function getTeacherDetail(id) {
    return request.get(`/admin/teacher/detail/${id}`)
}

// 查询讲师的所有课程
export function getTeacherCourses(id) {
    return request.get(`/admin/teacher/courses/${id}`)
}

// 设置讲师身份
export function setTeacherRole(userId) {
    return request.put(`/admin/teacher/set-role/${userId}`)
}

// 取消讲师身份
export function removeTeacherRole(userId) {
    return request.put(`/admin/teacher/remove-role/${userId}`)
}
