import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        // 从 localStorage 获取 Token
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        const res = response.data
        // 假设后端统一返回 code 200 为成功
        if (res.code === 200 || res.code === 0) {
            return res.data
        } else {
            ElMessage.error(res.message || '系统错误')
            return Promise.reject(new Error(res.message || 'Error'))
        }
    },
    (error) => {
        console.error('Response Error:', error)
        const { response } = error
        if (response) {
            switch (response.status) {
                case 401:
                    ElMessage.error('登录已过期，请重新登录')
                    localStorage.removeItem('token')
                    localStorage.removeItem('userInfo')
                    window.location.href = '/login'
                    break
                case 403:
                    ElMessage.error('无权访问')
                    break
                case 404:
                    ElMessage.error('请求地址不存在')
                    break
                default:
                    ElMessage.error(response.data?.message || '网络繁忙，请稍后再试')
            }
        } else {
            ElMessage.error('网络异常，请检查您的网络连接')
        }
        return Promise.reject(error)
    }
)

export default request
