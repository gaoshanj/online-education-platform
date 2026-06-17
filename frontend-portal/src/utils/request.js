import axios from 'axios'
import { ElMessage } from 'element-plus'

// 路由实例需延迟导入，避免循环依赖
let router = null
export function setRouter(r) {
    router = r
}

const request = axios.create({
    baseURL: '/api',
    timeout: 10000 // 修改由 10s 增加到 30s，以便容忍后端同步发邮件等慢速操作
})

// 请求拦截：注入 Token
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
})

// 响应拦截：统一错误处理
request.interceptors.response.use(
    res => {
        const data = res.data

        // 成功
        if (data.code === 200) {
            return data
        }

        // 未登录 / token失效
        if ([1005, 1006].includes(data.code)) {
            localStorage.removeItem('token')
            ElMessage.error('登录已过期，请重新登录')
            router?.push('/login')
            return Promise.reject(new Error(data.message))
        }

        // 其他业务错误
        ElMessage.error(data.message || '请求失败')
        return Promise.reject(new Error(data.message))
    },

    err => {
        // 后端未启动 / 网络断开
        if (!err.response) {
            ElMessage.error('无法连接服务器，请检查服务是否启动')
        }
        // 服务器异常
        else if (err.response.status === 500) {
            ElMessage.error('网络繁忙，请稍后再试')
        }
        else {
            ElMessage.error('请求异常')
        }

        return Promise.reject(err)
    }
)

export default request
