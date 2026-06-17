<template>
  <div class="course-review-detail">
    <!-- 头部统计区 -->
    <el-card class="stat-card" shadow="never">
      <h3 class="course-title">{{ courseName || '课程评价详情' }}</h3>
      <div class="review-stat-header">
        <div class="stat-left">
          <div class="avg-score">
            <span class="score-number">{{ statData.avgScore.toFixed(1) }}</span>
            <div class="score-stars">
              <el-rate
                :model-value="statData.avgScore"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
                disabled-void-color="#c6d1de"
              />
              <span class="total-reviews">共 {{ statData.totalReviews }} 人评价</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧评分分布 -->
        <div class="stat-right">
          <div class="distribution-list">
            <div 
              class="dist-item" 
              v-for="star in 5" 
              :key="`star-${6-star}`"
            >
              <span class="star-label">
                 <el-rate :model-value="6-star" disabled class="small-rate" />
              </span>
              <el-progress 
                :percentage="getPercentage(6-star)" 
                :show-text="false"
                color="#ff9900"
              />
              <span class="dist-count">{{ statData.ratingDistribution[6-star] || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 所有评价列表 -->
    <el-card class="list-card" shadow="never">
      <div class="all-reviews-section">
        <div class="list-toolbar">
          <div class="toolbar-left">
            <span class="section-title">用户评价</span>
          </div>
          <div class="toolbar-right">
            <div class="filter-item">
              <span class="filter-label">排序：</span>
              <el-select v-model="queryParams.sortType" style="width: 100px" @change="handleFilterChange">
                <el-option label="最新" value="latest" />
                <el-option label="热门" value="hot" />
              </el-select>
            </div>
            <div class="filter-item">
              <span class="filter-label">筛选：</span>
              <el-select v-model="queryParams.rating" style="width: 120px" @change="handleFilterChange" clearable placeholder="全部">
                <el-option label="全部" :value="null" />
                <el-option label="5星" :value="5" />
                <el-option label="4星" :value="4" />
                <el-option label="3星" :value="3" />
                <el-option label="2星" :value="2" />
                <el-option label="1星" :value="1" />
              </el-select>
            </div>
          </div>
        </div>

        <div class="review-list" v-loading="loading">
          <template v-if="reviewList.length > 0">
            <div class="review-card user-review-card" v-for="item in reviewList" :key="item.id">
              <div class="user-info">
                <el-avatar :size="40" :src="item.userAvatar" />
                <div class="name-rate-wrap">
                  <span class="nickname">{{ item.userNickname }}</span>
                  <el-rate :model-value="item.rating" disabled class="small-rate-display" />
                </div>
              </div>
              <div class="review-content-wrap">
                <p class="content">{{ item.content }}</p>
              </div>
              <div class="card-footer">
                <span class="time">发表于 {{ item.createTime }}</span>
                <span class="like-display">
                  <el-icon><Icon icon="mdi:thumb-up-outline" /></el-icon>
                  {{ item.likeCount || '0' }}
                </span>
              </div>
            </div>
            
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="queryParams.page"
                v-model:page-size="queryParams.size"
                :total="total"
                layout="prev, pager, next"
                @current-change="fetchReviewList"
              />
            </div>
          </template>
          <el-empty v-else description="该课程暂无评价内容" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseReviewStatByCourseId, getCourseReviewDetailList } from '@/api/review'
import { Icon } from '@iconify/vue'

const route = useRoute()
const router = useRouter()
const courseId = route.params.courseId
const courseName = route.query.courseName

// 统计数据
const statData = reactive({
  avgScore: 0,
  totalReviews: 0,
  ratingDistribution: {},
  userHasReviewed: false
})

// 计算评分分布的百分比
const getPercentage = (star) => {
  if (statData.totalReviews === 0) return 0
  const count = statData.ratingDistribution[star] || 0
  return Number(((count / statData.totalReviews) * 100).toFixed(1))
}

const fetchStatData = async () => {
  if (!courseId) return
  try {
    const data = await getCourseReviewStatByCourseId(courseId)
    if (data) {
      statData.avgScore = data.avgScore || 0
      statData.totalReviews = data.totalReviews || 0
      statData.ratingDistribution = data.ratingDistribution || {}
      statData.userHasReviewed = data.userHasReviewed || false
    }
  } catch (error) {
    console.error('获取课程评价统计失败', error)
  }
}

// 列表数据
const queryParams = reactive({
  courseId: courseId,
  page: 1,
  size: 10,
  sortType: 'latest',
  rating: null
})
const reviewList = ref([])
const total = ref(0)
const loading = ref(false)

const fetchReviewList = async () => {
  if (!courseId) return
  loading.value = true
  try {
    const res = await getCourseReviewDetailList(queryParams)
    // 根据 request.js 拦截器，res 直接是 Page 对象
    if (res && res.records) {
      reviewList.value = res.records
      total.value = res.total
    } else {
      reviewList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取评价列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  queryParams.page = 1
  fetchReviewList()
}

onMounted(() => {
  fetchStatData()
  fetchReviewList()
})
</script>

<style scoped>
.course-review-detail {
  padding: 0;
}

.course-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 20px 0;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.stat-card {
  margin-bottom: 20px;
}

/* 头部统计区 */
.review-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
  padding: 24px;
  border-radius: 8px;
}

.stat-left {
  flex: 1;
}

.avg-score {
  display: flex;
  align-items: center;
  gap: 20px;
}

.score-number {
  font-size: 48px;
  font-weight: bold;
  color: #ff9900;
  line-height: 1;
}

.score-stars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.total-reviews {
  font-size: 14px;
  color: #909399;
}

.stat-right {
  flex: 2;
  border-left: 1px solid #ebeef5;
  padding-left: 40px;
  margin-right: 20px;
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.dist-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.star-label {
  display: flex;
  align-items: center;
  width: 90px;
}

:deep(.small-rate .el-rate__icon) {
  font-size: 14px;
  margin-right: 2px;
}

:deep(.dist-item .el-progress) {
  flex: 1;
}

.dist-count {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #606266;
}

/* 评价区块通用样式 */
.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.review-card {
  padding: 20px 0;
  border-bottom: 1px dashed #dcdfe6;
}

.review-card:last-child {
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.name-rate-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

:deep(.small-rate-display .el-rate__icon) {
  font-size: 14px;
  margin-right: 0;
}

.review-content-wrap {
  padding-left: 52px;
}

.content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  word-break: break-all;
  margin: 0;
}

.card-footer {
  margin-top: 12px;
  padding-left: 52px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time {
  font-size: 12px;
  color: #909399;
}

.like-display {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 列表工具栏 */
.list-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.toolbar-left .section-title {
  margin-bottom: 0;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.review-list {
  background-color: #fafbfc;
  padding: 0 20px 20px 20px;
  border-radius: 8px;
  border: 1px solid #e1e4e8;
}
</style>
