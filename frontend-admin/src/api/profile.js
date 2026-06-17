import request from '@/utils/request'

export function updateUserInfo(data) {
  return request({
    url: '/common/user/info',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/common/user/password',
    method: 'put',
    data
  })
}

export function sendUpdateEmailCode(email) {
  return request({
    url: '/common/user/send-update-email-code',
    method: 'post',
    params: { email }
  })
}

export function updateEmail(data) {
  return request({
    url: '/common/user/email',
    method: 'put',
    data
  })
}
