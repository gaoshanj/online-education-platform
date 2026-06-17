<template>
  <div class="my-favorites-panel mock-panel">
    <div class="mock-header">
      <h3><el-icon><Collection /></el-icon> 我的收藏</h3>
    </div>
    
    <el-card shadow="never" class="mock-card" v-loading="loading">
      <template v-if="courseList.length > 0">
        <div class="course-grid">
          <CourseCard 
            v-for="item in courseList" 
            :key="item.id"
            :course="item"
            :is-manage-mode="true"
            manage-type="favorite"
            @cancel-action="handleCancelFavorite"
          />
        </div>

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
        description="暂无收藏课程" 
        :image-size="100"
      >
        <el-button type="primary" @click="$router.push('/courses')">去发现课程</el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Collection } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyFavoritesPage, toggleCourseFavorite } from '@/api/course'
import CourseCard from '@/components/course/CourseCard.vue'

const loading = ref(true)
const courseList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)

async function fetchCourses() {
  loading.value = true
  try {
    const res = await getMyFavoritesPage({
      page: currentPage.value,
      size: pageSize.value
    })
    courseList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (err) {
    console.error('获取收藏列表失败', err)
  } finally {
    loading.value = false
  }
}

function handlePageChange(val) {
  currentPage.value = val
  window.scrollTo({ top: 0, behavior: 'smooth' })
  fetchCourses()
}

async function handleCancelFavorite(course) {
  try {
    await ElMessageBox.confirm(`确定要取消收藏《${course.courseName}》吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await toggleCourseFavorite(course.id)
    ElMessage.success('已取消收藏')
    // 重新获取当前页（如果当前页只剩一条且不是第一页，则退回上一页）
    if (courseList.value.length === 1 && currentPage.value > 1) {
      currentPage.value -= 1
    }
    fetchCourses()
  } catch (err) {
    if (err !== 'cancel') {
      console.error('取消收藏失败', err)
      ElMessage.error('取消收藏失败')
    }
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
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
  min-height: 400px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  padding: 10px 4px;
}

.pagination-wrap {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  padding-bottom: 10px;
}
</style>
