<template>
  <div class="app-container">
    <el-card shadow="never" class="page-header-card">
      <el-page-header @back="goBack" title="返回学员列表">
        <template #content>
          <span class="header-nickname">{{ nickname ? `${nickname} 的学习明细` : '学员学习明细' }}</span>
        </template>
      </el-page-header>
    </el-card>

    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm" class="search-form">
        <el-form-item label="课程名称" prop="courseName">
          <el-input
            v-model="queryParams.courseName"
            placeholder="请输入课程检索"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="完成状态" prop="isCompleted">
          <el-select v-model="queryParams.isCompleted" placeholder="全部状态" clearable style="width: 140px" @change="handleQuery">
            <el-option label="全部" :value="null" />
            <el-option label="已完成" :value="1" />
            <el-option label="未完成" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="排序方式" prop="sortBy">
          <el-select v-model="queryParams.sortBy" style="width: 160px" @change="handleQuery">
            <el-option label="最后学习时间" value="lastLearnTime" />
            <el-option label="学习进度" value="completionRate" />
            <el-option label="已学时长" value="duration" />
            <el-option label="报名时间" value="enrollTime" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="main-card">
      <el-table
        v-loading="loading"
        :data="courseList"
        style="width: 100%"
        border
      >
        <el-table-column label="报名课程" min-width="280">
          <template #default="scope">
            <div class="course-info">
              <el-image 
                v-if="scope.row.coverImage"
                :src="scope.row.coverImage" 
                class="course-cover"
                fit="cover"
              />
              <div v-else class="course-cover-placeholder"></div>
              <span class="course-name">{{ scope.row.courseName }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="学习进度" width="220" align="center">
          <template #default="scope">
            <div class="progress-wrap">
              <el-progress 
                 :percentage="scope.row.completionRate || 0" 
                 :color="getCompletionColor(scope.row.completionRate)"
                 style="width: 100%;"
              />
              <span class="chapter-info">
                ({{ scope.row.completedChapters }}/{{ scope.row.totalChapters }} 章节)
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isCompleted ? 'success' : 'info'">
              {{ scope.row.isCompleted ? '已完成' : '学习中' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="已学时长" width="130" align="center">
           <template #default="scope">
             {{ formatDuration(scope.row.duration) }}
           </template>
        </el-table-column>

        <el-table-column prop="enrollTime" label="报名时间" width="170" align="center" />
        <el-table-column prop="lastLearnTime" label="最后学习时间" width="170" align="center" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getStudentDetailList } from '@/api/student'
import { formatDuration } from '@/utils/format'

const router = useRouter()
const route = useRoute()

const userId = route.params.userId
const nickname = route.query.nickname

const goBack = () => {
  router.push('/student/list')
}

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  courseName: '',
  isCompleted: null,
  sortBy: 'lastLearnTime'
})

const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const queryForm = ref(null)

const getCompletionColor = (percentage) => {
  if (percentage < 30) return '#f56c6c'
  if (percentage < 70) return '#e6a23c'
  if (percentage < 100) return '#409eff'
  return '#67c23a'
}

// 获取明细列表
const fetchList = async () => {
  if (!userId) return
  loading.value = true
  try {
    const res = await getStudentDetailList(userId, queryParams)
    if (res && res.records) {
      courseList.value = res.records
      total.value = res.total
    } else {
      courseList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch student details:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.page = 1
  fetchList()
}

const resetQuery = () => {
  if (queryForm.value) {
    queryForm.value.resetFields()
  }
  queryParams.page = 1
  fetchList()
}

const handleSizeChange = (newSize) => {
  queryParams.size = newSize
  fetchList()
}

const handleCurrentChange = (newPage) => {
  queryParams.page = newPage
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.page-header-card {
  margin-bottom: 20px;
}

.header-nickname {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.search-card {
  margin-bottom: 20px;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.course-cover {
  width: 100px;
  height: 60px;
  border-radius: 4px;
}

.course-cover-placeholder {
  width: 100px;
  height: 60px;
  background-color: #f0f2f5;
  border-radius: 4px;
}

.course-name {
  font-weight: 500;
  color: #303133;
}

.progress-wrap {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.chapter-info {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
