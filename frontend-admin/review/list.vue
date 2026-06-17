<template>
  <div class="review-stat-container">
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="平均评分">
          <el-select v-model="queryParams.avgScoreFilter" placeholder="请选择" clearable class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="4.5以上" value="above4_5" />
            <el-option label="4.0以上" value="above4_0" />
            <el-option label="3.5以下" value="below3_5" />
          </el-select>
        </el-form-item>
        <el-form-item label="好评率(4星+)">
          <el-select v-model="queryParams.goodReviewRateFilter" placeholder="请选择" clearable class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="90%以上" value="above90" />
            <el-option label="80%以上" value="above80" />
            <el-option label="60%以下" value="below60" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序方式">
          <el-select v-model="queryParams.sortBy" placeholder="默认排序" class="filter-select" @change="handleQuery">
            <el-option label="最新评价" value="latestReview" />
            <el-option label="评价数最多" value="reviewCount" />
            <el-option label="评分最高" value="scoreDesc" />
            <el-option label="评分最低" value="scoreAsc" />
            <el-option label="好评率最高" value="goodRateDesc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" style="width: 100%" border>
        <el-table-column label="课程封面" width="160" align="center">
          <template #default="{ row }">
            <el-image 
              :src="row.coverImage" 
              class="course-cover"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="平均评分" width="180" align="center">
          <template #default="{ row }">
            <div class="rate-container">
              <span class="score-text">{{ row.avgScore?.toFixed(1) || '0.0' }}</span>
              <el-rate :model-value="Number(row.avgScore)" disabled text-color="#ff9900" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="reviewCount" label="评价总数" width="120" align="center" />
        <el-table-column label="好评率(4星+)" width="120" align="center">
          <template #default="{ row }">
            <el-tag
              :type="row.goodReviewRate >= 90 ? 'success' : (row.goodReviewRate >= 60 ? 'warning' : 'danger')"
              disable-transitions
            >
              {{ row.goodReviewRate ? row.goodReviewRate + '%' : '0%' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latestReviewTime" label="最新评价时间" width="180" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReviewStatList } from '@/api/review'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  courseName: '',
  avgScoreFilter: 'all',
  goodReviewRateFilter: 'all',
  sortBy: 'latestReview',
  page: 1,
  size: 10
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 获取列表数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getReviewStatList(queryParams)
    // 根据 request.js 响应拦截器，这里拿到的 res 已经是响应体中的 data（即 Page 对象）
    if (res && res.records !== undefined) {
      tableData.value = res.records
      total.value = res.total
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取评价统计列表失败:', error)
    // 此处不需要再报 ElMessage.error，因为 request.js 拦截器里已经统一处理过错误了
  } finally {
    loading.value = false
  }
}

// 查询操作
const handleQuery = () => {
  queryParams.page = 1
  fetchData()
}

// 重置查询
const resetQuery = () => {
  queryParams.courseName = ''
  queryParams.avgScoreFilter = 'all'
  queryParams.goodReviewRateFilter = 'all'
  queryParams.sortBy = 'latestReview'
  handleQuery()
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.size = val
  fetchData()
}

// 当前页码改变
const handleCurrentChange = (val) => {
  queryParams.page = val
  fetchData()
}

// 查看详情跳转
const handleViewDetail = (row) => {
  router.push({
    path: `/review/detail/${row.courseId}`,
    query: { courseName: row.courseName }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.review-stat-container {
  padding: 0;
}
.filter-card {
  margin-bottom: 20px;
}
.filter-select {
  width: 140px;
}
.table-card {
  padding-bottom: 20px;
}
.course-cover {
  width: 120px;
  height: 68px;
  border-radius: 4px;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
}
.rate-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.score-text {
  font-weight: bold;
  color: #ff9900;
  font-size: 16px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
