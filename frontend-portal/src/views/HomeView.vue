<template>
  <div class="home-page">
    <!-- 顶部 Banner 区 -->
    <section class="banner-section">
      <div class="banner-inner">
        <el-row :gutter="20">
          <!-- 左侧：课程分类树 (5 栅格) -->
          <el-col :span="5">
            <div class="category-card">
              <h3 class="category-title">
                <el-icon><Menu /></el-icon> 课程分类
              </h3>
              <el-tree
                :data="categories"
                :props="{ children: 'children', label: 'categoryName' }"
                node-key="id"
                :expand-on-click-node="false"
                @node-click="handleCategoryClick"
                class="home-category-tree"
              >
                <template #default="{ node, data }">
                  <span class="tree-node-label" :title="data.categoryName">
                    {{ data.categoryName }}
                  </span>
                </template>
              </el-tree>
            </div>
          </el-col>

          <!-- 中间：首页轮播图 (14 栅格) -->
          <el-col :span="14">
            <div class="carousel-card">
              <el-carousel trigger="click" height="380px" arrow="always" :interval="2000">
                <el-carousel-item v-for="item in banners" :key="item.id">
                  <div class="carousel-image-wrapper" @click="handleBannerClick(item)">
                    <el-image
                      :src="item.imageUrl"
                      fit="cover"
                      class="carousel-image"
                      :alt="item.title"
                    >
                      <template #error>
                        <div class="image-slot">
                          <el-icon><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                    <!-- 轮播图标题可选展示 -->
                    <!-- <div class="carousel-title" v-if="item.title">{{ item.title }}</div> -->
                  </div>
                </el-carousel-item>
              </el-carousel>
            </div>
          </el-col>

          <!-- 右侧：个人信息与快捷入口 (5 栅格) -->
          <el-col :span="5">
            <div class="home-user-card">
              <!-- 未登录状态 -->
              <div class="user-top" v-if="!isLoggedIn">
                <el-avatar :size="64" :icon="UserFilled" class="user-avatar" />
                <div class="user-greeting">Hi，欢迎来到企业AI + 云能力人才培养平台</div>
                <div class="user-btn-group">
                  <el-button type="primary" round class="action-btn" @click="router.push('/login')">登录 / 注册</el-button>
                </div>
              </div>
              
              <!-- 已登录状态 -->
              <div class="user-top" v-else>
                <el-avatar :size="64" :src="userStore.userInfo?.avatar" class="user-avatar">
                  <el-icon><UserFilled /></el-icon>
                </el-avatar>
                <div class="user-greeting" :title="userStore.userInfo?.nickname || userStore.userInfo?.username">
                  Hi，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '学习者' }}
                </div>
                <div class="user-btn-group">
                  <el-button type="primary" round class="action-btn" @click="router.push('/learning')">我的学习</el-button>
                </div>
              </div>

              <!-- 底部特权展示 -->
              <div class="user-bottom-features">
                <div class="feature-item">
                  <div class="feature-icon-wrapper"><el-icon class="feature-icon"><Monitor /></el-icon></div>
                  <span>海量课程</span>
                </div>
                <div class="feature-item">
                  <div class="feature-icon-wrapper"><el-icon class="feature-icon"><Trophy /></el-icon></div>
                  <span>名师护航</span>
                </div>
                <div class="feature-item">
                  <div class="feature-icon-wrapper"><el-icon class="feature-icon"><DocumentChecked /></el-icon></div>
                  <span>系统学习</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 课程区块组 -->
    <div class="sections-wrap">
      <!-- 热门课程 -->
      <CourseSection
        title="🔥 热门课程"
        subtitle="人气最高的精选课程"
        :courses="hotCourses"
        :loading="hotLoading"
        more-link="/courses?orderBy=hot"
      />

      <!-- 最新课程 -->
      <CourseSection
        title="🆕 最新课程"
        subtitle="最近上线的新课"
        :courses="newCourses"
        :loading="newLoading"
        more-link="/courses?orderBy=time"
      />

      <!-- 精品课程 -->
      <CourseSection
        title="⭐ 精品课程"
        subtitle="平台精心挑选的优质内容"
        :courses="featuredCourses"
        :loading="featuredLoading"
        more-link="/courses"
      />

      <!-- 为你推荐（仅登录用户） -->
      <template v-if="isLoggedIn">
        <CourseSection
          title="🎯 为你推荐"
          subtitle="根据你的学习记录推荐"
          :courses="recommendCourses"
          :loading="recommendLoading"
          more-link="/courses"
        />
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Menu, Picture, UserFilled, Monitor, Trophy, DocumentChecked } from '@element-plus/icons-vue'
import { getCategories, getBanners, getHotRecommend, getLatestRecommend, getFeaturedRecommend, getPersonalRecommend } from '@/api/course'
import { useUserStore } from '@/stores/user'
import CourseSection from '@/components/course/CourseSection.vue'

