import request from '@/utils/request'

// 分页查询用户
export function getUserPage(params) {
    return request.get('/admin/user/page', { params })
}

// 查询用户详情
export function getUserDetail(id) {
    return request.get(`/admin/user/detail/${id}`)
}

// 启用用户
export function enableUser(id) {
    return request.put(`/admin/user/enable/${id}`)
}

// 禁用用户
export function disableUser(id) {
    return request.put(`/admin/user/disable/${id}`)
}

// 创建用户
export function createUser(data) {
    return request.post('/admin/user/create', data)
}

// 修改用户角色
export function updateUserRoles(id, data) {
    return request.put(`/admin/user/${id}/roles`, data)
}

// 删除用户
export function deleteUser(id) {
    return request.delete(`/admin/user/delete/${id}`)
}
