<template>
  <div class="course-audit-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="课程名称">
          <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-tree-select v-model="queryParams.categoryId" :data="categoryOptions"
            :props="{ label: 'categoryName', value: 'id', children: 'children' }" placeholder="请选择分类" check-strictly
            clearable style="width: 200px" @change="handleSearch" />
        </el-form-item>
        <!--移除了状态筛选，此页面固定请求 status=1 即待审核记录-->
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
        <el-table-column prop="courseName" label="课程名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="teacherName" label="提审讲师" width="140" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="createTime" label="提审时间" width="170" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">审批详情</el-button>
            <el-button type="success" link size="small" @click="handleApprove(row)">予以通过</el-button>
            <el-button type="danger" link size="small" @click="handleReject(row)">驳回申请</el-button>
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
    <el-drawer v-model="drawerVisible" title="上架审批详情信息" size="600px" append-to-body destroy-on-close>
      <div class="drawer-content" v-loading="detailLoading">
        <template v-if="currentDetail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="课程ID">{{ currentDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="课程名称">{{ currentDetail.courseName }}</el-descriptions-item>
            <el-descriptions-item label="讲师名称">{{ currentDetail.teacherName }}</el-descriptions-item>
            <el-descriptions-item label="所属分类">{{ currentDetail.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="课程状态">
              <el-tag type="warning" size="small">待审核</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="课程简介" :span="2">
              <div style="white-space: pre-wrap; word-break: break-all; color: #666;">
                {{ currentDetail.description || '无' }}
              </div>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">内嵌章节检阅</el-divider>
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
                      </el-icon>播放预览
                    </el-button>
                  </div>
                </div>
              </template>
            </el-tree>
            <el-empty v-else description="暂无提审内容结构" :image-size="60" />
          </div>
        </template>
      </div>
    </el-drawer>

    <!-- 视频审核预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="'视频内容审核: ' + previewTitle" width="640px" append-to-body destroy-on-close
      @closed="handlePreviewClose">
      <div class="video-container">
        <video v-if="previewUrl" :src="previewUrl" controls autoplay controlslist="nodownload" class="preview-video">
          浏览器不支持 HTML5 视频播放
        </video>
        <el-empty v-else description="暂无有效视频载体源" />
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
  getChapterTree
} from '@/api/course'

// ——— 查询与列表 ———
const queryParams = reactive({
  courseName: '',
  categoryId: undefined,
  status: 1, // 强绑定为待审核状态
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
  handleSearch()
}

onMounted(() => {
  fetchCategoryOptions()
  fetchList()
})

// ——— 操作项 ———
async function handleApprove(row) {
  try {
    await ElMessageBox.confirm(`确认通过课程「${row.courseName}」的审批吗？通过后将正式上线。`, '放行上架', {
      confirmButtonText: '通过上架',
      cancelButtonText: '取消',
      type: 'success'
    })
  } catch {
    return
  }

  try {
    await approveCourse(row.id)
    ElMessage.success('已审核通过，已上架到系统！')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    fetchList()
  } catch { }
}

async function handleReject(row) {
  let reason = ''
  try {
    const res = await ElMessageBox.prompt('请输入拒绝原因', '审批拒绝', {
      confirmButtonText: '确定驳回',
      cancelButtonText: '再想想',
      inputPattern: /\S+/,
      inputErrorMessage: '驳回原因不能为空',
      inputPlaceholder: '必填：拒绝原因'
    })
    reason = res.value
  } catch {
    return
  }

  if (!reason) return

  try {
    await rejectCourse(row.id, reason)
    ElMessage.success('已登记驳回这门课程的审批流')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
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

// ——— 审核视频预览逻辑 ———
const previewVisible = ref(false)
const previewUrl = ref('')
const previewTitle = ref('')

function openVideoPreview(url, title) {
  if (!url) {
    ElMessage.warning('未能拉取到视频文件链接')
    return
  }
  previewUrl.value = url
  previewTitle.value = title || '当前章节媒体'
  previewVisible.value = true
}

function handlePreviewClose() {
  previewUrl.value = ''
}
</script>

<style scoped>
.course-audit-page {
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
  background: #fdfbf8;
  /* 微缩黄以防与常规模版颜色雷同，增加审查感 */
  padding: 16px;
  border-radius: 6px;
  border: 1px dashed #e6a23c;
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
  background: #101014;
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
