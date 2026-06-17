import request from '@/utils/request'

// 分页查询轮播图列表
export function getBannerPage(page = 1, size = 10) {
    return request.get('/admin/banner/page', { params: { page, size } })
}

// 新增轮播图
export function createBanner(data) {
    return request.post('/admin/banner', data)
}

// 修改轮播图
export function updateBanner(id, data) {
    return request.put(`/admin/banner/${id}`, data)
}

// 删除轮播图
export function deleteBanner(id) {
    return request.delete(`/admin/banner/${id}`)
}

// 启用轮播图
export function enableBanner(id) {
    return request.put(`/admin/banner/${id}/enable`)
}

// 禁用轮播图
export function disableBanner(id) {
    return request.put(`/admin/banner/${id}/disable`)
}

// 分页查询可关联的课程
export function getBannerCoursePage(page = 1, size = 10, categoryId = null, courseName = null) {
    return request.get('/admin/banner/course/page', {
        params: {
            page,
            size,
            categoryId,
            courseName
        }
    })
}
