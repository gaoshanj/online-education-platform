import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器：自动携带 token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：统一处理业务错误
request.interceptors.response.use(
    response => {
        const res = response.data

        // 成功
        if (res.code === 200) {
            return res
        }

        // token失效 / 未登录
        if (res.code === 1005 || res.code === 1006) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            window.location.href = '/login'
            return Promise.reject(new Error(res.message))
        }

        // 其他业务错误
        ElMessage.error(res.message || '请求失败')
        return Promise.reject(new Error(res.message))
    },
    error => {
        // 后端未启动 / 网络断开
        if (!error.response) {
            ElMessage.error('无法连接服务器，请检查服务是否启动')
        }
        // 服务器异常
        else if (error.response.status === 500) {
            ElMessage.error('网络繁忙，请稍后再试')
        }
        else {
            ElMessage.error('请求异常')
        }

        return Promise.reject(error)
    }
)

export default request
