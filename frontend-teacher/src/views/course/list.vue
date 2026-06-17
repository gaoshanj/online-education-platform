<template>
  <div class="course-list-container">
    <el-card class="admin-card">

      <!-- 顶部过滤 -->
      <el-card class="filter-card" shadow="never" style="margin-bottom: 20px;">
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

      <!-- 表格 -->
      <el-table v-loading="loading" :data="courseList" style="width: 100%" border>
        <el-table-column label="封面" width="120" align="center">
          <template #default="{ row }">
            <el-image :src="row.coverImage" style="width: 80px; height: 45px; border-radius: 4px" fit="cover">
              <template #error>
                <div class="image-placeholder flex-center"><el-icon>
                    <Picture />
                  </el-icon></div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="课程名称" prop="courseName" show-overflow-tooltip align="center" />
        <el-table-column label="分类" prop="categoryName" width="120" align="center" />
        <el-table-column label="报名人数" prop="studentCount" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="精品" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRecommend === 1 ? 'success' : 'info'" size="small">
              {{ row.isRecommend === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="info" @click="openDetail(row.id)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)"
              :disabled="row.status === 1 || row.status === 2">编辑</el-button>
            <el-button link type="success" @click="handleSubmitAudit(row)"
              v-if="[0, 3, 4].includes(row.status)">提交审核</el-button>
            <el-button link type="warning" @click="handleOffline(row)" v-if="row.status === 2">下架</el-button>
            <!-- <el-popconfirm title="确定要删除该草稿吗？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" v-if="row.status === 0">删除</el-button>
              </template>
            </el-popconfirm> -->
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
          @size-change="getList" @current-change="getList" />
      </div>
    </el-card>

    <!-- 课程详情侧边抽屉 -->
    <el-drawer v-model="drawerVisible" title="课程详情" size="600px" destroy-on-close>
      <div class="drawer-content" v-loading="detailLoading">
        <template v-if="currentDetail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="课程名称">{{ currentDetail.courseName }}</el-descriptions-item>
            <el-descriptions-item label="讲师名称">{{ currentDetail.teacherName }}</el-descriptions-item>
            <el-descriptions-item label="所属分类">{{ currentDetail.categoryName || '无' }}</el-descriptions-item>
            <el-descriptions-item label="课程状态">
              <el-tag :type="getStatusType(currentDetail.status)" size="small">
                {{ getStatusLabel(currentDetail.status) }}
              </el-tag>
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
            <el-descriptions-item label="好评率(4星+)">
              {{ currentDetail.goodReviewRate != null ? Number(currentDetail.goodReviewRate).toFixed(2) + '%' : '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="浏览次数">{{ currentDetail.viewCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="报名人数">{{ currentDetail.studentCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="点赞数">{{ currentDetail.likeCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="收藏数">{{ currentDetail.favoriteCount || 0 }}</el-descriptions-item>
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
                    <span class="video-duration" v-if="data.videoDuration">视频时长: {{ formatDuration(data.videoDuration)
                      }}</span>
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
    <el-dialog v-model="previewVisible" :title="'预览: ' + previewTitle" width="640px" destroy-on-close
      @closed="handlePreviewClose">
      <div class="video-container">
        <video v-if="previewUrl" :src="previewUrl" controls autoplay controlslist="nodownload"
          class="preview-video"></video>
        <el-empty v-else description="暂无视频源" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Picture, VideoPlay } from '@element-plus/icons-vue'
import { getCourseList, deleteCourse, submitAudit, offlineCourse, getCourseById } from '@/api/course'
import { getChapterTree } from '@/api/chapter'
import { getCategoryTree } from '@/api/category'

const router = useRouter()
const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const categoryOptions = ref([])

const queryParams = reactive({
  page: 1,
  size: 10,
  courseName: '',
  categoryId: undefined,
  status: undefined,
  isRecommend: undefined,
  orderBy: undefined
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getCourseList(queryParams)
    courseList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.page = 1
  getList()
}

const resetQuery = () => {
  queryParams.courseName = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.isRecommend = undefined
  queryParams.orderBy = undefined
  handleSearch()
}

const handleEdit = (row) => {
  router.push(`/course/edit/${row.id}`)
}

const handleSubmitAudit = async (row) => {
  try {
    await ElMessageBox.confirm('确定提交审核吗？提交后在审核期间将无法修改。', '提示', { type: 'warning' })
    await submitAudit(row.id)
    ElMessage.success('已提交审核')
    getList()
  } catch (e) { }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm('确定下架该课程吗？下架后学员将无法报名学习。', '提示', { type: 'warning' })
    await offlineCourse(row.id)
    ElMessage.success('已下架')
    getList()
  } catch (e) { }
}

const handleDelete = async (row) => {
  try {
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) { }
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = { 0: '草稿', 1: '待审核', 2: '已发布', 3: '审核拒绝', 4: '已下架' }
  return map[status] || '未知'
}

// ——— 抽屉：课程详情与章节树 ———
const drawerVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)
const chapterTree = ref([])

const openDetail = async (courseId) => {
  drawerVisible.value = true
  detailLoading.value = true
  try {
    const [detailRes, treeRes] = await Promise.all([
      getCourseById(courseId),
      getChapterTree(courseId)
    ])
    // 适配不同的包装返回
    currentDetail.value = detailRes.data || detailRes || null
    chapterTree.value = treeRes.data || treeRes || []
  } catch (error) {
    console.error('获取详情失败', error)
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

const openVideoPreview = (url, title) => {
  if (!url) {
    ElMessage.warning('视频链接不存在')
    return
  }
  previewUrl.value = url
  previewTitle.value = title || '附带视频'
  previewVisible.value = true
}

const handlePreviewClose = () => {
  previewUrl.value = ''
}

const formatDuration = (seconds) => {
  if (!seconds) return '0秒'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60

  let res = ''
  if (h > 0) res += `${h}小时`
  if (m > 0) res += `${m}分`
  if (s > 0 || res === '') res += `${s}秒`
  return res
}

const fetchCategoryOptions = async () => {
  try {
    const res = await getCategoryTree()
    categoryOptions.value = res.data || res || []
  } catch (error) {
    categoryOptions.value = []
  }
}

onMounted(() => {
  fetchCategoryOptions()
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.search-form {
  margin-bottom: 20px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
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

:deep(.el-tree-node__content) {
  height: 40px;
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
