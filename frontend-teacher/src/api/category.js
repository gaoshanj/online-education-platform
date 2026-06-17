import request from '@/utils/request'

/**
 * 获取所有的课程分类（树形结构）
 */
export function getCategoryTree() {
    return request({
        url: '/admin/category/list',
        method: 'get'
    })
}
