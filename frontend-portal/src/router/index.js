import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/layouts/AppLayout.vue'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 无布局：登录
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/auth/LoginView.vue'),
            meta: { hideLayout: true }
        },
        {
            path: '/forgot-password',
            name: 'forgotPassword',
            component: () => import('@/views/auth/ForgotPasswordView.vue'),
            meta: { hideLayout: true }
        },
        // 有布局：主应用
        {
            path: '/',
            component: AppLayout,
            children: [
                {
                    path: '',
                    name: 'home',
                    component: () => import('@/views/HomeView.vue')
                },
                {
                    path: 'courses',
                    name: 'courses',
                    component: () => import('@/views/course/CourseListView.vue')
                },
                {
                    path: 'course/:id',
                    name: 'courseDetail',
                    component: () => import('@/views/course/CourseDetailView.vue')
                },
                {
                    path: 'ai',
                    name: 'ai',
                    component: () => import('@/views/ai/AIAssistantView.vue')
                },
                {
                    path: 'learning',
                    name: 'learningCenter',
                    component: () => import('@/views/user/LearningCenterView.vue'),
                    meta: { requireAuth: true }
                },
                {
                    path: 'teacher-apply',
                    name: 'teacherApplyLanding',
                    component: () => import('@/views/teacher/TeacherApplyLandingView.vue')
                },
                {
                    path: 'teacher-apply/form',
                    name: 'teacherApplyForm',
                    component: () => import('@/views/teacher/TeacherApplyView.vue'),
                    meta: { requireAuth: true }
                }
            ]
        },
        // 管理端预留
        {
            path: '/admin',
            name: 'admin',
            component: () => import('@/views/admin/AdminLayout.vue')
        }
    ],
    scrollBehavior(to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        }
        // 如果仅仅是 query/hash 的改变（如同一个课程详情页切换tab），保持原位
        if (to.path === from.path) {
            return false
        }
        return { top: 0 }
    }
})

// 路由守卫：已登录状态访问 /login /register 自动跳首页，以及需要登录页面的拦截
router.beforeEach((to, from, next) => {
    // 【修复】切换路由时，清除可能因为组件强制卸载而遗留的弹窗遮罩和样式锁
    // （当有 ElDialog 或 ElDrawer 打开时发生页面强制重定向，如 Token 过期，容易导致遮罩层遗留）
    document.body.classList.remove('el-popup-parent--hidden')
    const modals = document.querySelectorAll('.v-modal')
    modals.forEach(modal => {
        if (modal.parentNode) {
            modal.parentNode.removeChild(modal)
        }
    })

    // 尝试关闭任何可能未自动关闭的 MessageBox
    import('element-plus').then(({ ElMessageBox }) => {
        if (ElMessageBox && ElMessageBox.close) {
            ElMessageBox.close()
        }
    }).catch(() => { })

    const token = localStorage.getItem('token')
    document.title = '企业AI + 云能力人才培养平台'

    // 如果想要统一做拦截，可在这里对 requireAuth 做处理
    if (to.meta.requireAuth && !token) {
        import('element-plus').then(({ ElMessage }) => {
            ElMessage.warning('请先登录后再操作')
        })
        next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
        return
    }

    if (token && to.name === 'login') {
        next({ name: 'home' })
    } else {
        next()
    }
})

export default router
