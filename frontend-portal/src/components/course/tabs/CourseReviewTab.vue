<template>
  <div class="course-review-tab">
    <!-- 头部统计区和写评价按钮 -->
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
      
      <!-- 写评价按钮，与综合评分水平对齐 -->
      <div class="write-review-btn-wrap">
        <el-button type="primary" @click="handleWriteReview">
          写评价
        </el-button>
      </div>
    </div>

    <!-- 我的评价区域 -->
    <div v-if="statData.userHasReviewed && myReview" class="my-review-section">
      <div class="section-title">我的评价</div>
      <div class="review-card my-card">
        <div class="user-info">
          <el-avatar :size="40" :src="myReview.userAvatar" />
          <div class="name-rate-wrap">
            <span class="nickname">{{ myReview.userNickname }}</span>
            <el-rate :model-value="myReview.rating" disabled class="small-rate-display" />
          </div>
        </div>
        
        <!-- 更多操作下拉菜单 -->
        <div class="more-actions">
          <el-dropdown trigger="click" @command="handleMyReviewCommand">
            <span class="el-dropdown-link">
              <el-icon class="more-icon"><MoreFilled /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">修改</el-dropdown-item>
                <el-dropdown-item command="delete" class="danger-item">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="review-content-wrap">
          <p class="content">{{ myReview.content }}</p>
        </div>
        <div class="card-footer">
          <span class="time">发表于 {{ myReview.createTime }}</span>
          <el-button 
            text 
            :class="{ 'is-liked': myReview.isLiked }"
            @click="handleToggleLike(myReview)"
          >
            <el-icon><Icon :icon="myReview.isLiked ? 'mdi:thumb-up' : 'mdi:thumb-up-outline'" /></el-icon>
            {{ myReview.likeCount || '0' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 所有评价列表 -->
    <div class="all-reviews-section">
      <div class="list-toolbar">
        <div class="toolbar-left">
          <span class="section-title">用户评价</span>
        </div>
        <div class="toolbar-right">
          <div class="filter-item">
            <span class="filter-label">排序：</span>
            <el-select v-model="queryParams.sortType" style="width: 100px" @change="handleSortChange">
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
              <el-button 
                text 
                :class="{ 'is-liked': item.isLiked }"
                @click="handleToggleLike(item)"
              >
                <el-icon><Icon :icon="item.isLiked ? 'mdi:thumb-up' : 'mdi:thumb-up-outline'" /></el-icon>
                {{ item.likeCount || '0' }}
              </el-button>
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
        <el-empty v-else description="暂无评价内容" />
      </div>
    </div>

    <!-- 评价输入弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '修改评价' : '发表评价'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="reviewForm" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="课程评分" prop="rating">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您对这门课程的看法..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="submitLoading">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Icon } from '@iconify/vue'
import { MoreFilled } from '@element-plus/icons-vue'
import {
  getCourseReviewStat,
  getCourseReviewList,
  submitCourseReview,
  updateCourseReview,
  deleteCourseReview,
  toggleCourseReviewLike
} from '@/api/course'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  courseId: {
    type: [Number, String],
    required: true
  }
})

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// --- 统计数据 ---
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
  try {
    const { data } = await getCourseReviewStat(props.courseId)
    if (data) {
      statData.avgScore = data.avgScore || 0
      statData.totalReviews = data.totalReviews || 0
      statData.ratingDistribution = data.ratingDistribution || {}
      statData.userHasReviewed = data.userHasReviewed || false
      
      if (statData.userHasReviewed) {
        fetchMyReview()
      }
    }
  } catch (error) {
    console.error('获取评分统计失败', error)
  }
}

// --- 我的评价 ---
const myReview = ref(null)
const fetchMyReview = async () => {
  try {
    const { data } = await getCourseReviewList({
      courseId: props.courseId,
      page: 1,
      size: 1,
      mine: true
    })
    if (data && data.records && data.records.length > 0) {
      myReview.value = data.records[0]
    } else {
      myReview.value = null
    }
  } catch (error) {
    console.error('获取我的评价失败', error)
  }
}

