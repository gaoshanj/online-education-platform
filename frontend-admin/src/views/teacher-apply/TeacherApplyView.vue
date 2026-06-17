<template>
  <div class="teacher-apply-page">
    <!-- 顶部过滤 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="filter-form">
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="修改待审核" :value="3" />
            <el-option label="已取消讲师资格" :value="4" />
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
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="education" label="学历" width="100" align="center" />
        <el-table-column prop="major" label="专业" min-width="140" show-overflow-tooltip />
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger" size="small">已拒绝</el-tag>
            <el-tag v-else-if="row.status === 3" type="warning" size="small">修改待审核</el-tag>
            <el-tag v-else-if="row.status === 4" type="info" size="small">已取消讲师资格</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 0 || row.status === 3" type="success" link size="small" @click="handleApprove(row.id)">通过</el-button>
            <el-button v-if="row.status === 0 || row.status === 3" type="danger" link size="small" @click="handleReject(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchList"
        />
      </div>
    </el-card>

    <!-- 申请详情侧边抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="讲师申请审核"
      size="600px"
      append-to-body
      destroy-on-close
    >
      <div class="drawer-content" v-loading="detailLoading">
        <template v-if="currentDetail">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="真实姓名">{{ currentDetail.realName }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ currentDetail.username }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ currentDetail.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentDetail.email }}</el-descriptions-item>
            <el-descriptions-item label="身份证号" :span="2">{{ currentDetail.idCard }}</el-descriptions-item>
            <el-descriptions-item label="学历">{{ currentDetail.education }}</el-descriptions-item>
            <el-descriptions-item label="专业">{{ currentDetail.major }}</el-descriptions-item>
            <el-descriptions-item label="申请时间" :span="2">{{ currentDetail.createTime }}</el-descriptions-item>
            <el-descriptions-item label="审核状态" :span="2">
              <el-tag v-if="currentDetail.status === 0" type="warning" size="small">待审核</el-tag>
              <el-tag v-else-if="currentDetail.status === 1" type="success" size="small">已通过</el-tag>
              <el-tag v-else-if="currentDetail.status === 2" type="danger" size="small">已拒绝</el-tag>
              <el-tag v-else-if="currentDetail.status === 3" type="warning" size="small">修改待审核</el-tag>
              <el-tag v-else-if="currentDetail.status === 4" type="info" size="small">已取消讲师资格</el-tag>
            </el-descriptions-item>
            <template v-if="[1, 2, 4].includes(currentDetail.status)">
              <el-descriptions-item label="审核人" :span="2">{{ currentDetail.auditorName || '系统' }}</el-descriptions-item>
              <el-descriptions-item label="审核时间" :span="2">{{ currentDetail.auditTime }}</el-descriptions-item>
            </template>
            <el-descriptions-item label="拒绝原因" :span="2" v-if="currentDetail.status === 2">
              <span style="color: #F56C6C">{{ currentDetail.rejectReason }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="教学经验" :span="2">
              <div class="desc-text">{{ currentDetail.teachingExperience || '无' }}</div>
            </el-descriptions-item>
          </el-descriptions>

          <el-divider content-position="left">资质证书</el-divider>
          <div class="cert-box">
            <template v-if="certList.length > 0">
              <el-image
                v-for="(url, index) in certList"
                :key="index"
                :src="url"
                class="cert-img"
                fit="cover"
                :preview-src-list="certList"
                :initial-index="index"
                preview-teleported
              >
                <template #error><div class="img-error"><el-icon><Picture /></el-icon></div></template>
              </el-image>
            </template>
            <el-empty v-else description="未上传证书" :image-size="60" />
          </div>

          <!-- 抽屉底部的审核操作栏 -->
          <div class="drawer-footer-actions" v-if="currentDetail.status === 0 || currentDetail.status === 3">
            <el-button type="success" @click="handleApprove(currentDetail.id)">审核通过</el-button>
            <el-button type="danger" @click="handleReject(currentDetail.id)">审核拒绝</el-button>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Picture } from '@element-plus/icons-vue'
import {
  getTeacherApplyPage,
  getTeacherApplyDetail,
  approveTeacherApply,
  rejectTeacherApply
} from '@/api/teacherApply'

// ——— 查询与列表 ———
const queryParams = reactive({
  status: undefined,
  page: 1,
  size: 10
})

const tableLoading = ref(false)
const tableData = ref([])
const total = ref(0)

async function fetchList() {
  tableLoading.value = true
  try {
    const res = await getTeacherApplyPage(queryParams)
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
  queryParams.status = undefined
  handleSearch()
}

onMounted(() => {
  fetchList()
})

// ——— 操作项 ———
async function handleApprove(id) {
  try {
    await ElMessageBox.confirm('确定通过该讲师申请吗？通过后该用户将获得讲师权限。', '审核确认', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      type: 'success'
    })
  } catch {
    return
  }

  try {
    await approveTeacherApply(id)
    ElMessage.success('已审核通过')
    drawerVisible.value = false
    fetchList()
  } catch {}
}

async function handleReject(id) {
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
    await rejectTeacherApply(id, reason)
    ElMessage.success('已驳回申请')
    drawerVisible.value = false
    fetchList()
  } catch {}
}

// ——— 抽屉：申请详情 ———
const drawerVisible = ref(false)
const detailLoading = ref(false)
const currentDetail = ref(null)

const certList = computed(() => {
  if (!currentDetail.value || !currentDetail.value.certificateUrls) return []
  return currentDetail.value.certificateUrls.split(',').filter(url => url.trim() !== '')
})

async function openDetail(id) {
  drawerVisible.value = true
  detailLoading.value = true
  try {
    const res = await getTeacherApplyDetail(id)
    currentDetail.value = res.data
  } catch {
    currentDetail.value = null
  } finally {
    detailLoading.value = false
  }
}
</script>

<style scoped>
.teacher-apply-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card, .table-card {
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
  padding: 0 4px 20px;
}

.desc-text {
  white-space: pre-wrap;
  word-break: break-all;
  color: #666;
  line-height: 1.5;
}

.cert-box {
  background: #f8fbff;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.cert-img {
  width: 120px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid #ddd;
  cursor: pointer;
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

.drawer-footer-actions {
  margin-top: 32px;
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>
