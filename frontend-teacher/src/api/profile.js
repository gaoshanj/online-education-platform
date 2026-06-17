import request from '@/utils/request'

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/common/user/info',
    method: 'get'
  })
}

// 修改当前用户信息
export function updateUserInfo(data) {
  return request({
    url: '/common/user/info',
    method: 'put',
    data: data
  })
}

// 修改密码
export function updatePassword(data) {
  return request({
    url: '/common/user/password',
    method: 'put',
    data: data
  })
}

// 发送更换邮箱验证码
export function sendUpdateEmailCode(email) {
  return request({
    url: '/common/user/send-update-email-code',
    method: 'post',
    params: { email }
  })
}

// 更换邮箱
export function updateEmail(data) {
  return request({
    url: '/common/user/email',
    method: 'put',
    data: data
  })
}

// 查看自己的认证信息（申请记录详情）
export function getCertificationInfo() {
  return request({
    url: '/teacher/info/certification',
    method: 'get'
  })
}

// 修改认证信息并重新提交审核
export function updateCertificationInfo(data) {
  return request({
    url: '/teacher/info/certification',
    method: 'put',
    data: data
  })
}
