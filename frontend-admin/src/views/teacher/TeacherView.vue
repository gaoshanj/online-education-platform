<template>
  <div class="teacher-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="讲师名称">
          <el-input v-model="queryParams.name" placeholder="请输入讲师名称" clearable />
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
        <!-- <el-table-column label="头像" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar" :icon="UserFilled" />
          </template>
</el-table-column> -->
        <el-table-column prop="nickname" label="讲师昵称" min-width="120" align="center">
        </el-table-column>
        <!-- <el-table-column prop="email" label="邮箱" min-width="140" show-overflow-tooltip /> -->
        <el-table-column prop="courseCount" label="课程总数" width="100" align="center"  />
        <el-table-column prop="studentCount" label="学生总数" width="100" align="center"  />
        <el-table-column prop="viewCount" label="浏览总数" width="100" align="center"  />
        <el-table-column prop="reviewCount" label="评价总数" width="100" align="center" />
        <el-table-column prop="avgScore" label="平均评分" width="100" align="center"  />
        <el-table-column prop="likeCount" label="点赞总数" width="100" align="center"  />
        <el-table-column prop="favoriteCount" label="收藏总数" width="100" align="center"  />
        <el-table-column prop="createTime" label="注册时间" width="170" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">详情</el-button>
            <el-button type="success" link size="small" @click="openCourses(row.id)">查看课程</el-button>
            <el-button type="danger" link size="small" @click="handleRemoveRole(row)">取消身份</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total"
          :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchList" />
      </div>
    </el-card>

    <!-- 讲师详情信息对话框 -->
    <el-dialog v-model="dialogVisible" title="讲师详细资料" width="600px" destroy-on-close>
      <div v-loading="detailLoading" class="detail-wrap">
        <el-descriptions v-if="currentDetail" :column="2" border>
          <el-descriptions-item label="用户名">{{ currentDetail.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentDetail.nickname }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentDetail.email || '无' }}</el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag :type="currentDetail.status === 1 ? 'success' : 'danger'" size="small">
              {{ currentDetail.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程总数">{{ currentDetail.courseCount }}</el-descriptions-item>
          <el-descriptions-item label="学生总数">{{ currentDetail.studentCount }}</el-descriptions-item>
          <el-descriptions-item label="累计浏览量">{{ currentDetail.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="评价总数">{{ currentDetail.reviewCount }}</el-descriptions-item>
          <el-descriptions-item label="平均评分">{{ currentDetail.avgScore }}</el-descriptions-item>
          <el-descriptions-item label="点赞总数">{{ currentDetail.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="收藏总数">{{ currentDetail.favoriteCount }}</el-descriptions-item>
          <el-descriptions-item label="入驻时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 所发课程抽屉 -->
    <el-drawer v-model="drawerVisible" title="该讲师所有课程" size="600px" append-to-body destroy-on-close>
      <div class="drawer-content" v-loading="courseLoading">
        <template v-if="courseList.length > 0">
          <div class="course-list">
            <el-card v-for="course in courseList" :key="course.id" shadow="hover" class="course-card">
              <div class="c-left">
                <el-image :src="course.coverImage" class="c-cover" fit="cover">
                  <template #error>
                    <div class="img-error"><el-icon>
                        <Picture />
                      </el-icon></div>
                  </template>
                </el-image>
              </div>
              <div class="c-right">
                <div class="c-title">{{ course.courseName }}</div>
                <div class="c-meta">分类：{{ course.categoryName }}</div>
                <div class="c-stats">
                  <span><el-icon>
                      <User />
                    </el-icon> {{ course.studentCount }}</span>
                  <span><el-icon>
                      <View />
                    </el-icon> {{ course.viewCount }}</span>
                </div>
                <!-- 底部状态 -->
                <div class="c-status">
                  <el-tag v-if="course.status === 0" type="info" size="small">草稿</el-tag>
                  <el-tag v-else-if="course.status === 1" type="warning" size="small">待审核</el-tag>
                  <el-tag v-else-if="course.status === 2" type="success" size="small">已发布</el-tag>
                  <el-tag v-else-if="course.status === 3" type="danger" size="small">已拒绝</el-tag>
                  <el-tag v-else-if="course.status === 4" type="info" size="small">已下架</el-tag>
                </div>
              </div>
            </el-card>
          </div>
        </template>
        <el-empty v-else description="该讲师暂未发布任何课程" :image-size="80" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, UserFilled, Picture, View, User } from '@element-plus/icons-vue'
import {
  getTeacherPage,
  getTeacherDetail,
  getTeacherCourses,
  removeTeacherRole
} from '@/api/teacher'

// ——— 查询与列表 ———
const queryParams = reactive({
  name: '',
  page: 1,
  size: 10
})

const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getTeacherPage(queryParams)
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
  queryParams.name = ''
  handleSearch()
}

onMounted(() => {
  fetchList()
})

// ——— 详情对话框 ———
const dialogVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)

async function openDetail(id) {
  dialogVisible.value = true
  detailLoading.value = true
  try {
    const res = await getTeacherDetail(id)
    currentDetail.value = res.data
  } catch {
    currentDetail.value = null
  } finally {
    detailLoading.value = false
  }
}

// ——— 讲师发布课程抽屉 ———
const drawerVisible = ref(false)
const courseLoading = ref(false)
const courseList = ref([])

async function openCourses(id) {
  drawerVisible.value = true
  courseLoading.value = true
  try {
    const res = await getTeacherCourses(id)
    courseList.value = res.data || []
  } catch {
    courseList.value = []
  } finally {
    courseLoading.value = false
  }
}

// ——— 取消讲师身份操作 ———
async function handleRemoveRole(row) {
  try {
    await ElMessageBox.confirm(`确定要取消用户「${row.nickname}」的讲师身份吗？\n取消后该用户将只能作为普通学员使用平台。`, '敏感操作', {
      confirmButtonText: '确定取消',
      cancelButtonText: '放弃',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await removeTeacherRole(row.id)
    ElMessage.success('讲师身份已撤销')
    if (tableData.value.length === 1 && queryParams.page > 1) {
      queryParams.page--
    }
    fetchList()
  } catch { }
}
</script>

<style scoped>
.teacher-page {
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

.detail-wrap {
  min-height: 150px;
}

.drawer-content {
  padding: 0 16px 20px;
  background: #f8fbff;
  min-height: 100%;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-card {
  border-radius: 8px;
  border: none;
}

.course-card :deep(.el-card__body) {
  display: flex;
  padding: 12px;
  gap: 16px;
}

.c-left {
  flex-shrink: 0;
}

.c-cover {
  width: 140px;
  height: 90px;
  border-radius: 6px;
}

.img-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  color: #8fa8c8;
  font-size: 24px;
}

.c-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.c-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.c-meta {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.c-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.c-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.c-status {
  text-align: left;
}
</style>
