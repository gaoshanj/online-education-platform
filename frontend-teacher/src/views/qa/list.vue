<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm" class="form-inline">
        <el-form-item label="所属课程" prop="courseId">
          <el-select
            v-model="queryParams.courseId"
            placeholder="请选择课程"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="答复状态" prop="hasTeacherAnswer">
          <el-select
            v-model="queryParams.hasTeacherAnswer"
            placeholder="不限"
            clearable
            style="width: 120px"
          >
            <el-option label="待解答" :value="0" />
            <el-option label="已解答" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 问答列表 -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="questionList" style="width: 100%" border>
        <el-table-column label="所属课程" prop="courseName" min-width="150" show-overflow-tooltip />
        <el-table-column label="提问标题" prop="title" min-width="200" show-overflow-tooltip />
        <el-table-column label="提问时间" prop="createTime" width="170" align="center" />
        <el-table-column label="提问人" prop="nickname" width="120" align="center" show-overflow-tooltip />
        <el-table-column label="答复状态" align="center" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.hasTeacherAnswer === 1 ? 'success' : 'warning'"
              effect="light"
            >
              {{ scope.row.hasTeacherAnswer === 1 ? '已解答' : '待解答' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="回复数" prop="answerCount" width="80" align="center" />
        <el-table-column label="最新回复时间" prop="updateTime" width="170" align="center" />
        <el-table-column label="操作" align="center" width="120" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              :icon="ChatDotRound"
              @click="handleDetail(scope.row)"
            >
              解答/详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 问答详情抽屉 (参考 QaDetailDrawer.vue 改进) -->
    <el-drawer
      v-model="drawerVisible"
      title="问题详情"
      size="500px"
      destroy-on-close
      @closed="handleClosed"
    >
      <div v-loading="detailLoading" class="qa-detail-wrapper">
        <template v-if="currentDetail">
          <!-- Question Section -->
          <div class="question-header">
            <h2 class="q-title">{{ currentDetail.title }}</h2>
            <div class="q-meta">
              <el-avatar :size="28" :src="currentDetail.avatar || defaultAvatar" />
              <span class="q-user">{{ currentDetail.nickname || currentDetail.username || '匿名用户' }}</span>
              <span class="q-time">{{ currentDetail.createTime }}</span>
            </div>
            <div class="q-content" v-html="currentDetail.content"></div>
          </div>

          <el-divider class="divider" />

          <!-- Answer Actions -->
          <div class="answer-action-bar">
            <span class="answer-count">共 {{ currentDetail.answerCount || 0 }} 个回答</span>
            <el-button type="primary" @click="showAnswerInput = !showAnswerInput" size="small" plain>解答问题</el-button>
          </div>

          <!-- First Level Answer Input Box -->
          <div v-if="showAnswerInput" class="answer-input-box">
            <el-input
              v-model="replyForm.content"
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
          <div class="answers-list" v-if="currentDetail.answers && currentDetail.answers.length > 0">
            <div v-for="ans in currentDetail.answers" :key="ans.id" class="answer-item">
              <div class="ans-meta">
                <el-avatar :size="32" :src="ans.avatar || defaultAvatar" class="ans-avatar" />
                <div class="ans-info">
                  <div class="ans-name-line">
                    <span class="ans-user">{{ ans.nickname || ans.username || '匿名用户' }}</span>
                    <el-tag v-if="ans.isTeacher === 1" size="small" type="success" class="teacher-tag">讲师</el-tag>
                  </div>
                  <div class="ans-time">{{ ans.createTime }}</div>
                </div>
              </div>
              
              <div class="ans-content" v-html="ans.content"></div>
              
              <div class="ans-actions">
                <span class="action-btn" @click="toggleReplies(ans)">
                  <el-icon><ChatDotRound /></el-icon> {{ (ans.replyTimes || ans.replies?.length) || '评论' }}
                </span>
                <!-- 讲师端可以考虑增加删除能力，若接口支持 -->
              </div>

              <!-- Nested Replies (Expanded) -->
              <div v-if="ans._showReplies" class="replies-wrapper">
                <div class="replies-list" v-loading="ans._repliesLoading">
                  <!-- 直接使用详情接口返回的一并带出的 replies，不需要二次加载 -->
                  <template v-if="ans.replies && ans.replies.length > 0">
                    <div class="reply-item" v-for="reply in ans.replies" :key="reply.id">
                      <div class="reply-meta-line">
                        <span class="reply-user" :class="{ 'is-teacher': reply.isTeacher === 1 }">
                          {{ reply.nickname || reply.username || '匿名用户' }}
                          <el-tag v-if="reply.isTeacher === 1" size="mini" type="success" effect="plain" style="margin-right: 4px; transform: scale(0.85)">讲师</el-tag>
                        </span>
                        
                        <template v-if="reply.targetUserId && reply.targetReplyId !== ans.id">
                           <span class="reply-to-text">回复</span>
                           <span class="reply-user target">{{ reply.targetNickname }}</span>
                        </template>
                        
                        <span class="reply-content-text">：<span v-html="reply.content"></span></span>
                      </div>
                      
                      <div class="reply-actions">
                        <span class="r-time">{{ reply.createTime }}</span>
                        <span class="action-btn r-btn" @click="openReplyInput(ans, reply)">
                          回复
                        </span>
                      </div>
                    </div>
                  </template>
                  <div v-else class="no-replies">暂无评论</div>
                </div>
                
                <!-- Reply Input Box -->
                <div class="reply-input-wrapper">
                  <el-input
                    v-model="ans._replyContent"
                    size="small"
                    :placeholder="ans._replyTarget ? `回复 ${ans._replyTarget.nickname || ans._replyTarget.username}` : '写下你的评论...'"
                    @keyup.enter="submitSubReply(ans)"
                  >
                    <template #append>
                      <el-button :loading="ans._submitting" @click="submitSubReply(ans)">发表</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, ChatDotRound } from '@element-plus/icons-vue'
import { getQuestionPage, getQuestionDetail, answerQuestion, getAnswerReplies } from '@/api/qa'
import { getCourseList } from '@/api/course'

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  courseId: '',
  hasTeacherAnswer: ''
})

