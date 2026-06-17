<template>
  <el-drawer
    v-model="visible"
    title="问题详情"
    size="500px"
    destroy-on-close
    @closed="handleClosed"
  >
    <div v-loading="loading" class="qa-detail-wrapper">
      <template v-if="detailData">
        <!-- Question Section -->
        <div class="question-header">
          <h2 class="q-title">{{ detailData.title }}</h2>
          <div class="q-meta">
            <el-avatar :size="28" :src="detailData.avatar || defaultAvatar" />
            <span class="q-user">{{ detailData.nickname || detailData.username || '匿名用户' }}</span>
            <span class="q-time">{{ formatTime(detailData.createTime) }}</span>
          </div>
          <div class="q-content">{{ detailData.content }}</div>
        </div>

        <el-divider class="divider" />

        <!-- Answer Actions -->
        <div class="answer-action-bar">
          <span class="answer-count">共 {{ detailData.answerCount || 0 }} 个回答</span>
          <el-button type="primary" @click="handleWantAnswer" size="small" plain>我要回答</el-button>
        </div>

        <!-- First Level Answer Input Box -->
        <div v-if="showAnswerInput" class="answer-input-box">
          <el-input
            v-model="answerContent"
            type="textarea"
            :rows="4"
            placeholder="写下你的回答..."
          />
          <div class="input-actions">
            <el-button @click="showAnswerInput = false" size="small">取消</el-button>
            <el-button type="primary" size="small" :loading="submitLoading" @click="submitFirstLevelAnswer">发布回答</el-button>
          </div>
        </div>

        <!-- Answers List -->
        <div class="answers-list" v-if="detailData.answers && detailData.answers.length > 0">
          <div v-for="ans in detailData.answers" :key="ans.id" class="answer-item">
            <div class="ans-meta">
              <el-avatar :size="32" :src="ans.avatar || defaultAvatar" class="ans-avatar" />
              <div class="ans-info">
                <div class="ans-name-line">
                  <span class="ans-user">{{ ans.nickname || ans.username || '匿名用户' }}</span>
                  <el-tag v-if="ans.isTeacher === 1" size="small" type="success" class="teacher-tag">讲师</el-tag>
                </div>
                <div class="ans-time">{{ formatTime(ans.createTime) }}</div>
              </div>
            </div>
            
            <div class="ans-content">{{ ans.content }}</div>
            
            <div class="ans-actions">
              <span class="action-btn" :class="{ 'is-liked': ans.isLiked }" @click="handleLike(ans)">
                <el-icon><Pointer /></el-icon> {{ ans.likeCount || 0 }}
              </span>
              <span class="action-btn" @click="toggleReplies(ans)">
                <el-icon><ChatDotRound /></el-icon> {{ (ans.replyTimes || ans.replies?.length) || '评论' }}
              </span>
              <span v-if="ans.isOwner" class="action-btn delete-btn" @click="handleDeleteAnswer(ans)">
                删除
              </span>
            </div>

            <!-- Nested Replies (Expanded) -->
            <div v-if="ans._showReplies" class="replies-wrapper">
              <div class="replies-list" v-loading="ans._repliesLoading">
                <template v-if="ans._fetchedReplies && ans._fetchedReplies.length > 0">
                  <div class="reply-item" v-for="reply in ans._fetchedReplies" :key="reply.id">
                    <div class="reply-meta-line">
                      <span class="reply-user">{{ reply.nickname || reply.username || '匿名用户' }}</span>
                      
                      <template v-if="reply.targetUserId && reply.targetReplyId !== ans.id">
                         <span class="reply-to-text">回复</span>
                         <span class="reply-user target">{{ reply.targetNickname }}</span>
                      </template>
                      
                      <span class="reply-content-text">：{{ reply.content }}</span>
                    </div>
                    
                    <div class="reply-actions">
                      <span class="r-time">{{ formatTime(reply.createTime) }}</span>
                      <span class="action-btn r-btn" :class="{ 'is-liked': reply.isLiked }" @click="handleLike(reply)">
                        <el-icon><Pointer /></el-icon> {{ reply.likeCount || 0 }}
                      </span>
                      <span class="action-btn r-btn" @click="openReplyInput(ans, reply)">
                        回复
                      </span>
                      <span v-if="reply.isOwner" class="action-btn delete-btn r-btn" @click="handleDeleteAnswer(reply, ans)">
                        删除
                      </span>
                    </div>
                  </div>
                </template>
                <div v-else-if="!ans._repliesLoading" class="no-replies">暂无评论</div>
              </div>
              
              <!-- Reply Input Box -->
              <div class="reply-input-wrapper">
                <el-input
                  v-model="ans._replyContent"
                  size="small"
                  :placeholder="ans._replyTarget ? `回复 ${ans._replyTarget.nickname || ans._replyTarget.username}` : '写下你的评论...'"
                  @keyup.enter="submitReply(ans)"
                >
                  <template #append>
                    <el-button :loading="ans._submitting" @click="submitReply(ans)">发表</el-button>
                  </template>
                </el-input>
                <div class="reply-cancel" v-if="ans._replyTarget">
                  <el-button link size="small" type="info" @click="ans._replyTarget = null; ans._replyContent = ''">
                    取消回复人
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无回答，快来抢沙发吧~" :image-size="80" />
      </template>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Pointer, ChatDotRound } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionDetail, getReplies, submitAnswer, toggleLike, deleteAnswer } from '@/api/question'
