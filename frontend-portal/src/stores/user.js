import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin, logout as apiLogout, getUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(null)

    /** 登录：调接口 → 存 token → 拉用户信息 */
    async function login(credentials) {
        const res = await apiLogin(credentials)
        token.value = res.data.token
        localStorage.setItem('token', res.data.token)
        // 真实响应结构：{ token, userInfo: {...}, roles: [] }
        userInfo.value = {
            ...res.data.userInfo,
            roles: res.data.roles
        }
        return res
    }

    /** 拉取完整用户信息 */
    async function fetchUserInfo() {
        const res = await getUserInfo()
        userInfo.value = res.data
        return res.data
    }

    /** 注销 */
    async function logout() {
        try {
            await apiLogout()
        } catch (_) {
            // 注销失败也要清除本地状态
        } finally {
            token.value = ''
            userInfo.value = null
            localStorage.removeItem('token')
        }
    }

    const isLoggedIn = () => !!token.value

    return { token, userInfo, login, logout, fetchUserInfo, isLoggedIn }
})
