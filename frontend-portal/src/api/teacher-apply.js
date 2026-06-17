import request from '@/utils/request'

/**
 * 获取我的申请状态
 */
export function getMyApplyStatus() {
    return request.get('/app/teacher-apply/my-status')
}

/**
 * 首次提交讲师申请
 * @param {Object} data - 申请资料
 */
export function submitApply(data) {
    return request.post('/app/teacher-apply/submit', data)
}

/**
 * 修改申请信息并重新提交（待审核/已拒绝转为修改待审）
 * @param {Object} data - 申请资料
 */
export function updateApply(data) {
    return request.put('/app/teacher-apply/update', data)
}
