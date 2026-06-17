import request from '@/utils/request'

/**
 * 分页查询讲师自己课程的问题列表
 * @param {Object} params {courseId, hasTeacherAnswer, page, size}
 */
export function getQuestionPage(params) {
    return request({
        url: '/teacher/question/page',
        method: 'get',
        params
    })
}

/**
 * 查询问题详情（包含回复树）
 * @param {Long} id 
 */
export function getQuestionDetail(id) {
    return request({
        url: `/teacher/question/detail/${id}`,
        method: 'get'
    })
}

/**
 * 讲师回答问题
 * @param {Object} data { answerId, content, questionId, targetReplyId, targetUserId }
 */
export function answerQuestion(data) {
    return request({
        url: '/teacher/question/answer',
        method: 'post',
        data
    })
}

/**
 * 获取某回答下的二级评论列表
 * @param {Long} answerId 
 */
export function getAnswerReplies(answerId) {
    return request({
        url: `/teacher/question/answer/replies/${answerId}`,
        method: 'get'
    })
}
