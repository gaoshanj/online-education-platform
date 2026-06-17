<template>
  <div class="question-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="课程">
          <el-select v-model="queryParams.courseId" placeholder="请选择所属课程" clearable filterable style="width: 200px">
            <el-option v-for="course in courseOptions" :key="course.id" :label="course.courseName" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="讲师回复">
          <el-select v-model="queryParams.hasTeacherAnswer" placeholder="全部" clearable style="width: 140px">
            <el-option label="已回复" :value="1" />
            <el-option label="未回复" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查 询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重 置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 主体表格 -->
    <el-card class="table-card" shadow="never">
      <!-- <el-alert
        title="警告：问答管理涉及用户生产内容。删除违规问题或违规回复为不可逆的级联操作，请谨慎处理。"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 16px;"
      /> -->

      <el-table v-loading="tableLoading" :data="tableData" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="提问标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="courseName" label="所属课程" min-width="140" show-overflow-tooltip />
        <el-table-column prop="nickname" label="提问者" width="120" />
        <el-table-column label="讲师已回复" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hasTeacherAnswer === 1 ? 'success' : 'info'" size="small">
              {{ row.hasTeacherAnswer === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerCount" label="回复总数" width="90" align="center" />
        <el-table-column prop="createTime" label="提问时间" width="170" align="center" />
        <el-table-column label="内容审核与操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">查看详情与回复</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteQuestion(row)">删除问题</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchList" />
      </div>
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="问答详情与评论审核" size="550px" append-to-body destroy-on-close
      @closed="handleClosed">
      <div class="drawer-content" v-loading="detailLoading">
        <template v-if="currentDetail">
          <!-- 主问题信息区 -->
          <div class="question-header">
            <h2 class="q-title">{{ currentDetail.title }}</h2>
            <div class="q-meta">
              <el-avatar :size="24" :src="currentDetail.avatar" :icon="UserFilled" />
              <span class="q-author">{{ currentDetail.nickname || currentDetail.username }}</span>
              <span class="q-course">来自课程：{{ currentDetail.courseName }}</span>
              <span class="q-time">{{ formatTime(currentDetail.createTime) }}</span>
            </div>
            <div class="q-content">{{ currentDetail.content }}</div>
          </div>

          <el-divider class="divider" content-position="left">
            回复列表 ({{ currentDetail.answerCount || 0 }})
          </el-divider>

          <!-- 回复列表区 -->
          <div v-if="currentDetail.answers && currentDetail.answers.length > 0" class="answers-list">
            <!-- 一级回答 -->
            <div class="answer-item" v-for="answer in currentDetail.answers" :key="answer.id">
              <div class="answer-main">
                <el-avatar :size="32" :src="answer.avatar" :icon="UserFilled" class="ans-avatar" />
                <div class="answer-body">
                  <div class="a-meta">
                    <span class="a-author" :class="{ 'is-teacher': answer.isTeacher }">
                      {{ answer.nickname || answer.username }}
                      <el-tag v-if="answer.isTeacher" type="danger" size="small" effect="dark"
                        class="teacher-tag">讲师</el-tag>
                    </span>
                    <span class="a-time">{{ formatTime(answer.createTime) }}</span>
                  </div>
                  <div class="a-content">{{ answer.content }}</div>

                  <!-- 后台操作操作区 -->
                  <div class="ans-actions">
                    <span class="action-btn" @click="toggleReplies(answer)">
                      <el-icon>
                        <ChatDotRound />
                      </el-icon> {{ (answer.replyTimes || answer.replies?.length) || '评论详情' }}
                    </span>
                    <span class="action-btn delete-btn" @click="handleDeleteAnswer(answer)">
                      <el-icon>
                        <Delete />
                      </el-icon> 删除该违规回答
                    </span>
                  </div>
                </div>
              </div>

              <!-- 楼层内部的追问/回复（二级评论懒加载区域） -->
              <div v-if="answer._showReplies" class="replies-wrapper">
                <div class="reply-list" v-loading="answer._repliesLoading">
                  <template v-if="answer._fetchedReplies && answer._fetchedReplies.length > 0">
                    <div class="reply-item" v-for="reply in answer._fetchedReplies" :key="reply.id">
                      <div class="reply-meta-line">
                        <span class="r-author" :class="{ 'is-teacher': reply.isTeacher }">
                          {{ reply.nickname || reply.username }}
                        </span>

                        <template v-if="reply.targetUserId && reply.targetReplyId !== answer.id">
                          <span class="reply-to-text">回复</span>
                          <span class="reply-user target">@{{ reply.targetNickname }}</span>
                        </template>

                        <span class="reply-content-text">：{{ reply.content }}</span>
                      </div>
                      <div class="reply-actions">
                        <span class="r-time">{{ formatTime(reply.createTime) }}</span>
                        <span class="action-btn delete-btn r-btn" @click="handleDeleteAnswer(reply, answer)">
                          删除违规评论
                        </span>
                      </div>
                    </div>
                  </template>
                  <div v-else-if="!answer._repliesLoading" class="no-replies">暂无评论记录</div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无任何回复" :image-size="80" />
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, UserFilled, ChatDotRound, Delete } from '@element-plus/icons-vue'
import { getCoursePage } from '@/api/course' // 共用获取所有课程用于下拉联动
import {
  getQuestionPage,
  getQuestionDetail,
  deleteQuestion,
  deleteAnswer,
  getReplies // 引入二级评论懒加载接口
} from '@/api/question'

// ——— 查询与列表 ———
const queryParams = reactive({
  courseId: undefined,
  hasTeacherAnswer: undefined,
  page: 1,
  size: 10
})

const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const courseOptions = ref([])

// 拉取所有课程
async function fetchCourseOpts() {
  try {
    const res = await getCoursePage({ page: 1, size: 1000 })
    courseOptions.value = res.data.records || []
  } catch (err) { }
}

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getQuestionPage(queryParams)
    tableData.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    tableLoading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  fetchList()
}

