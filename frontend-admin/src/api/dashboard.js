import request from '@/utils/request'

// 全平台总览统计（用户数-讲师数-课程数-学习人数-评价数-平均评分）
export function getDashboardOverview() {
    return request.get('/admin/dashboard/overview')
}

// 饼图：全平台课程评分分布
export function getRatingDistribution() {
    return request.get('/admin/dashboard/rating-dist')
}

// Top10 课程排行
export function getTopCourses(params) {
    return request.get('/admin/dashboard/top-courses', { params })
}

// 折线图：每日指标趋势
export function getTrendData(params) {
    return request.get('/admin/dashboard/trend', { params })
}

// 折线图：累计数据（自平台启动日起）
export function getCumulativeData() {
    return request.get('/admin/dashboard/cumulative')
}
