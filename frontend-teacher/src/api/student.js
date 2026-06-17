import request from '@/utils/request'

/**
 * 讲师端查询学员列表
 * @param {Object} params {learnEnd, learnStart, nickname, page, size, sortBy}
 */
export function getStudentList(params) {
    return request({
        url: '/teacher/students',
        method: 'get',
        params
    })
}

/**
 * 讲师端查询学员学习详情（课程维度）
 * @param {Long} userId 学员ID
 * @param {Object} params {courseName, isCompleted, page, size, sortBy}
 */
export function getStudentDetailList(userId, params) {
    return request({
        url: `/teacher/students/${userId}`,
        method: 'get',
        params
    })
}
