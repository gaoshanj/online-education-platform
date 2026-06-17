<template>
  <div class="learning-center-page">
    <div class="main-container">
      <el-container class="center-container">
        <!-- 左侧导航菜单 -->
        <el-aside width="220px" class="center-sidebar">
          <div class="sidebar-header">
            <h3>个人中心</h3>
          </div>
          <el-menu
            :default-active="activeMenu"
            class="center-menu"
            @select="handleSelectMenu"
          >
            <el-menu-item index="courses">
              <el-icon><Reading /></el-icon>
              <span>我的课程</span>
            </el-menu-item>
            <el-menu-item index="likes">
              <el-icon><Star /></el-icon>
              <span>我的点赞</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Collection /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="settings">
              <el-icon><Setting /></el-icon>
              <span>个人设置</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <!-- 右侧内容区域 -->
        <el-main class="center-content">
          <!-- 我的课程 -->
          <template v-if="activeMenu === 'courses'">
            <MyCoursesPanel />
          </template>

          <!-- 我的点赞 -->
          <template v-else-if="activeMenu === 'likes'">
            <MyLikesPanel />
          </template>

          <!-- 我的收藏 -->
          <template v-else-if="activeMenu === 'favorites'">
            <div class="mock-panel">
              <MyFavoritesPanel />
            </div>
          </template>



          <!-- 个人设置 -->
          <template v-else-if="activeMenu === 'settings'">
            <MySettingsPanel />
          </template>
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Reading, Star, Collection, Setting } from '@element-plus/icons-vue'
import MyCoursesPanel from '@/components/user/MyCoursesPanel.vue'
import MyLikesPanel from '@/components/user/MyLikesPanel.vue'
import MyFavoritesPanel from '@/components/user/MyFavoritesPanel.vue'
import MySettingsPanel from '@/components/user/MySettingsPanel.vue'

const route = useRoute()
const router = useRouter()

// 当前激活的菜单项：默认选中 courses，或者从 url 的 tab 参数读取
const activeMenu = ref(route.query.tab || 'courses')

function handleSelectMenu(index) {
  activeMenu.value = index
  router.replace({ query: { ...route.query, tab: index } })
}

// 监听路由参数变化，处理例如浏览器前进后退的情况
watch(() => route.query.tab, (newTab) => {
  if (newTab) {
    activeMenu.value = newTab
  }
})

onMounted(() => {
  if (!route.query.tab) {
    router.replace({ query: { ...route.query, tab: 'courses' } })
  }
})
</script>

<style scoped>
.learning-center-page {
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
  padding: 30px 0 60px;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.center-container {
  gap: 24px;
  align-items: flex-start; /* 使得侧边栏不会被强制拉长 */
}

/* 左侧边栏 */
.center-sidebar {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.sidebar-header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #f0f2f5;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--text-primary);
  font-weight: 600;
}

.center-menu {
  border-right: none;
  padding: 10px 0;
}

.center-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 6px;
  color: var(--text-regular);
}

.center-menu .el-menu-item.is-active {
  background-color: var(--primary-light);
  color: var(--primary);
  font-weight: 500;
}

.center-menu .el-menu-item:hover {
  background-color: #f5f7fa;
  color: var(--primary);
}

.center-menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
}

/* 右侧内容区域 */
.center-content {
  background: transparent;
  padding: 0;
  overflow: visible;
}

/* 占位模块通用样式 */
.mock-panel {
  width: 100%;
}
.mock-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.mock-header h3 {
  font-size: 18px;
  margin: 0;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}
.mock-header h3 .el-icon {
  color: var(--primary);
  font-size: 20px;
}
.mock-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}
</style>
