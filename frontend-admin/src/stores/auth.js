import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi, getUserInfo } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(null)

    // 登录
    async function login(data) {
        const res = await loginApi(data)
        token.value = res.data.token
        userInfo.value = res.data.userInfo
        localStorage.setItem('token', res.data.token)
        return res
    }

    // 注销
    async function logout() {
        try {
            await logoutApi()
        } finally {
            token.value = ''
            userInfo.value = null
            localStorage.removeItem('token')
        }
    }

    // 获取当前用户信息
    async function fetchUserInfo() {
        const res = await getUserInfo()
        userInfo.value = res.data
        return res.data
    }

    return { token, userInfo, login, logout, fetchUserInfo }
})