const queryForm = ref(null)
const loading = ref(false)
const questionList = ref([])
const total = ref(0)
const courseOptions = ref([])

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await getQuestionPage(queryParams)
    questionList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取提问列表失败', error)
  } finally {
    loading.value = false
  }
}

// 获取讲师课程列表用于下拉筛选
const loadCourses = async () => {
  try {
    const res = await getCourseList({ pageNum: 1, pageSize: 1000 })
    courseOptions.value = res.records || []
  } catch (error) {
    console.error('获取课程列表框失败', error)
  }
}

const handleQuery = () => {
  queryParams.page = 1
  getList()
}

const resetQuery = () => {
  if (queryForm.value) {
    queryForm.value.resetFields()
  }
  handleQuery()
}

// 详情抽屉状态
const drawerVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 一级回答功能
const showAnswerInput = ref(false)
const submitLoading = ref(false)
const replyForm = reactive({
  questionId: null,
  content: ''
})

// 查看详情 (每次开抽屉)
const handleDetail = async (row) => {
  drawerVisible.value = true
  currentDetail.value = null
  detailLoading.value = true
  showAnswerInput.value = false
  replyForm.content = ''
  replyForm.questionId = row.id 
  
  try {
    const res = await getQuestionDetail(row.id)
    if (res && res.answers) {
      res.answers.forEach(ans => {
        ans._showReplies = false // 默认不展开
        ans._repliesLoaded = false // 标记是否加载过二级评论
        ans._repliesLoading = false
        ans._replyContent = ''
        ans._replyTarget = null
        ans._submitting = false
        ans.replies = [] // 清空/初始化
      })
    }
    currentDetail.value = res
  } catch (error) {
    console.error('获取问题详情失败', error)
  } finally {
    detailLoading.value = false
  }
}