import { useUserStore } from '@/stores/user'
import { MSG_LOGIN_REQUIRED } from '@/utils/constants'

const router = useRouter()
const route = useRoute()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const emit = defineEmits(['update-list'])

const userStore = useUserStore()
const visible = ref(false)
const loading = ref(false)
const detailData = ref(null)
const currentQuestionId = ref(null)

// 一级回答
const showAnswerInput = ref(false)
const answerContent = ref('')
const submitLoading = ref(false)

// 暴露给父组件调用
const open = (id) => {
  currentQuestionId.value = id
  visible.value = true
  fetchDetail()
}

const handleClosed = () => {
  detailData.value = null
  showAnswerInput.value = false
  answerContent.value = ''
}

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getQuestionDetail(currentQuestionId.value)
    // 初始化一些前端状态标志位
    const data = res.data
    if (data && data.answers) {
      data.answers.forEach(ans => {
        ans._showReplies = false
        ans._repliesLoading = false
        ans._fetchedReplies = []
        ans._replyContent = ''
        ans._replyTarget = null
        ans._submitting = false
      })
    }
    detailData.value = data
  } catch (error) {
    console.error('Failed to fetch detail', error)
  } finally {
    loading.value = false
  }
}

// 检查登录
const checkLogin = () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning(MSG_LOGIN_REQUIRED)
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return false
  }
  return true
}

const handleWantAnswer = () => {
  if (!checkLogin()) return
  showAnswerInput.value = !showAnswerInput.value
}

