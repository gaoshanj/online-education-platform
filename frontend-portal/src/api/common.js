import request from '@/utils/request'

/**
 * 通用图片上传
 * @param {File} file - 原生 File 对象
 * @returns {Promise} 返回上传结果的 Promise
 */
export function uploadImage(file) {
    // 构建 formData
    const formData = new FormData()
    formData.append('file', file)

    return request.post('/common/file/upload/image', formData, {
        // 覆盖全局由于 qs 等造成的影响，确保传输的是 FormData
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
