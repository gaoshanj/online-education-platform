import request from '@/utils/request'

/**
 * 获取会话列表（分页）
 * @param {Object} params - { page, size }
 */
export function getChatSessionPage(params) {
  return request.get('/app/chat/sessions', { params })
}

/**
 * 发送消息（如果 sessionId 未传会默认自动创建会话）
 * @param {Object} data - { content: String, sessionId?: Number }
 */
export function sendChatMessage(data) {
  return request.post('/app/chat/message', data)
}

/**
 * 轮询获取AI回复（AI回复完成前返回 null）
 * @param {Number} sessionId - 会话 ID
 */
export function getChatResult(sessionId) {
  return request.get(`/app/chat/result/${sessionId}`)
}

/**
 * 获取会话详情（聊天记录）
 * @param {Number} sessionId - 会话 ID
 */
export function getChatSessionDetail(sessionId) {
  return request.get(`/app/chat/session/${sessionId}`)
}

/**
 * 删除会话
 * @param {Number} sessionId - 会话 ID
 */
export function deleteChatSession(sessionId) {
  return request.delete(`/app/chat/session/${sessionId}`)
}
