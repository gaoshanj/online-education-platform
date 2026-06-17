import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/LoginView.vue'),
        meta: { public: true }
    },
    {
        path: '/',
        component: () => import('@/layouts/AdminLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'banner',
                name: 'Banner',
                component: () => import('@/views/banner/BannerView.vue'),
                meta: { title: '轮播图管理' }
            },
            {
                path: 'category',
                name: 'Category',
                component: () => import('@/views/category/CategoryView.vue'),
                meta: { title: '课程分类管理' }
            },
            {
                path: 'course',
                name: 'Course',
                component: () => import('@/views/course/CourseView.vue'),
                meta: { title: '课程信息列表' }
            },
            {
                path: 'course-audit',
                name: 'CourseAudit',
                component: () => import('@/views/audit/CourseAuditView.vue'),
                meta: { title: '课程上架审核' }
            },
            {
                path: 'teacher-apply',
                name: 'TeacherApply',
                component: () => import('@/views/teacher-apply/TeacherApplyView.vue'),
                meta: { title: '讲师申请管理' }
            },
            {
                path: 'user',
                name: 'User',
                component: () => import('@/views/user/UserView.vue'),
                meta: { title: '用户管理' }
            },
            {
                path: 'question',
                name: 'Question',
                component: () => import('@/views/question/QuestionView.vue'),
                meta: { title: '问答管理' }
            },
            {
                path: 'teacher',
                name: 'Teacher',
                component: () => import('@/views/teacher/TeacherView.vue'),
                meta: { title: '讲师管理' }
            },
            {
                path: 'review',
                name: 'Review',
                component: () => import('@/views/review/ReviewView.vue'),
                meta: { title: '课程评价管理' }
            },
            {
                path: 'review/detail/:courseId',
                name: 'ReviewDetail',
                component: () => import('@/views/review/ReviewDetailView.vue'),
                meta: { title: '评价详情', hidden: true }
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('@/views/dashboard/DashboardView.vue'),
                meta: { title: '数据统计大盘' }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/profile/ProfileView.vue'),
                meta: { title: '个人资料', hidden: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    document.title = to.meta.title ? `${to.meta.title} - 在线教育管理端` : '在线教育管理端'
    if (!to.meta.public && !token) {
        next('/login')
    } else if (to.path === '/login' && token) {
        next('/')
    } else {
        next()
    }
})

export default router
