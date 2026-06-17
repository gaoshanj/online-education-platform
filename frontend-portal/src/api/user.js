import request from '@/utils/request'

/** 
 * 获取当前用户信息 
 * (由于之前 auth 包有这个接口，也可以复用或者移动过来。依据文档地址也是相同的)
 */
export function getUserInfo() {
    return request.get('/common/user/info')
}

/**
 * 修改当前用户基本信息
 * @param {Object} data - { avatar, gender, nickname } 排除 phone
 */
export function updateUserInfo(data) {
    return request.put('/common/user/info', data)
}

/**
 * 修改密码
 * @param {Object} data - { newPassword, oldPassword }
 */
export function updatePassword(data) {
    return request.put('/common/user/password', data)
}


/**
 * 找回密码重置
 * @param {Object} data - { code, email, newPassword }
 */
export function resetPassword(data) {
    return request.post('/common/user/forgot-password', data)
}

/**
 * 发送更换邮箱验证码
 * @param {String} email 
 */
export function sendUpdateEmailCode(email) {
    return request.post('/common/user/send-update-email-code', null, { params: { email } })
}

/**
 * 更换邮箱
 * @param {Object} data - { code, email }
 */
export function updateEmail(data) {
    return request.put('/common/user/email', data)
}
