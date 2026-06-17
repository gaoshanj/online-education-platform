<template>
  <div class="course-review-detail">
    <!-- 头部统计区，复用讲师端优良布局并在最前加了返回按钮以便管理员操作 -->
    <el-card class="stat-card" shadow="never">
      <div class="header-nav">
        <el-button :icon="ArrowLeft" @click="handleBack" plain>返回列表</el-button>
        <span class="course-title">{{ courseName || '课程评价详情' }}</span>
      </div>
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
              <span class="total-reviews">共 {{ statData.totalReviews }} 人参与评价</span>
            </div>
          </div>
        </div>
        
        <!-- 右侧评分分布图 -->
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

    <!-- 评价详情列表管理 -->
    <el-card class="list-card" shadow="never">
      <div class="all-reviews-section">
        <div class="list-toolbar">
          <div class="toolbar-left">
            <span class="section-title">评价内容管理</span>
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
              <el-select v-model="queryParams.rating" style="width: 120px" @change="handleFilterChange" clearable placeholder="全部评价">
                <el-option label="全部等级" :value="null" />
                <el-option label="5星 (极好)" :value="5" />
                <el-option label="4星 (较好)" :value="4" />
                <el-option label="3星 (一般)" :value="3" />
                <el-option label="2星 (较差)" :value="2" />
                <el-option label="1星 (极差)" :value="1" />
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
                <div class="footer-left">
                  <span class="time">发表于 {{ item.createTime }}</span>
                  <span class="like-display">
                    被赞同数：{{ item.likeCount || '0' }}
                  </span>
                </div>
                <!-- 新增：管理功能：删除评价 -->
                <div class="footer-right">
                  <el-button type="danger" link size="small" @click="handleDelete(item)">删除该评价</el-button>
                </div>
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
          <el-empty v-else description="暂无符合条件的筛选评价" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCourseReviewStatByCourseId, getCourseReviewDetailList, deleteReview } from '@/api/review'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const courseId = route.params.courseId
const courseName = route.query.courseName

// 返回
const handleBack = () => {
  router.back()
}

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
    const res = await getCourseReviewStatByCourseId(courseId)
    if (res && res.data) {
      statData.avgScore = res.data.avgScore || 0
      statData.totalReviews = res.data.totalReviews || 0
      statData.ratingDistribution = res.data.ratingDistribution || {}
      statData.userHasReviewed = res.data.userHasReviewed || false
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
    // 假设 res 格式为外层Result包裹，取 data
    if (res && res.data && res.data.records) {
      reviewList.value = res.data.records
      total.value = res.data.total
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

// 追加管理操作端：删除评价
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要移除由用户「${row.userNickname}」发表的这条评论记录吗？删除操作不可恢复。`, '移除评论', {
      confirmButtonText: '强制删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }
  
  try {
    await deleteReview(row.id)
    ElMessage.success('操作成功，选中评价已删除。')
    // 若当前条目唯一且是尾页，往前翻一页
    if (reviewList.value.length === 1 && queryParams.page > 1) {
      queryParams.page -= 1
    }
    // 并发刷新统计头与列表数据，确保评分基数同步
    fetchStatData()
    fetchReviewList()
  } catch (err) {
    console.error(err)
  }
}

onMounted(() => {
  if (!courseId) {
    ElMessage.error('缺少课程信息')
    return router.replace('/review')
  }
  fetchStatData()
  fetchReviewList()
})
</script>

<style scoped>
.course-review-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-card, .list-card {
  border-radius: 8px;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.course-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

/* 头部统计区 */
.review-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8fbff;
  border: 1px solid #e1eaf7;
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
  border-left: 1px dashed #dcdfe6;
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
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.review-card {
  padding: 20px 0;
  border-bottom: 1px dashed #e1e4e8;
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

.footer-left {
  display: flex;
  gap: 16px;
}

.time {
  font-size: 12px;
  color: #909399;
}

.like-display {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
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

.list-toolbar .section-title {
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
  font-size: 13px;
  color: #606266;
}

.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.review-list {
  background-color: #fbfdff;
  padding: 0 20px 20px 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
</style>
