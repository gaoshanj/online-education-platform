import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: safeJsonParse(localStorage.getItem('userInfo'), {}),
        roles: safeJsonParse(localStorage.getItem('roles'), [])
    }),
    actions: {
        setToken(token) {
            this.token = token
            localStorage.setItem('token', token)
        },
        setUserInfo(userInfo) {
            this.userInfo = userInfo
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
        },
        setRoles(roles) {
            this.roles = roles
            localStorage.setItem('roles', JSON.stringify(roles))
        },
        logout() {
            this.token = ''
            this.userInfo = {}
            this.roles = []
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('roles')
        }
    },
    getters: {
        isLoggedIn: (state) => !!state.token,
        isTeacher: (state) => state.roles.includes('TEACHER')
    }
})

function safeJsonParse(str, fallback) {
    try {
        if (!str || str === 'undefined' || str === 'null') {
            return fallback
        }
        return JSON.parse(str)
    } catch (e) {
        return fallback
    }
}