const router = useRouter()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn())

const categories = ref([])
const banners = ref([])

const hotCourses = ref([])
const newCourses = ref([])
const featuredCourses = ref([])
const recommendCourses = ref([])
const hotLoading = ref(true)
const newLoading = ref(true)
const featuredLoading = ref(true)
const recommendLoading = ref(true)

function handleCategoryClick(data) {
  router.push({ path: '/courses', query: { categoryId: data.id } })
}

function handleBannerClick(banner) {
  if (banner.courseId && banner.courseId > 0) {
    router.push({ path: `/course/${banner.courseId}` })
  }
}

async function fetchCategoriesAndBanners() {
  try {
    const [catRes, banRes] = await Promise.all([
      getCategories(),
      getBanners()
    ])
    categories.value = catRes.data || []
    banners.value = banRes.data || []
  } catch (e) {
    console.error('加载分类或轮播图失败:', e)
  }
}

async function fetchSectionData(apiFunc, target, loadingRef) {
  loadingRef.value = true
  try {
    const res = await apiFunc({ limit: 5 })
    target.value = res.data || []
  } catch (e) {
    console.error('加载推荐区块失败:', e)
  } finally {
    loadingRef.value = false
  }
}

onMounted(() => {
  fetchCategoriesAndBanners()
  fetchSectionData(getHotRecommend, hotCourses, hotLoading)
  fetchSectionData(getLatestRecommend, newCourses, newLoading)
  fetchSectionData(getFeaturedRecommend, featuredCourses, featuredLoading)
  if (isLoggedIn.value) {
    fetchSectionData(getPersonalRecommend, recommendCourses, recommendLoading)
  }
})
</script>

<style scoped>
.home-page {
  background: var(--bg-page);
  min-height: calc(100vh - 60px);
}

/* 顶部 Banner 区 */
.banner-section {
  padding: 24px 0 0;
  background-color: var(--bg-page);
}

.banner-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 左侧分类卡片 */
.category-card {
  background: #ffffff;
  border-radius: 8px;
  height: 380px;
  overflow-y: auto;
  overflow-x: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  padding: 20px 16px;
  box-sizing: border-box;
}

/* 隐藏滚动条但保留功能 */
.category-card::-webkit-scrollbar {
  width: 4px;
}
.category-card::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 4px;
}

.category-title {
  margin: 0 0 12px 0;
  padding: 0 4px 12px;
  font-size: 15px;
  font-weight: bold;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  gap: 8px;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 2;
}

/* 分类树样式定制 */
.home-category-tree {
  padding: 0;
  --el-tree-node-hover-bg-color: #ecf5ff;
}

:deep(.home-category-tree .el-tree-node__content) {
  height: 36px;
  border-radius: 4px;
  margin-bottom: 4px;
}

.tree-node-label {
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧轮播图 */
.carousel-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background-color: #f5f7fa;
  height: 380px;
}

.carousel-image-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.carousel-image {
  width: 100%;
  height: 100%;
  display: block;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 48px;
}

/* 标题渐变背景 */
.carousel-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px 24px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: #fff;
  font-size: 20px;
  font-weight: 500;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧用户信息卡片 */
.home-user-card {
  background: #ffffff;
  border-radius: 8px;
  height: 380px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  padding: 20px 16px;
  text-align: center;
  box-sizing: border-box;
}

.user-top {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.user-avatar {
  margin-bottom: 16px;
  border: 2px solid #ecf5ff;
  font-size: 32px;
  background-color: #f0f2f5;
}

.user-greeting {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
  line-height: 1.5;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-btn-group {
  width: 100%;
}

.action-btn {
  width: 85%;
  font-weight: 600;
}

.user-bottom-features {
  display: flex;
  justify-content: space-between;
  border-top: 1px solid #ebeef5;
  padding-top: 20px;
  margin-top: 10px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 12px;
}

.feature-icon-wrapper {
  background: #ecf5ff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-icon {
  font-size: 20px;
  color: #409eff;
}

/* 课程区块 */
.sections-wrap {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px 48px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}
</style>
