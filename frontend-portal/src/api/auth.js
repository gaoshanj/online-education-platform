import request from '@/utils/request'

/** 用户登录 */
export function login(data) {
    return request.post('/auth/login', data)
}

/** 用户注册 */
export function register(data) {
    return request.post('/auth/register', data)
}

/** 
 * 发送注册验证码 
 */
export function sendRegisterCode(email) {
    return request.post('/auth/send-email-code', null, { params: { email, scene: 'register' } })
}

/** 
 * 发送找回密码验证码 
 */
export function sendForgotCode(email) {
    return request.post('/auth/send-email-code', null, { params: { email, scene: 'forgot' } })
}

/** 用户注销 */
export function logout() {
    return request.post('/auth/logout')
}

/** 获取当前用户信息 */
export function getUserInfo() {
    return request.get('/auth/info')
}
