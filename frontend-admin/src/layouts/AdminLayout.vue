<template>
  <el-container class="admin-layout">
    <!-- 左侧菜单 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo-area">
        <el-icon class="logo-icon">
          <Reading />
        </el-icon>
        <span class="logo-text">在线教育管理端</span>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu :default-active="activeMenu" router background-color="#1a3a5c" text-color="#c9d8f0"
          active-text-color="#ffffff" class="side-menu">
        <el-menu-item index="/dashboard">
          <el-icon>
            <DataLine />
          </el-icon>
          <span>概览</span>
        </el-menu-item>
        <el-menu-item index="/banner">
          <el-icon>
            <Picture />
          </el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-sub-menu index="/audit">
          <template #title>
            <el-icon>
              <Stamp />
            </el-icon>
            <span>审核中心</span>
          </template>
          <el-menu-item index="/course-audit">
            <el-icon>
              <DocumentChecked />
            </el-icon>
            <span>课程上架审核</span>
          </el-menu-item>
          <el-menu-item index="/teacher-apply">
            <el-icon>
              <Avatar />
            </el-icon>
            <span>讲师入驻审核</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/course-group">
          <template #title>
            <el-icon>
              <VideoCamera />
            </el-icon>
            <span>课程管理</span>
          </template>
          <el-menu-item index="/category">
            <el-icon>
              <FolderOpened />
            </el-icon>
            <span>课程分类管理</span>
          </el-menu-item>
          <el-menu-item index="/course">
            <el-icon>
              <Reading />
            </el-icon>
            <span>课程信息列表</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/question">
          <el-icon>
            <ChatDotSquare />
          </el-icon>
          <span>问答管理</span>
        </el-menu-item>
        <el-menu-item index="/review">
          <el-icon>
            <Star />
          </el-icon>
          <span>评价管理</span>
        </el-menu-item>
        <!-- <el-menu-item index="/teacher">
          <el-icon>
            <UserFilled />
          </el-icon>
          <span>讲师管理</span>
        </el-menu-item> -->
        <el-menu-item index="/user">
          <el-icon>
            <User />
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 右侧主区域 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/" class="custom-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(crumb, index) in breadcrumbs" :key="index">
              <span v-if="!crumb.path" class="breadcrumb-current">{{ crumb.title }}</span>
              <router-link v-else :to="crumb.path" class="breadcrumb-link">{{ crumb.title }}</router-link>
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="hover" class="user-dropdown">
            <div class="user-info-dropdown">
              <el-avatar :size="32" :src="authStore.userInfo?.avatar" class="avatar">
                <el-icon>
                  <User />
                </el-icon>
              </el-avatar>
              <span class="username">{{ authStore.userInfo?.nickname || authStore.userInfo?.username || '管理员' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'
import {
  Odometer,
  ArrowDown,
  DataLine,
  Stamp,
  DocumentChecked
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)

// 父级菜单及其默认跳转路径映射表
const parentMenuMap = {
  '/category': { title: '课程管理', path: '/course' },
  '/course': { title: '课程管理', path: '/course' },
  '/course-audit': { title: '审核中心', path: '/course-audit' },
  '/teacher-apply': { title: '审核中心', path: '/course-audit' },
  '/review/detail': { title: '评价管理' }
}

// 动态计算面包屑 (返回结构: { title: string, path?: string })
const breadcrumbs = computed(() => {
  const path = route.path
  if (path === '/dashboard') {
    return [{ title: '概览' }] // 首页作为根级，此处显示末级文字“概览” (无path即不可点置灰)
  }
  
  const results = []
  
  // 查询是否存在父级菜单关联
  // 问题修复：由于 '/course-audit' 以 '/course' 开头，短的 key 会提前匹配导致错乱
  // 按照 key 的长度降序排序来做精确优先的前缀匹配
  let matchedParent = null
  const sortedKeys = Object.keys(parentMenuMap).sort((a, b) => b.length - a.length)
  for (const key of sortedKeys) {
    if (path.startsWith(key)) {
      matchedParent = parentMenuMap[key]
      break
    }
  }
  
  if (matchedParent) {
    // 父级菜单给予可点击能力并赋予黑色标题
    results.push({ title: matchedParent.title, path: matchedParent.path })
  }
  
  // 压入当前业务页面（作为最后项），不再传入 path = 不可点并变灰
  if (route.meta?.title && route.meta.title !== '数据统计大盘') {
    results.push({ title: route.meta.title })
  }
  
  return results
})

onMounted(async () => {
  if (!authStore.userInfo && authStore.token) {
    try {
      await authStore.fetchUserInfo()
    } catch {
      // token 失效由拦截器处理
    }
  }
})

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  await authStore.logout()
  router.push('/login')
}

function handleCommand(command) {
  if (command === 'logout') {
    handleLogout()
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

/* 侧边栏 */
.sidebar {
  background-color: #1a3a5c;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.logo-icon {
  font-size: 24px;
  color: #4da8ff;
}

.logo-text {
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}

.menu-scrollbar {
  flex: 1;
  overflow: hidden;
}

.side-menu {
  border-right: none;
}

/* 顶部栏 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 70, 160, 0.08);
  padding: 0 24px;
  height: 60px;
}

.custom-breadcrumb {
  font-size: 14px;
  line-height: inherit;
}

/* 一级/二级层级：黑色，加粗，具有点击效应 */
:deep(.el-breadcrumb__inner),
:deep(.el-breadcrumb__inner a), .breadcrumb-link {
  color: #303133;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
}

:deep(.el-breadcrumb__inner a:hover), .breadcrumb-link:hover {
  color: #4da8ff;
}

/* 单独强行约束当前页（最右侧且无path包裹）：置灰字不再接受加粗 */
.breadcrumb-current,
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner),
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner .breadcrumb-current) {
  font-weight: normal;
  color: #909399;
  cursor: text;
}

.header-right {
  display: flex;
  align-items: center;
  height: 100%;
}

.user-dropdown {
  height: 100%;
}

.user-info-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 12px;
  height: 100%;
  transition: background-color 0.2s;
  outline: none;
}

.user-info-dropdown:hover {
  background-color: #f5f7fa;
}

.arrow-icon {
  font-size: 12px;
  color: #909399;
}

.username {
  font-size: 14px;
  color: #4a6080;
}

/* 内容区 */
.main-content {
  background: #f0f5ff;
  padding: 20px;
}
</style>