const handleClosed = () => {
  currentDetail.value = null
  showAnswerInput.value = false
  replyForm.content = ''
}

// 刷新详情 (提交回复后)
const refreshDetail = async () => {
  if (!replyForm.questionId) return
  detailLoading.value = true
  try {
    const res = await getQuestionDetail(replyForm.questionId)
    // 保持各一级回答评论框的可见状态与内容（如果有的话），这里图简单重置。真实项目可能要做状态保留
    if (res && res.answers) {
      // 保持之前的展开状态和已加载的评论，如果在真实项目中应做更复杂的 Diff
      res.answers.forEach(ans => {
        const oldAns = currentDetail.value?.answers?.find(a => a.id === ans.id)
        if (oldAns) {
          ans._showReplies = oldAns._showReplies
          ans._repliesLoaded = oldAns._repliesLoaded
          ans.replies = oldAns.replies || []
        } else {
          ans._showReplies = false
          ans._repliesLoaded = false
          ans.replies = []
        }
        ans._replyContent = ''
        ans._replyTarget = null
      })
    }
    currentDetail.value = res
    getList()
  } catch (error) {
    console.error('刷新问题详情失败', error)
  } finally {
    detailLoading.value = false
  }
}

// 提交一级回答
const submitFirstLevelAnswer = async () => {
  if (!replyForm.content.trim()) {
    ElMessage.warning('回答内容不能为空')
    return
  }
  submitLoading.value = true
  try {
    const reqData = {
      questionId: replyForm.questionId,
      answerId: 0, 
      content: replyForm.content,
      targetReplyId: 0,
      targetUserId: 0
    }
    await answerQuestion(reqData)
    ElMessage.success('回答成功')
    replyForm.content = ''
    showAnswerInput.value = false
    refreshDetail()
  } catch (error) {
    console.error('回答失败', error)
  } finally {
    submitLoading.value = false
  }
}

// 展开/收起二级评论并懒加载
const toggleReplies = async (ans) => {
  ans._showReplies = !ans._showReplies
  
  if (ans._showReplies && !ans._repliesLoaded) {
    ans._repliesLoading = true
    try {
      const res = await getAnswerReplies(ans.id)
      ans.replies = res.data || res || []
      ans._repliesLoaded = true
    } catch (e) {
      console.error('获取二级评论失败', e)
    } finally {
      ans._repliesLoading = false
    }
  }
}

// 准备二级回复记录 (点击子回复的"回复"按钮)
const openReplyInput = (ans, replyTarget) => {
  ans._replyTarget = replyTarget
}

// 提交二级评论
const submitSubReply = async (ans) => {
  if (!ans._replyContent.trim()) {
    return ElMessage.warning('评论内容不能为空')
  }
  ans._submitting = true
  
  const targetReplyId = ans._replyTarget ? ans._replyTarget.id : 0
  const targetUserId = ans._replyTarget ? ans._replyTarget.userId : ans.userId

  try {
    await answerQuestion({
      questionId: replyForm.questionId,
      answerId: ans.id, 
      content: ans._replyContent,
      targetReplyId: targetReplyId,
      targetUserId: targetUserId
    })
    ElMessage.success('评论成功')
    ans._replyContent = ''
    ans._replyTarget = null
    // 重新获取当前二级评论列表，不需要全局刷新
    ans._repliesLoaded = false
    ans._showReplies = false
    toggleReplies(ans)
  } catch (e) {
    console.error(e)
  } finally {
    ans._submitting = false
  }
}


onMounted(() => {
  loadCourses()
  getList()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

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
  font-size: 14px;
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
.answers-list {
  padding-bottom: 30px;
}
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
  font-size: 14px;
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
  &.is-teacher {
    color: #67C23A;
  }
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