function resetQuery() {
  queryParams.courseId = undefined
  queryParams.hasTeacherAnswer = undefined
  handleSearch()
}

onMounted(() => {
  fetchCourseOpts()
  fetchList()
})

// ——— 提问级别操作 ———
async function handleDeleteQuestion(row) {
  try {
    await ElMessageBox.confirm(
      `确定要强制删除提问「${row.title}」吗？警告：该提问下的所有讲师和学生回答数据将一并被级联清理！`,
      '危险操作',
      {
        confirmButtonText: '强制删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
  } catch {
    return
  }

  try {
    await deleteQuestion(row.id)
    ElMessage.success('提问删除成功')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    fetchList()
  } catch { }
}

// ——— 详情与回答级别操作 ———
const drawerVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)
const currentQuestionId = ref(null)

async function openDetail(id) {
  currentQuestionId.value = id
  drawerVisible.value = true
  await fetchDetail(id)
}

const handleClosed = () => {
  currentDetail.value = null
  currentQuestionId.value = null
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

async function fetchDetail(id) {
  detailLoading.value = true
  try {
    const res = await getQuestionDetail(id)
    const data = res.data
    // 如果返回了答案，挂载前端用到的属性
    if (data && data.answers) {
      data.answers.forEach(ans => {
        ans._showReplies = false
        ans._repliesLoading = false
        ans._fetchedReplies = []
      })
    }
    currentDetail.value = data
  } catch {
    currentDetail.value = null
  } finally {
    detailLoading.value = false
  }
}

// ——— 展开二级评论懒加载 ———
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

// ——— 管理员删除层级违规回复 ———
async function handleDeleteAnswer(item, parentAns = null) {
  try {
    await ElMessageBox.confirm(
      '确定要删除此条不良发言吗？此操作不可逆，请核对。',
      '清理违规内容',
      {
        confirmButtonText: '强制删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
  } catch {
    return
  }

  try {
    await deleteAnswer(item.id)
    ElMessage.success('发言已清理')

    // 按场景采取局部/全局刷新策略
    if (parentAns) {
      // 这是在删除二级评论，只刷新当前评论列表即可防止抖动
      await loadReplies(parentAns)
      parentAns.replyTimes = Math.max(0, (parentAns.replyTimes || 0) - 1)
    } else {
      // 刷新整个详情页
      if (currentQuestionId.value) {
        fetchDetail(currentQuestionId.value)
      }
    }
    // 最后更新列表数据
    fetchList()
  } catch { }
}
</script>

<style scoped>
.question-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card,
.table-card {
  border-radius: 8px;
}

.filter-form .el-form-item {
  margin-bottom: 0;
  margin-right: 24px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.drawer-content {
  padding: 0 16px 20px;
}

/* 主问题样式区 */
.question-header {
  background: #f8fbff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
  border: 1px solid #e1eaf7;
}

.q-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #303133;
}

.q-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 12px;
}

.q-author {
  font-weight: bold;
}

.q-course {
  color: #8fa8c8;
}

.q-time {
  color: #909399;
}

.q-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 回复列表外围样式 */
.answers-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.answer-item {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 16px;
}

.answer-item:last-child {
  border-bottom: none;
}

.answer-main {
  display: flex;
  gap: 12px;
}

.ans-avatar {
  flex-shrink: 0;
}

.answer-body {
  flex: 1;
}

/* 回复标题数据流 */
.a-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.reply-meta-line {
  font-size: 13px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 6px;
  word-wrap: break-word;
}

.a-author {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 4px;
}

.r-author {
  font-weight: 500;
  color: #303133;
}

.a-author.is-teacher,
.r-author.is-teacher {
  color: #f56c6c;
}

.teacher-tag {
  zoom: 0.8;
}

.a-time,
.r-time {
  font-size: 12px;
  color: #909399;
}

.a-content,
.reply-content-text {
  font-size: 14px;
  line-height: 1.5;
  color: #606266;
  margin-bottom: 8px;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 操作按键及防嵌套样式 */
.ans-actions {
  display: flex;
  gap: 24px;
  margin-top: 10px;
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

.delete-btn:hover {
  color: #F56C6C;
}

/* 二级回复样式包裹区域 */
.replies-wrapper {
  margin-top: 16px;
  margin-left: 44px;
  /* 恰好躲开头像 */
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

.reply-to-text {
  margin: 0 4px;
  color: #909399;
}

.target {
  color: #409EFF;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.r-btn {
  font-size: 12px;
}

.no-replies {
  font-size: 13px;
  color: #909399;
  text-align: center;
  padding: 10px 0;
}
</style>
