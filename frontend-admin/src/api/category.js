import request from '@/utils/request'

// 查询所有分类（树形结构）
export function getCategoryList() {
    return request.get('/admin/category/list')
}

// 添加分类
export function createCategory(data) {
    return request.post('/admin/category/add', data)
}

// 更新分类
export function updateCategory(data) {
    return request.put('/admin/category/update', data)
}

// 删除分类
export function deleteCategory(id) {
    return request.delete(`/admin/category/delete/${id}`)
}
