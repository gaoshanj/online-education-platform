<template>
  <div class="app-container">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm" class="search-form">
        <el-form-item label="学员昵称" prop="nickname">
          <el-input
            v-model="queryParams.nickname"
            placeholder="请输入学员昵称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="最近学习时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 380px"
            @change="handleQuery"
          />
        </el-form-item>
        
        <el-form-item label="排序方式" prop="sortBy">
          <el-select v-model="queryParams.sortBy" style="width: 160px" @change="handleQuery">
            <el-option label="最近学习时间" value="latestLearn" />
            <el-option label="学习课程数" value="courseCount" />
            <el-option label="总学习时长" value="duration" />
            <el-option label="平均完成率" value="completionRate" />
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
        :data="studentList"
        style="width: 100%"
        border
      >
        <el-table-column label="学员信息" min-width="200" align="left">
          <template #default="scope">
            <div class="student-info">
              <el-avatar :size="40" :src="scope.row.avatar" />
              <span class="nickname">{{ scope.row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="joinTime" label="加入时间" width="170" align="center" />
        
        <el-table-column prop="latestLearnTime" label="最近学习时间" width="170" align="center" />

        <el-table-column prop="courseCount" label="学习课程数" width="120" align="center" />
        
        <el-table-column label="总学习时长" width="140" align="center">
          <template #default="scope">
            <el-tag type="info" effect="plain">{{ formatDuration(scope.row.totalDuration) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="平均完成率" width="180" align="center">
          <template #default="scope">
            <span>{{ scope.row.avgCompletionRate || 0 }}%</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              icon="View"
              @click="handleViewDetail(scope.row)"
            >
              课程明细
            </el-button>
          </template>
        </el-table-column>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStudentList } from '@/api/student'
import { formatDuration } from '@/utils/format'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  nickname: '',
  learnStart: '',
  learnEnd: '',
  sortBy: 'latestLearn'
})

// 时间绑定变量
const dateRange = ref([])
watch(dateRange, (val) => {
  if (val && val.length === 2) {
    queryParams.learnStart = val[0]
    queryParams.learnEnd = val[1]
  } else {
    queryParams.learnStart = ''
    queryParams.learnEnd = ''
  }
})

const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const queryForm = ref(null)

// 进度条颜色分级
const getCompletionColor = (percentage) => {
  if (percentage < 30) return '#f56c6c'
  if (percentage < 70) return '#e6a23c'
  if (percentage < 100) return '#409eff'
  return '#67c23a'
}

// 获取学员列表
const fetchList = async () => {
  loading.value = true
  try {
    const res = await getStudentList(queryParams)
    if (res && res.records) {
      studentList.value = res.records
      total.value = res.total
    } else {
      studentList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch student list:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.page = 1
  fetchList()
}

// 重置搜索
const resetQuery = () => {
  if (queryForm.value) {
    queryForm.value.resetFields()
  }
  dateRange.value = []
  queryParams.page = 1
  fetchList()
}

// 点击查看详情
const handleViewDetail = (row) => {
  router.push({
    path: `/student/detail/${row.userId}`,
    query: { nickname: row.nickname }
  })
}

// 分页变化
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

.search-card {
  margin-bottom: 20px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nickname {
  font-weight: bold;
  color: #303133;
}

.progress-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
