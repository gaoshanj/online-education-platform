<template>
  <div class="course-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-tree-select v-model="queryParams.categoryId" :data="categoryOptions"
            :props="{ label: 'categoryName', value: 'id', children: 'children' }" placeholder="请选择分类" check-strictly
            clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="已拒绝" :value="3" />
            <el-option label="已下架" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否精品">
          <el-select v-model="queryParams.isRecommend" placeholder="全部" clearable style="width: 100px">
            <el-option label="是" :value="1" />
            <el-option label="否" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-select v-model="queryParams.orderBy" placeholder="综合排序" clearable style="width: 120px">
            <el-option label="最新发布" value="time" />
            <el-option label="最高热度" value="hot" />
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
      <el-table v-loading="tableLoading" :data="tableData" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="封面" width="100" align="center">
          <template #default="{ row }">
            <el-image :src="row.coverImage" style="width: 60px; height: 40px; border-radius: 4px;" fit="cover"
              :preview-src-list="[row.coverImage]" preview-teleported>
              <template #error>
                <div class="img-error"><el-icon>
                    <Picture />
                  </el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="180"  show-overflow-tooltip />
        <el-table-column prop="teacherName" label="讲师" width="120" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info" size="small">草稿</el-tag>
            <el-tag v-else-if="row.status === 1" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="success" size="small">已发布</el-tag>
            <el-tag v-else-if="row.status === 3" type="danger" size="small">已拒绝</el-tag>
            <el-tag v-else-if="row.status === 4" type="info" size="small">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="精品" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRecommend === 1 ? 'success' : 'info'" size="small">
              {{ row.isRecommend === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 1" type="success" link size="small"
              @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 1" type="danger" link size="small" @click="handleReject(row)">拒绝</el-button>
            <template v-if="row.status === 2">
              <el-button v-if="row.isRecommend === 0 || !row.isRecommend" type="success" link size="small"
                @click="handleRecommend(row, 1)">设为精品</el-button>
              <el-button v-if="row.isRecommend === 1" type="info" link size="small"
                @click="handleRecommend(row, 0)">取消精品</el-button>
            </template>
            <el-button v-if="row.status === 2" type="warning" link size="small"
              @click="handleOffline(row)">下架</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchList" />
      </div>
    </el-card>

    <!-- 课程详情侧边抽屉 -->
    <el-drawer v-model="drawerVisible" title="课程详情" size="600px" append-to-body destroy-on-close>
      <div class="drawer-content" v-loading="detailLoading">
        <template v-if="currentDetail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="课程名称">{{ currentDetail.courseName }}</el-descriptions-item>
            <el-descriptions-item label="讲师名称">{{ currentDetail.teacherName }}</el-descriptions-item>
            <el-descriptions-item label="所属分类">{{ currentDetail.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="课程状态">
              <el-tag v-if="currentDetail.status === 0" type="info" size="small">草稿</el-tag>
              <el-tag v-else-if="currentDetail.status === 1" type="warning" size="small">待审核</el-tag>
              <el-tag v-else-if="currentDetail.status === 2" type="success" size="small">已发布</el-tag>
              <el-tag v-else-if="currentDetail.status === 3" type="danger" size="small">已拒绝</el-tag>
              <el-tag v-else-if="currentDetail.status === 4" type="info" size="small">已下架</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="是否精品课程">
              <el-tag :type="currentDetail.isRecommend ? 'success' : 'info'" size="small">
                {{ currentDetail.isRecommend ? '是' : '否' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="平均评分">
              <span style="color: #ff9900; font-weight: bold;">{{ currentDetail.avgScore != null ?
                Number(currentDetail.avgScore).toFixed(1) : '暂无' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="评价总数">{{ currentDetail.reviewCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="好评率">
              {{ currentDetail.goodReviewRate != null ? Number(currentDetail.goodReviewRate).toFixed(2) + '%' : '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="浏览次数">{{ currentDetail.viewCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="报名人数">{{ currentDetail.studentCount }}</el-descriptions-item>
            <el-descriptions-item label="点赞数">{{ currentDetail.likeCount }}</el-descriptions-item>
            <el-descriptions-item label="收藏数">{{ currentDetail.favoriteCount }}</el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="更新时间" :span="2">{{ currentDetail.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="拒绝原因" :span="2" v-if="currentDetail.status === 3">
              <span style="color: #F56C6C">{{ currentDetail.rejectReason }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="课程简介" :span="2">
              <div style="white-space: pre-wrap; word-break: break-all; color: #666;">
                {{ currentDetail.description || '无' }}
              </div>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">章节目录树</el-divider>
          <div class="chapter-tree-box">
            <el-tree v-if="chapterTree.length > 0" :data="chapterTree"
              :props="{ label: 'chapterName', children: 'children' }" default-expand-all>
              <template #default="{ node, data }">
                <div class="custom-tree-node">
                  <span>{{ node.label }}</span>
                  <div class="node-tags" v-if="data.parentId !== 0">
                    <el-tag v-if="data.isFree" type="success" size="small" effect="plain">试看</el-tag>
                    <span class="video-duration" v-if="data.videoDuration">视频时长: {{ data.videoDuration }}s</span>
                    <el-button v-if="data.videoUrl" type="primary" link size="small"
                      @click.stop="openVideoPreview(data.videoUrl, data.chapterName)">
                      <el-icon>
                        <VideoPlay />
                      </el-icon>预览
                    </el-button>
                  </div>
                </div>
              </template>
            </el-tree>
            <el-empty v-else description="暂无章节内容" :image-size="60" />
          </div>
        </template>
      </div>
    </el-drawer>

    <!-- 视频预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="'预览: ' + previewTitle" width="640px" append-to-body destroy-on-close
      @closed="handlePreviewClose">
      <div class="video-container">
        <video v-if="previewUrl" :src="previewUrl" controls autoplay controlslist="nodownload" class="preview-video">
          浏览器不支持 HTML5 视频播放
        </video>
        <el-empty v-else description="暂无视频源" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Picture, VideoPlay } from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/category'
import {
  getCoursePage,
  getCourseDetail,
  approveCourse,
  rejectCourse,
  offlineCourse,
  deleteCourse,
  getChapterTree,
  recommendCourse
} from '@/api/course'

const queryParams = reactive({
  courseName: '',
  categoryId: undefined,
  status: undefined,
  isRecommend: undefined,
  orderBy: undefined,
  page: 1,
  size: 10
})

const categoryOptions = ref([])
const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)

async function fetchCategoryOptions() {
  try {
    const res = await getCategoryList()
    categoryOptions.value = res.data || []
  } catch {
    categoryOptions.value = []
  }
}

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getCoursePage(queryParams)
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
  queryParams.courseName = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.isRecommend = undefined
  queryParams.orderBy = undefined
  handleSearch()
}

onMounted(() => {
  fetchCategoryOptions()
  fetchList()
})

// ——— 操作项 ———
async function handleApprove(row) {
  try {
    await ElMessageBox.confirm(`确定通过课程「${row.courseName}」的审核吗？`, '审核确认', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      type: 'success'
    })
  } catch {
    return
  }

  try {
    await approveCourse(row.id)
    ElMessage.success('已审核通过')
    fetchList()
  } catch { }
}

async function handleReject(row) {
  let reason = ''
  try {
    const res = await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '原因不能为空',
      inputPlaceholder: '必填：拒绝原因'
    })
    reason = res.value
  } catch {
    return
  }

  if (!reason) return

  try {
    await rejectCourse(row.id, reason)
    ElMessage.success('已驳回课程申请')
    fetchList()
  } catch { }
}

async function handleOffline(row) {
  try {
    await ElMessageBox.confirm(`确定强制下架该课程吗？下架后用户将无法学习该课程。`, '强制下架警告', {
      confirmButtonText: '强制下架',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await offlineCourse(row.id)
    ElMessage.success('已下架')
    fetchList()
  } catch { }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定彻底删除该课程数据吗？该操作不可逆！`, '危险操作', {
      confirmButtonText: '彻底删除',
      cancelButtonText: '取消',
      type: 'error'
    })
  } catch {
    return
  }

  try {
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    fetchList()
  } catch { }
}

async function handleRecommend(row, isRecommend) {
  const actionText = isRecommend === 1 ? '设为精品' : '取消精品'
  try {
    await ElMessageBox.confirm(`确定要将该课程${actionText}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await recommendCourse(row.id, isRecommend)
    ElMessage.success(`已${actionText}`)
    fetchList()
  } catch { }
}

// ——— 抽屉：课程详情与章节树 ———
const drawerVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)
const chapterTree = ref([])

async function openDetail(courseId) {
  drawerVisible.value = true
  detailLoading.value = true
  try {
    const [detailRes, treeRes] = await Promise.all([
      getCourseDetail(courseId),
      getChapterTree(courseId)
    ])
    currentDetail.value = detailRes.data
    chapterTree.value = treeRes.data || []
  } catch {
    currentDetail.value = null
    chapterTree.value = []
  } finally {
    detailLoading.value = false
  }
}

// ——— 视频预览逻辑 ———
const previewVisible = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')

function openVideoPreview(url, title) {
  if (!url) {
    ElMessage.warning('视频链接不存在')
    return
  }
  previewUrl.value = url
  previewTitle.value = title || '附带视频'
  previewVisible.value = true
}

function handlePreviewClose() {
  previewUrl.value = '' // 清空连接以彻底暂停视频并在下次加载前置空
}
</script>

<style scoped>
.course-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card,
.table-card {
  border-radius: 8px;
}

.filter-form .el-form-item {
  margin-bottom: 12;
  margin-right: 24px;
}

.img-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  color: #8fa8c8;
  font-size: 20px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.drawer-content {
  padding: 0 4px 20px;
}

.chapter-tree-box {
  background: #f8fbff;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  min-height: 100px;
}

.chapter-tree-box :deep(.el-tree-node__content) {
  height: 36px;
  margin-bottom: 2px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 15px;
  padding-right: 8px;
}

.node-tags {
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-duration {
  font-size: 12px;
  color: #909399;
}

.video-container {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #000;
  border-radius: 4px;
  overflow: hidden;
  min-height: 360px;
}

.preview-video {
  width: 100%;
  max-height: 60vh;
  outline: none;
}
</style>
