<template>
  <div class="review-stat-container">
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="模糊搜索课程" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <!-- 新增：讲师昵称筛选 -->
        <el-form-item label="所属讲师">
          <el-select v-model="queryParams.teacherId" placeholder="请选择讲师" clearable filterable class="filter-select"
            @change="handleQuery">
            <el-option v-for="item in teacherOptions" :key="item.id" :label="item.nickname || item.username"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="平均评分">
          <el-select v-model="queryParams.avgScoreFilter" placeholder="不限" clearable class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="4.5星及以上" value="above4_5" />
            <el-option label="4.0星及以上" value="above4_0" />
            <el-option label="3.5星及以下" value="below3_5" />
          </el-select>
        </el-form-item>
        <el-form-item label="好评率(4星+)">
          <el-select v-model="queryParams.goodReviewRateFilter" placeholder="不限" clearable class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="90%以上" value="above90" />
            <el-option label="80%以上" value="above80" />
            <el-option label="60%以下" value="below60" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="queryParams.sortBy" placeholder="默认排序" class="filter-select" @change="handleQuery">
            <el-option label="最新评价时间" value="latestReview" />
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
      <el-table v-loading="loading" :data="tableData" style="width: 100%" border stripe>
        <el-table-column label="课程封面" width="160" align="center">
          <template #default="{ row }">
            <el-image :src="row.coverImage" class="course-cover" fit="cover">
              <template #error>
                <div class="image-slot">
                  <el-icon>
                    <Picture />
                  </el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="200" show-overflow-tooltip />
        <!-- 新增展示字段：讲师名称 -->
        <el-table-column prop="teacherName" label="讲师名称" width="140" show-overflow-tooltip />

        <el-table-column label="平均评分" width="160" align="center">
          <template #default="{ row }">
            <div class="rate-container">
              <span class="score-text">{{ row.avgScore?.toFixed(1) || '0.0' }}</span>
              <el-rate :model-value="Number(row.avgScore)" disabled text-color="#ff9900" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="reviewCount" label="评价总数" width="100" align="center" />
        <el-table-column label="好评率(4星+)" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.goodReviewRate >= 90 ? 'success' : (row.goodReviewRate >= 60 ? 'warning' : 'danger')"
              disable-transitions>
              {{ row.goodReviewRate ? row.goodReviewRate + '%' : '0%' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latestReviewTime" label="最新评价时间" width="170" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">评价详情管理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getReviewStatList } from '@/api/review'
import { getTeacherPage } from '@/api/teacher'
import { Picture } from '@element-plus/icons-vue'

const router = useRouter()

// 讲师下拉列表
const teacherOptions = ref([])

const fetchTeacherOptions = async () => {
  try {
    const res = await getTeacherPage({ page: 1, size: 1000 })
    teacherOptions.value = res.data?.records || []
  } catch (error) {
    console.error('拉取讲师列表失败', error)
  }
}

// 查询参数
const queryParams = reactive({
  courseName: '',
  teacherId: undefined,
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
    if (res && res.data && res.data.records !== undefined) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      tableData.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取评价统计列表失败:', error)
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
  queryParams.teacherId = undefined
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

// 查看详情并管理跳转
const handleViewDetail = (row) => {
  router.push({
    path: `/review/detail/${row.courseId}`,
    query: { courseName: row.courseName }
  })
}

onMounted(() => {
  fetchTeacherOptions()
  fetchData()
})
</script>

<style scoped>
.review-stat-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card,
.table-card {
  border-radius: 8px;
}

.demo-form-inline .el-form-item {
  margin-right: 20px;
  margin-bottom: 12px;
}

.filter-select {
  width: 140px;
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
  color: #c0c4cc;
  font-size: 24px;
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
  font-size: 15px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
