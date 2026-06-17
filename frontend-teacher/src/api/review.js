import request from '@/utils/request'

/**
 * 分页查询课程评价统计列表
 * @param {Object} params {courseName, avgScoreFilter, goodReviewRateFilter, sortBy, page, size}
 */
export function getReviewStatList(params) {
    return request({
        url: '/teacher/reviews/stat',
        method: 'get',
        params
    })
}

/**
 * 获取课程评分统计（详情页）
 * @param {Long} courseId 
 */
export function getCourseReviewStatByCourseId(courseId) {
    return request({
        url: `/teacher/reviews/stat/${courseId}`,
        method: 'get'
    })
}

/**
 * 获取课程评价列表（详情页）
 * @param {Object} params {courseId, page, size, rating, sortType}
 */
export function getCourseReviewDetailList(params) {
    return request({
        url: '/teacher/reviews/list',
        method: 'get',
        params
    })
}
