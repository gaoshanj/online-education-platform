<template>
  <div class="course-qa-tab">
    <!-- 顶部操作区 -->
    <div class="qa-header">
      <el-tabs v-model="activeTab" class="qa-tabs" @tab-change="handleTabChange" :before-leave="handleBeforeTabLeave">
        <el-tab-pane label="全部问题" name="all" />
        <el-tab-pane label="我的问题" name="mine" />
      </el-tabs>
      
      <el-button type="primary" @click="handleAsk">我要提问</el-button>
    </div>

    <!-- 问题列表区 -->
    <div class="qa-list" v-loading="loading">
      <template v-if="questionList.length > 0">
        <el-card 
          v-for="item in questionList" 
          :key="item.id" 
          class="qa-item-card" 
          shadow="hover"
          @click="goToDetail(item)"
        >
          <div class="qa-item-content">
            <!-- 左侧头像 -->
            <div class="qa-avatar">
              <el-avatar :size="40" :src="item.avatar || defaultAvatar" />
            </div>
            
            <!-- 右侧详情 -->
            <div class="qa-main">
              <div class="qa-meta">
                <span class="username">{{ item.nickname || item.username || '匿名用户' }}</span>
                <span class="time">{{ formatTime(item.createTime) }}</span>
              </div>
              
              <div class="qa-title">{{ item.title }}</div>
              <div class="qa-desc">{{ item.content }}</div>
              
              <!-- 最新回答 -->
              <div class="qa-latest-answer" v-if="item.latestAnswerContent">
                <span class="answer-author">{{ item.latestAnswerNickname }}：</span>
                <span class="answer-text">{{ item.latestAnswerContent }}</span>
              </div>
              
              <!-- 底部操作与统计 -->
              <div class="qa-actions">
                <div class="action-left">
                  <el-button link type="primary" size="small" @click.stop="goToDetail(item)">
                    <el-icon><ChatDotRound /></el-icon> {{ item.answerCount || 0 }} 回答
                  </el-button>
                </div>
                <div class="action-right" v-if="activeTab === 'mine' || item.isOwner">
                  <el-button link type="danger" size="small" @click.stop="handleDelete(item)">
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 分页 -->
        <div class="qa-pagination">
          <el-pagination
            v-model:current-page="pageParams.page"
            v-model:page-size="pageParams.size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="fetchList"
          />
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty v-else :description="activeTab === 'mine' ? '您还没有提问过哦~' : '暂无问题，快来抢沙发吧~'" />
    </div>

    <!-- 提问弹窗 -->
    <QaAskDialog
      ref="askDialogRef"
      :course-id="course.id"
      :course-name="course.title || course.courseName"
      @success="handleAskSuccess"
    />

    <!-- 问题详情抽屉 -->
    <QaDetailDrawer
      ref="detailDrawerRef"
      @update-list="fetchList"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChatDotRound } from '@element-plus/icons-vue'
import { getQuestionList, deleteQuestion } from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'
import QaAskDialog from '../qa/QaAskDialog.vue'
import QaDetailDrawer from '../qa/QaDetailDrawer.vue'
import { MSG_LOGIN_REQUIRED } from '@/utils/constants'

// 假设有一个通用的默认头像，你也可以填入项目中实际使用的默认头像 URL
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const props = defineProps({
  course: {
    type: Object,
    required: true
  },
  isLoggedIn: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()
const route = useRoute()

const activeTab = ref('all')
const loading = ref(false)
const questionList = ref([])
const total = ref(0)
const pageParams = ref({
  page: 1,
  size: 10
})

const askDialogRef = ref(null)
const detailDrawerRef = ref(null)

onMounted(() => {
  fetchList()
})

const handleBeforeTabLeave = (activeName, oldActiveName) => {
  if (activeName === 'mine' && !props.isLoggedIn) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return false // 阻止切换
  }
  return true
}

const handleTabChange = (name) => {
  // before-leave 已经做了拦截，这里只负责发请求
  pageParams.value.page = 1
  fetchList()
}

const fetchList = async () => {
  loading.value = true
  try {
    const onlyMine = activeTab.value === 'mine'
    const params = {
      onlyMine,
      page: pageParams.value.page,
      size: pageParams.value.size
    }
    const res = await getQuestionList(props.course.id, params)
    questionList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to fetch questions:', error)
  } finally {
    loading.value = false
  }
}

const handleAsk = () => {
  if (!props.isLoggedIn) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  askDialogRef.value?.open()
}

const handleAskSuccess = () => {
  activeTab.value = 'mine' // 提问成功后切到我的问题并刷新
  pageParams.value.page = 1
  fetchList()
}

const goToDetail = (item) => {
  detailDrawerRef.value?.open(item.id)
}

const handleDelete = (item) => {
  ElMessageBox.confirm('确定要删除这个问题吗？相关的回答也会一并删除！', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteQuestion(item.id)
      ElMessage.success('删除成功')
      fetchList()
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

// 简单的一层格式化。若项目中引入了 Day.js，可替换
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.course-qa-tab {
  padding: 10px 0;
}

.qa-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.qa-tabs {
  margin-bottom: 0;
}
:deep(.qa-tabs .el-tabs__header) {
  margin: 0;
}

.qa-list {
  min-height: 200px;
}

.qa-item-card {
  margin-bottom: 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.qa-item-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

.qa-item-content {
  display: flex;
  gap: 16px;
}

.qa-avatar {
  flex-shrink: 0;
}

.qa-main {
  flex: 1;
  min-width: 0; /* 防止文本溢出撑开 */
}

.qa-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.username {
  color: #606266;
  font-weight: 500;
}

.time {
  color: #909399;
  font-size: 13px;
}

.qa-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.qa-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2; /* 限制2行 */
  overflow: hidden;
}

.qa-latest-answer {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 12px;
  color: #606266;
}

.answer-author {
  font-weight: 600;
  color: #409EFF;
}

.qa-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.qa-pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
