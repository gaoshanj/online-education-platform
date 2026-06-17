import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/login.vue'),
        meta: { title: '登录', public: true }
    },
    {
        path: '/forgot-password',
        name: 'ForgotPassword',
        component: () => import('@/views/auth/forgot-password.vue'),
        meta: { title: '找回密码', public: true }
    },
    {
        path: '/',
        component: () => import('@/layout/index.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'DashboardParent',
                redirect: '/dashboard/stat',
                children: [
                    {
                        path: 'stat',
                        name: 'DashboardStat',
                        component: () => import('@/views/dashboard/stat.vue'),
                        meta: { title: '概览', icon: 'Odometer' }
                    }
                ]
            },
            {
                path: 'course-create',
                name: 'CourseCreateParent',
                redirect: '/course-create/index',
                children: [
                    {
                        path: 'index',
                        name: 'CourseCreate',
                        component: () => import('@/views/course/edit.vue'),
                        meta: { title: '新建课程', icon: 'DocumentAdd' }
                    }
                ]
            },
            {
                path: 'course',
                name: 'Course',
                redirect: '/course/list',
                children: [
                    {
                        path: 'list',
                        name: 'CourseList',
                        component: () => import('@/views/course/list.vue'),
                        meta: { title: '我的课程', icon: 'Reading' }
                    },
                    {
                        path: 'edit/:id',
                        name: 'CourseEdit',
                        component: () => import('@/views/course/edit.vue'),
                        meta: { title: '编辑课程', hidden: true, activeMenu: '/course/list' }
                    }
                ]
            },
            {
                path: 'qa',
                name: 'QA',
                redirect: '/qa/list',
                children: [
                    {
                        path: 'list',
                        name: 'QAList',
                        component: () => import('@/views/qa/list.vue'),
                        meta: { title: '学生提问', icon: 'ChatLineSquare' }
                    }
                ]
            },
            {
                path: 'review',
                name: 'Review',
                redirect: '/review/list',
                children: [
                    {
                        path: 'list',
                        name: 'ReviewList',
                        component: () => import('@/views/review/list.vue'),
                        meta: { title: '评价统计', icon: 'Star' }
                    },
                    {
                        path: 'detail/:courseId',
                        name: 'ReviewDetail',
                        component: () => import('@/views/review/detail.vue'),
                        meta: { title: '课程评价详情', hidden: true, activeMenu: '/review/list' }
                    }
                ]
            },
            {
                path: 'student',
                name: 'Student',
                redirect: '/student/list',
                children: [
                    {
                        path: 'list',
                        name: 'StudentList',
                        component: () => import('@/views/student/list.vue'),
                        meta: { title: '我的学员', icon: 'User' }
                    },
                    {
                        path: 'detail/:userId',
                        name: 'StudentDetail',
                        component: () => import('@/views/student/detail.vue'),
                        meta: { title: '学习明细', hidden: true, activeMenu: '/student/list' }
                    }
                ]
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/profile/index.vue'),
                meta: { title: '个人资料', hidden: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from) => {
    const userStore = useUserStore()
    document.title = to.meta.title ? `${to.meta.title} - 在线教育讲师端` : '在线教育讲师端'

    if (to.meta.public) {
        return true
    } else if (!userStore.isLoggedIn) {
        return '/login'
    } else {
        return true
    }
})

export default router