// 提交一级回答
const submitFirstLevelAnswer = async () => {
  if (!answerContent.value.trim()) {
    return ElMessage.warning('回答内容不能为空')
  }
  submitLoading.value = true
  try {
    await submitAnswer({
      questionId: currentQuestionId.value,
      content: answerContent.value,
      answerId: 0 // 一级回答传0或null
    })
    ElMessage.success('回答成功')
    answerContent.value = ''
    showAnswerInput.value = false
    emit('update-list')
    fetchDetail() // 刷新当前详情
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

// 展开/收起二级评论
const toggleReplies = async (ans) => {
  ans._showReplies = !ans._showReplies
  if (ans._showReplies && ans._fetchedReplies.length === 0) {
    await loadReplies(ans)
  }
}

const loadReplies = async (ans) => {
  ans._repliesLoading = true
  try {
    const res = await getReplies(ans.id)
    ans._fetchedReplies = res.data || []
  } catch (err) {
    console.error(err)
  } finally {
    ans._repliesLoading = false
  }
}

// 设置要回复的目标
const openReplyInput = (ans, replyTarget) => {
  if (!checkLogin()) return
  ans._replyTarget = replyTarget
}

// 提交二级评论
const submitReply = async (ans) => {
  if (!checkLogin()) return
  if (!ans._replyContent.trim()) {
    return ElMessage.warning('评论内容不能为空')
  }
  ans._submitting = true
  
  // 如果选中了_replyTarget（点击了某个回复的“回复”），那么针对那条回复
  // 否则就是直接评论一级回答，targetReplyId 不传或传 0
  const targetReplyId = ans._replyTarget ? ans._replyTarget.id : 0
  const targetUserId = ans._replyTarget ? ans._replyTarget.userId : ans.userId

  try {
    await submitAnswer({
      questionId: currentQuestionId.value,
      answerId: ans.id, // 指向一级回答的 ID
      content: ans._replyContent,
      targetReplyId: targetReplyId,
      targetUserId: targetUserId
    })
    ElMessage.success('评论成功')
    ans._replyContent = ''
    ans._replyTarget = null
    // 重新加载评论列表
    await loadReplies(ans)
    
    // 更新外部回答数等统计
    ans.replyTimes = (ans.replyTimes || 0) + 1
    emit('update-list')
  } catch (e) {
    console.error(e)
  } finally {
    ans._submitting = false
  }
}

// 点赞（适用于一级和二级）
const handleLike = async (item) => {
  if (!checkLogin()) return
  try {
    // 调用点赞接口（假设后端返回成功的同时，前端自己toggle状态）
    await toggleLike(item.id)
    if (item.isLiked) {
      item.isLiked = false
      item.likeCount = Math.max(0, (item.likeCount || 0) - 1)
    } else {
      item.isLiked = true
      item.likeCount = (item.likeCount || 0) + 1
    }
  } catch (e) {
    console.error(e)
  }
}

// 删除回答或评论
const handleDeleteAnswer = (item, parentAns = null) => {
  ElMessageBox.confirm('确定要删除这条内容吗？', '警告').then(async () => {
    try {
      await deleteAnswer(item.id)
      ElMessage.success('删除成功')
      if (parentAns) {
        // 这是在删除二级评论，只刷新当前评论列表
        await loadReplies(parentAns)
        parentAns.replyTimes = Math.max(0, (parentAns.replyTimes || 0) - 1)
      } else {
        // 刷新整个详情页
        fetchDetail()
        emit('update-list')
      }
    } catch (e) {
      console.error(e)
    }
  }).catch(() => {})
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

defineExpose({
  open
})
</script>

<style scoped>
.qa-detail-wrapper {
  padding: 0 10px;
}

/* 问题区块 */
.question-header {
  margin-bottom: 16px;
}
.q-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.q-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.q-user {
  font-weight: 500;
  color: #606266;
}
.q-time {
  font-size: 13px;
  color: #909399;
}
.q-content {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
}
.divider {
  margin: 20px 0;
}

/* 操作条 */
.answer-action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.answer-count {
  font-weight: 600;
  color: #303133;
}

/* 回答输入框 */
.answer-input-box {
  margin-bottom: 24px;
}
.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}

/* 一级回答列表 */
.answer-item {
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}
.answer-item:last-child {
  border-bottom: none;
}
.ans-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}
.ans-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}
.ans-name-line {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ans-user {
  font-weight: 600;
  color: #303133;
}
.teacher-tag {
  transform: scale(0.9);
}
.ans-time {
  font-size: 12px;
  color: #909399;
}
.ans-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 12px;
  padding-left: 44px; /* 对齐头像 */
}
.ans-actions {
  display: flex;
  gap: 24px;
  padding-left: 44px;
}
.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
  cursor: pointer;
  transition: color 0.2s;
}
.action-btn:hover {
  color: #409EFF;
}
.action-btn.is-liked {
  color: #409EFF;
}
.action-btn.delete-btn:hover {
  color: #F56C6C;
}

/* 二级评论区 */
.replies-wrapper {
  margin-top: 16px;
  margin-left: 44px;
  background: #f8f9fb;
  border-radius: 8px;
  padding: 16px;
}
.reply-item {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e4e7ed;
}
.reply-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}
.reply-meta-line {
  font-size: 13px;
  line-height: 1.5;
  color: #606266;
  margin-bottom: 6px;
}
.reply-user {
  font-weight: 500;
  color: #303133;
}
.reply-to-text {
  margin: 0 4px;
  color: #909399;
}
.reply-user.target {
  color: #409EFF;
}
.reply-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}
.r-time {
  font-size: 12px;
  color: #909399;
}
.r-btn {
  font-size: 12px;
}

/* 二级回复输入区 */
.reply-input-wrapper {
  margin-top: 16px;
}
.reply-cancel {
  margin-top: 4px;
  text-align: right;
}
.no-replies {
  font-size: 13px;
  color: #909399;
  text-align: center;
  padding: 10px 0;
}
</style>
