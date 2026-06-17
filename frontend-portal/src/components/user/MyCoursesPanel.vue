<template>
  <div class="my-courses-panel mock-panel">
    <div class="mock-header">
      <h3><el-icon><Reading /></el-icon> 我的课程</h3>
    </div>
    
    <el-card shadow="never" class="mock-card">
      <!-- 1️⃣ 最近学习 -->
      <div class="inner-section" v-loading="recentLoading">
        <div class="inner-header">
          <h4><el-icon><Clock /></el-icon> 最近学习</h4>
        </div>
        <template v-if="recentCourse">
          <LearningCourseCard 
            :course="recentCourse" 
            @continue="handleContinue"
            class="recent-course-item"
          />
        </template>
        <el-empty 
          v-else 
          description="暂无最近学习记录" 
          :image-size="80"
        />
      </div>

      <el-divider class="section-divider" />

      <!-- 2️⃣ 全部课程 -->
      <div class="inner-section" v-loading="listLoading">
        <div class="inner-header">
          <h4><el-icon><List /></el-icon> 全部课程</h4>
        </div>
        
        <template v-if="myCourses.length > 0">
          <div class="course-list-wrap">
            <LearningCourseCard 
              v-for="item in myCourses" 
              :key="item.courseId"
              :course="item"
              @continue="handleContinue"
              class="course-list-item"
            />
          </div>

          <!-- 分页器 -->
          <div class="pagination-wrap">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="total"
              :page-size="pageSize"
              v-model:current-page="currentPage"
              @current-change="handlePageChange"
            />
          </div>
        </template>
        <el-empty 
          v-else 
          description="您还未报名任何课程" 
          :image-size="80"
        >
          <el-button type="primary" @click="$router.push('/courses')">去选课</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Clock, List, Reading } from '@element-plus/icons-vue'
import { getRecentLearning, getMyLearningPage } from '@/api/course'
import LearningCourseCard from '@/components/course/LearningCourseCard.vue'

const router = useRouter()

// -- 最近学习状态 --
const recentLoading = ref(true)
const recentCourse = ref(null)

// -- 全部课程状态 --
const listLoading = ref(true)
const myCourses = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(6)

async function fetchRecentCourse() {
  recentLoading.value = true
  try {
    const res = await getRecentLearning()
    if (res.data && res.data.courseId) {
      recentCourse.value = res.data
    } else {
      recentCourse.value = null
    }
  } catch (err) {
    console.error('获取最近学习失败', err)
  } finally {
    recentLoading.value = false
  }
}

async function fetchMyCourses() {
  listLoading.value = true
  try {
    const res = await getMyLearningPage({
      page: currentPage.value,
      size: pageSize.value
    })
    
    myCourses.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取学习列表失败', err)
  } finally {
    listLoading.value = false
  }
}

function handlePageChange(val) {
  currentPage.value = val
  fetchMyCourses()
}

function handleContinue(courseId) {
  router.push(`/course/${courseId}`)
}

onMounted(() => {
  fetchRecentCourse()
  fetchMyCourses()
})
</script>

<style scoped>
.my-courses-panel {
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

.inner-section {
  padding: 10px 4px;
}

.inner-header {
  margin-bottom: 20px;
}

.inner-header h4 {
  font-size: 15px;
  margin: 0;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.inner-header h4 .el-icon {
  color: var(--text-secondary);
  font-size: 16px;
}

.section-divider {
  margin: 24px 0;
  border-color: #ebeef5;
}

.course-list-wrap {
  display: flex;
  flex-direction: column;
}

.course-list-item {
  border-bottom: 1px solid #ebeef5;
  padding: 0 0 20px 0 !important;
  margin-bottom: 20px;
}

.course-list-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0 !important;
}

.recent-course-item {
  padding: 0 !important;
}

.pagination-wrap {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  padding-bottom: 10px;
}
</style>
