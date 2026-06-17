import request from '@/utils/request'

// 获取总览统计（课程数、学习人数、点赞、收藏、评价、平均评分）
export function getDashboardOverview() {
    return request({
        url: '/teacher/dashboard/overview',
        method: 'get'
    })
}

// 饼图：1-5 星评分分布与占比
export function getRatingDistribution() {
    return request({
        url: '/teacher/dashboard/rating-dist',
        method: 'get'
    })
}

// Top10 排行：学习人数 + 评分
export function getTopCourses() {
    return request({
        url: '/teacher/dashboard/top-courses',
        method: 'get'
    })
}

// 折线图：每日新增学员 - 每日活跃学习人数
export function getTrendData(params) {
    return request({
        url: '/teacher/dashboard/trend',
        method: 'get',
        params
    })
}
