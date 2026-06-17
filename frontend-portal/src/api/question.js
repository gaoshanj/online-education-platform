import request from '@/utils/request'

/**
 * 提交回答或评论（answerId 为 null/0 时为回答，>0 时为评论）
 * @param {Object} data - { answerId, content, questionId, targetReplyId, targetUserId }
 */
export function submitAnswer(data) {
  return request.post('/app/question/answer', data)
}

/**
 * 点赞或取消点赞回答/评论
 * @param {Number} id - answer / reply ID
 */
export function toggleLike(id) {
  return request.post(`/app/question/answer/like/${id}`)
}

/**
 * 获取某回答下的二级评论列表
 * @param {Number} answerId - 一级回答 ID
 */
export function getReplies(answerId) {
  return request.get(`/app/question/answer/replies/${answerId}`)
}

/**
 * 删除回答或评论（仅本人）
 * @param {Number} id - answer / reply ID
 */
export function deleteAnswer(id) {
  return request.delete(`/app/question/answer/${id}`)
}

/**
 * 提问
 * @param {Object} data - { content, courseId, title }
 */
export function askQuestion(data) {
  return request.post('/app/question/ask', data)
}

/**
 * 查询课程问题列表
 * @param {Number} courseId 
 * @param {Object} params - { onlyMine, page, size }
 */
export function getQuestionList(courseId, params) {
  return request.get(`/app/question/course/${courseId}`, { params })
}

/**
 * 删除问题（仅提问者）
 * @param {Number} id - question ID
 */
export function deleteQuestion(id) {
  return request.delete(`/app/question/delete/${id}`)
}

/**
 * 查询问题详情
 * @param {Number} id - question ID
 */
export function getQuestionDetail(id) {
  return request.get(`/app/question/detail/${id}`)
}

/**
 * 更新问题（仅提问者）
 * @param {Object} data - { id, courseId, title, content }
 */
export function updateQuestion(data) {
  return request.put('/app/question/update', data)
}
