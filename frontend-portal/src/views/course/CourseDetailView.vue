<template>
  <div class="detail-page">
    <div v-if="loading" class="detail-skeleton">
      <el-skeleton :rows="6" animated />
    </div>

    <template v-else-if="course">
      <!-- 顶部区域 -->
      <section class="detail-header-section">
        <div class="header-inner">
          <div class="player-wrap">
            <CoursePlayer :section="currentSection" @timeupdate="handleTimeUpdate" />
          </div>
          <div class="info-wrap">
            <CourseInfo
              :course="course"
              :is-logged-in="isLoggedIn"
              :enroll-loading="enrollLoading"
              @enroll="handleEnroll"
              @toggle-like="handleToggleLike"
              @toggle-favorite="handleToggleFavorite"
            />
          </div>
        </div>
      </section>

      <!-- 下方内容区 -->
      <section class="detail-tabs-section">
        <div class="tabs-inner">
          <el-tabs v-model="activeTab" class="course-tabs">
            <el-tab-pane label="课程详情" name="intro">
              <CourseIntroTab :description="course.description" />
            </el-tab-pane>
            <el-tab-pane label="课程目录" name="chapter">
              <CourseChapterTab
                :chapters="processedServerChapters"
                :current-section-id="currentSection?.id"
                :is-enrolled="course.isEnrolled"
                :loading="chaptersLoading"
                @select-section="handleSectionSelect"
              />
            </el-tab-pane>
            <el-tab-pane label="互动问答" name="qa">
              <CourseQaTab 
                :course="course"
                :is-logged-in="isLoggedIn"
              />
            </el-tab-pane>
            <el-tab-pane label="课程评价" name="review">
              <CourseReviewTab :course-id="course.id" />
            </el-tab-pane>
          </el-tabs>
        </div>
      </section>
    </template>

    <el-empty v-else description="课程不存在" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseDetail, getChapterList, enrollCourse, toggleCourseLike, toggleCourseFavorite, saveProgress } from '@/api/course'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MSG_LOGIN_REQUIRED } from '@/utils/constants'

import CoursePlayer from '@/components/course/CoursePlayer.vue'
import CourseInfo from '@/components/course/CourseInfo.vue'
import CourseIntroTab from '@/components/course/tabs/CourseIntroTab.vue'
import CourseChapterTab from '@/components/course/tabs/CourseChapterTab.vue'
import CourseQaTab from '@/components/course/tabs/CourseQaTab.vue'
import CourseReviewTab from '@/components/course/tabs/CourseReviewTab.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const course = ref(null)
const chapters = ref([])
const activeTab = ref(route.query.tab || 'chapter') // 默认进入目录页面或读取URL的Tab参数

// 监听 tab 切换，同步到 URL
watch(activeTab, (newTab) => {
  router.replace({ ...route, query: { ...route.query, tab: newTab } }).catch(() => {})
})

const loading = ref(true)
const chaptersLoading = ref(true)
const enrollLoading = ref(false)

const currentSection = ref(null)

const isLoggedIn = computed(() => userStore.isLoggedIn())

// 处理章节数据，附加解锁逻辑（未报名/未登录且全局索引>=3时加锁）
const processedServerChapters = computed(() => {
  let globalIndex = 0;
  return chapters.value.map(chapter => {
    if (!chapter.children) return chapter;
    const newChildren = chapter.children.map(child => {
      const idx = globalIndex++;
      const isLocked = (!isLoggedIn.value || !course.value?.isEnrolled) && idx >= 3;
      return {
        ...child,
        globalIndex: idx,
        isLocked
      }
    })
    return { ...chapter, children: newChildren }
  })
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getCourseDetail(route.params.id)
    course.value = res.data
    
    // 只有获取课程详情后获取章节，防止前后时序引起 currentSection 解析错误
    await fetchChapters()
  } finally {
    loading.value = false
  }
}

async function fetchChapters() {
  chaptersLoading.value = true
  try {
    const res = await getChapterList(route.params.id)
    chapters.value = res.data || []
    
    initDefaultSection()
  } finally {
    chaptersLoading.value = false
  }
}

