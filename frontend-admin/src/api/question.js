import request from '@/utils/request'

// 分页查询全平台问题（内容审核）
export function getQuestionPage(params) {
    return request.get('/admin/question/page', { params })
}

// 查询问题详情及其所有回答
export function getQuestionDetail(id) {
    return request.get(`/admin/question/detail/${id}`)
}

// 获取某回答下的二级评论列表
export function getReplies(answerId) {
    return request.get(`/admin/question/answer/replies/${answerId}`)
}

// 删除违规问题（级联删除相关所有互动）
export function deleteQuestion(id) {
    return request.delete(`/admin/question/delete/${id}`)
}

// 删除违规回答或评论（包含此评论下方的所有回复）
export function deleteAnswer(id) {
    return request.delete(`/admin/question/answer/${id}`)
}
