<template>
  <el-menu
    :default-active="activeMenu"
    class="sidebar-menu"
    :collapse="isCollapse"
    background-color="#1a3a5c"
    text-color="#c9d8f0"
    active-text-color="#ffffff"
    router
  >
    <div class="logo">
      <el-icon v-if="!isCollapse" class="logo-icon"><Reading /></el-icon>
      <span v-if="!isCollapse" class="logo-text">在线教育讲师端</span>
      <el-icon v-else size="24" color="#4da8ff"><Platform /></el-icon>
    </div>
    
    <template v-for="route in sidebarRoutes" :key="route.path">
      <template v-if="!route.children || route.children.filter(c => !c.meta?.hidden).length === 1">
        <el-menu-item 
          :index="!route.children ? '/' + route.path : '/' + route.path + '/' + route.children.filter(c => !c.meta?.hidden)[0].path"
        >
          <el-icon v-if="route.meta?.icon || (!route.children ? null : route.children.filter(c => !c.meta?.hidden)[0].meta?.icon)">
            <component :is="route.meta?.icon || (!route.children ? null : route.children.filter(c => !c.meta?.hidden)[0].meta?.icon)" />
          </el-icon>
          <template #title>
            {{ !route.children ? route.meta?.title : route.children.filter(c => !c.meta?.hidden)[0].meta?.title }}
          </template>
        </el-menu-item>
      </template>
      
      <el-sub-menu v-else :index="'/' + route.path">
        <template #title>
          <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
          <span>{{ route.meta?.title }}</span>
        </template>
        <el-menu-item 
          v-for="child in route.children.filter(c => !c.meta?.hidden)" 
          :key="child.path" 
          :index="'/' + route.path + '/' + child.path"
        >
          {{ child.meta?.title }}
        </el-menu-item>
      </el-sub-menu>
    </template>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

defineProps({
  isCollapse: Boolean
})

const route = useRoute()
const router = useRouter()

const sidebarRoutes = computed(() => {
  return router.options.routes.find(r => r.path === '/').children.filter(r => !r.meta?.hidden)
})

const activeMenu = computed(() => {
  if (route.meta?.activeMenu) {
    return route.meta.activeMenu
  }
  return route.path
})
</script>

<style scoped>
.sidebar-menu {
  height: 100vh;
  border-right: none;
  background-color: #1a3a5c;
}
.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  overflow: hidden;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background-color: #1a3a5c;
  color: #ffffff;
}
.logo-icon {
  font-size: 24px;
  color: #4da8ff;
  margin-right: 10px;
}
.logo-text {
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
}
</style>
