import request from '@/utils/request'

/**
 * 上传图片
 * @param {File} file 
 */
export function uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/common/file/upload/image',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 上传视频
 * @param {File} file 
 */
export function uploadVideo(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/common/file/upload/video',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data' // axios会自动处理boundary
        }
    })
}
