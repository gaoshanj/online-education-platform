<template>
  <el-container class="app-layout">
    <!-- 顶部导航 -->
    <el-header class="app-header">
      <div class="header-inner">
        <!-- Logo / 项目名 -->
        <router-link to="/" class="logo">
          <img src="/logo.png" alt="神州数码" class="logo-img" />
          <span class="logo-text">在线教育平台</span>
        </router-link>

        <!-- 主导航菜单 -->
        <nav class="nav-menu">
          <router-link to="/" class="nav-item" :class="{ active: route.name === 'home' }">首页</router-link>
          <router-link to="/courses" class="nav-item" :class="{ active: route.name === 'courses' }">全部课程</router-link>
          <router-link to="/teacher-apply" class="nav-item" :class="{ active: ['teacherApplyLanding', 'teacherApplyForm'].includes(route.name) }">讲师入驻</router-link>
          <router-link to="/ai" class="nav-item" :class="{ active: route.name === 'ai' }">小智 AI 问答</router-link>
        </nav>

        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索你感兴趣的课程..."
            clearable
            @keyup.enter="handleSearch"
            class="header-search"
          >
            <template #suffix>
              <el-icon class="search-icon" @click="handleSearch"><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 用户区域 -->
        <div class="user-area">
          <router-link to="/learning" class="nav-item" :class="{ active: route.name === 'learningCenter' }">个人中心</router-link>
          
          <template v-if="userStore.isLoggedIn()">
            <el-dropdown @command="handleCommand">
              <div class="user-trigger">
                <el-avatar :size="36" :src="userStore.userInfo?.avatar" class="user-avatar">
                  {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link :to="`/login?redirect=${encodeURIComponent($route.fullPath)}`">
             <el-button type="primary" round class="action-btn">登录</el-button>
            </router-link>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主内容 -->
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, ArrowDown, SwitchButton, Reading } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

function handleSearch() {
  const kw = keyword.value.trim()
  if (!kw) return
  router.push({ name: 'courses', query: { keyword: kw } })
}

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.replace('/')
  }
}

onMounted(async () => {
  // 页面刷新后，如果有 token 且没有 userInfo，尝试拉取用户信息
  if (userStore.isLoggedIn() && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      console.error('获取用户信息失败', e)
    }
  }
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.08);
  height: 60px !important;
  padding: 0;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 24px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon {
  font-size: 22px;
}
.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: var(--primary);
  white-space: nowrap;
}
.logo-img {
  height: 32px;
  width: auto;
}

/* 导航菜单 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}
.nav-item {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--text-primary);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.nav-item:hover {
  color: var(--primary);
  background: var(--primary-light);
}
.nav-item.active {
  color: var(--primary);
  background: var(--primary-light);
  font-weight: 600;
}
.nav-item.disabled {
  color: var(--text-secondary);
  cursor: default;
}
.nav-item.disabled:hover {
  color: var(--text-secondary);
  background: transparent;
}

/* 搜索栏 */
.search-bar {
  flex: 1;
  max-width: 320px;
}

:deep(.header-search .el-input__wrapper) {
  border-radius: 8px;
  /* 确保整个容器是flex布局，使能够调转 suffix 内 x 和 自定义搜索图标 顺序 */
  display: flex;
}

:deep(.header-search .el-input__suffix-inner) {
  display: flex !important;
  flex-direction: row;
  align-items: center;
}

/* 将 clear icon (删除按钮) 挪到 search icon 的前面 */
:deep(.header-search .el-input__suffix-inner > .el-icon.el-input__clear) {
  order: 1;
  margin-right: 4px;
}

:deep(.header-search .search-icon) {
  order: 2;
  cursor: pointer;
  color: var(--primary);
  font-size: 16px;
  font-weight: bold;
}

/* 用户区域 */
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  margin-left: auto;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-trigger:hover {
  background: var(--primary-light);
}
.user-name {
  font-size: 14px;
  color: var(--text-primary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 主内容区 */
.app-main {
  flex: 1;
  padding: 0;
  background: var(--bg-page);
}
</style>