// --- 列表数据 ---
const queryParams = reactive({
  courseId: props.courseId,
  page: 1,
  size: 10,
  sortType: 'latest',
  rating: null,
  mine: false
})
const reviewList = ref([])
const total = ref(0)
const loading = ref(false)

const fetchReviewList = async () => {
  loading.value = true
  try {
    const { data } = await getCourseReviewList(queryParams)
    reviewList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取评价列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSortChange = () => {
  queryParams.page = 1
  fetchReviewList()
}
const handleFilterChange = () => {
  queryParams.page = 1
  fetchReviewList()
}

// --- 弹窗逻辑 ---
const dialogVisible = ref(false)
const isEditMode = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const reviewForm = reactive({
  id: null,
  rating: 0,
  content: ''
})

const rules = {
  rating: [
    { required: true, message: '请给课程打分', trigger: 'change' },
    { validator: (rule, value, callback) => {
        if (value === 0) callback(new Error('请给课程打分'))
        else callback()
      }, trigger: 'change'
    }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' }
  ]
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  reviewForm.id = null
  reviewForm.rating = 0
  reviewForm.content = ''
  isEditMode.value = false
}

const handleWriteReview = () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录后再发表评价')
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  if (statData.userHasReviewed) {
     return ElMessage.warning('您已经评价过该课程，可以直接修改您的评价')
  }
  isEditMode.value = false
  dialogVisible.value = true
}

const handleEditReview = () => {
  if (!myReview.value) return
  reviewForm.id = myReview.value.id
  reviewForm.rating = myReview.value.rating
  reviewForm.content = myReview.value.content
  isEditMode.value = true
  dialogVisible.value = true
}

const handleDeleteReview = () => {
  if (!myReview.value) return
  ElMessageBox.confirm('确定要删除您的这条评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCourseReview(myReview.value.id)
      ElMessage.success('删除评价成功')
      myReview.value = null
      statData.userHasReviewed = false
      // 重新拉取统计与列表
      fetchStatData()
      fetchReviewList()
    } catch (error) {
      console.error('删除评价失败', error)
    }
  }).catch(() => {})
}

const handleMyReviewCommand = (command) => {
  if (command === 'edit') {
    handleEditReview()
  } else if (command === 'delete') {
    handleDeleteReview()
  }
}

const submitReview = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      const payload = {
        courseId: props.courseId,
        rating: reviewForm.rating,
        content: reviewForm.content
      }
      
      if (isEditMode.value) {
        await updateCourseReview(reviewForm.id, payload)
        ElMessage.success('修改评价成功')
      } else {
        await submitCourseReview(payload)
        ElMessage.success('发表评价成功')
      }
      
      dialogVisible.value = false
      // 重新拉取统计与列表
      fetchStatData()
      fetchReviewList()
    } catch (error) {
       console.error(isEditMode.value ? '修改评价失败' : '发表评价失败', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// --- 点赞逻辑 ---
const handleToggleLike = async (item) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请登录后再操作')
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  try {
    const { data } = await toggleCourseReviewLike(item.id)
    if (data) {
      item.isLiked = data.isLiked
      item.likeCount = data.likeCount
    }
  } catch (error) {
    console.error('点赞失败', error)
  }
}

onMounted(() => {
  fetchStatData()
  fetchReviewList()
})
</script>

<style scoped>
.course-review-tab {
  padding: 24px 0;
}

/* 头部统计区 */
.review-stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
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

.write-review-btn-wrap {
  display: flex;
  align-items: center;
}

/* 评价区块通用样式 */
.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.review-card {
  padding: 20px 0;
  border-bottom: 1px dashed #dcdfe6;
}

.review-card:last-child {
  border-bottom: none;
}

.my-card {
  background-color: #fafbfc;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e1e4e8;
  margin-bottom: 30px;
  position: relative;
}

.more-actions {
  position: absolute;
  top: 20px;
  right: 20px;
}

.more-icon {
  font-size: 18px;
  color: #909399;
  cursor: pointer;
}

.more-icon:hover {
  color: #409eff;
}

.danger-item {
  color: #f56c6c !important;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px; /* 缩减边距 */
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

.actions {
  display: flex;
  gap: 12px;
}

.is-liked {
  color: #409eff !important;
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
