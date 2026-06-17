import request from '@/utils/request'

/**
 * 查询课程章节树
 * @param {Long} courseId 
 */
export function getChapterTree(courseId) {
    return request({
        url: `/teacher/chapter/tree/${courseId}`,
        method: 'get'
    })
}

export function saveOrUpdateChapter(data) {
    if (data.id) {
        return request({
            url: '/teacher/chapter/update',
            method: 'put',
            data
        })
    }
    return request({
        url: '/teacher/chapter/add',
        method: 'post',
        data
    })
}

/**
 * 删除章节
 * @param {Long} id 
 */
export function deleteChapter(id) {
    return request({
        url: `/teacher/chapter/delete/${id}`,
        method: 'delete'
    })
}

/**
 * 调整章节排序
 * @param {Long} id 
 * @param {Integer} sortOrder 
 */
export function sortChapter(id, sortOrder) {
    return request({
        url: '/teacher/chapter/sort',
        method: 'put',
        params: { id, sortOrder }
    })
}