function initDefaultSection() {
  const tree = processedServerChapters.value
  if (!tree.length) return

  // 1. 如果登录、已报名，并且有 recentChapterId，优先定位到该章节
  if (isLoggedIn.value && course.value?.isEnrolled && course.value?.recentChapterId) {
    let recentChild = null
    for (const chapter of tree) {
      if (chapter.children) {
        recentChild = chapter.children.find(c => c.id === course.value.recentChapterId)
        if (recentChild) break
      }
    }
    
    // 如果找到该历史学习章节并且未被锁定
    if (recentChild && !recentChild.isLocked) {
      currentSection.value = recentChild
      // 注意：CoursePlayer 里会有基于 lastPosition 的续播提示，如果需要在外面这里可以增加一句:
      // ElMessage.success(`已自动跳转到上次学习的章节: ${course.value.recentChapterName}`)
      return
    }
  }

  // 2. 否则默认回退到第一小节
  const firstChapter = tree[0]
  if (firstChapter.children && firstChapter.children.length > 0) {
    const firstChild = firstChapter.children[0]
    // 只有非锁定时，才允许设为默认（例如，既然前三节可试看，那第一节通常不锁）
    if (!firstChild.isLocked) {
      currentSection.value = firstChild
    }
  }
}

async function handleEnroll() {
  if (!isLoggedIn.value) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }

  ElMessageBox.confirm('您确定要报名学习该课程吗？', '确认报名', {
    confirmButtonText: '确定报名',
    cancelButtonText: '暂不',
    type: 'info'
  }).then(async () => {
    enrollLoading.value = true
    try {
      await enrollCourse(route.params.id)
      ElMessage.success('报名成功！')
      course.value.isEnrolled = true
      
      // 如果报名成功前因为各种原因没有选取小节，尝试选第一节
      if (!currentSection.value) initDefaultSection()
    } finally {
      enrollLoading.value = false
    }
  }).catch(() => {})
}

function handleSectionSelect(child) {
  if (child.$locked) {
    ElMessage.warning('请先报名该课程后继续观看')
    return
  }
  currentSection.value = child
}

async function handleToggleLike() {
  if (!isLoggedIn.value) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  if (!course.value) return

  try {
    await toggleCourseLike(course.value.id)
    if (course.value.isLiked) {
      course.value.isLiked = false
      course.value.likeCount = Math.max(0, (course.value.likeCount || 0) - 1)
      ElMessage.success('已取消点赞')
    } else {
      course.value.isLiked = true
      course.value.likeCount = (course.value.likeCount || 0) + 1
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败', error)
  }
}

async function handleToggleFavorite() {
  if (!isLoggedIn.value) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  if (!course.value) return

  try {
    await toggleCourseFavorite(course.value.id)
    if (course.value.isFavorited) {
      course.value.isFavorited = false
      course.value.favoriteCount = Math.max(0, (course.value.favoriteCount || 0) - 1)
      ElMessage.success('已取消收藏')
    } else {
      course.value.isFavorited = true
      course.value.favoriteCount = (course.value.favoriteCount || 0) + 1
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败', error)
  }
}

// 防抖记录学习进度（大约每10秒发送一次）
let lastSaveTime = 0
const PROGRESS_SAVE_INTERVAL = 10000 // 10s

async function handleTimeUpdate(payload) {
  // 必须是登录且已经报名才能记录进度
  if (!isLoggedIn.value || !course.value?.isEnrolled) return

  const now = Date.now()
  if (now - lastSaveTime < PROGRESS_SAVE_INTERVAL) return

  const { currentTime, maxPosition } = payload
  
  if (!currentSection.value || !course.value) return
  
  lastSaveTime = now
  try {
    await saveProgress({
      chapterId: currentSection.value.id,
      courseId: course.value.id,
      duration: 10, // 根据沟通：后端要求的duration是实际观看的增量秒数（心跳间隔约为10秒）
      lastPosition: currentTime,
      maxPosition: maxPosition
    })
  } catch (err) {
    console.error('进度存档失败:', err)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.detail-page {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding-bottom: 40px;
}

.detail-skeleton {
  max-width: 1280px;
  margin: 40px auto;
  padding: 0 24px;
}

/* 顶部区域 */
.detail-header-section {
  padding: 32px 0 0;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  gap: 24px; /* 调整间距和下方对齐感 */
  align-items: stretch;
}

.player-wrap {
  flex: 1.5; /* 左侧占 3/7 */
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.info-wrap {
  flex: 2; /* 右侧占 4/7 */
  min-width: 0;
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* 底部 Tabs 区 */
.detail-tabs-section {
  max-width: 1280px;
  margin: 32px auto 0;
  padding: 0 24px;
}

.tabs-inner {
  background: white;
  border-radius: 8px;
  padding: 24px 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

:deep(.course-tabs .el-tabs__item) {
  font-size: 16px;
  height: 48px;
  line-height: 48px;
}
</style>
