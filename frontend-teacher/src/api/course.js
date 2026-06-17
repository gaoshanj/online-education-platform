import request from '@/utils/request'

/**
 * 分页查询讲师自己的课程列表
 * @param {Object} params {pageNum, pageSize, title, status}
 */
export function getCourseList(params) {
    return request({
        url: '/teacher/course/page',
        method: 'get',
        params
    })
}

/**
 * 根据ID查询课程基本信息
 * @param {Long} id 
 */
export function getCourseById(id) {
    return request({
        url: `/teacher/course/detail/${id}`,
        method: 'get'
    })
}

/**
 * 创建草稿课程
 * @param {Object} data 
 */
export function saveCourse(data) {
    return request({
        url: '/teacher/course/add',
        method: 'post',
        data
    })
}

/**
 * 更新课程（草稿-已拒绝-已下架状态可编辑）
 * @param {Object} data 
 */
export function updateCourse(data) {
    return request({
        url: '/teacher/course/update',
        method: 'put',
        data
    })
}

/**
 * 删除课程（仅草稿）
 * @param {Long} id 
 */
export function deleteCourse(id) {
    return request({
        url: `/teacher/course/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 提交审核
 * @param {Long} id 
 */
export function submitAudit(id) {
    return request({
        url: `/teacher/course/submit/${id}`,
        method: 'put'
    })
}

/**
 * 下架课程
 * @param {Long} id 
 */
export function offlineCourse(id) {
    return request({
        url: `/teacher/course/offline/${id}`,
        method: 'put'
    })
}
